package com.learning.chatapp.data.remote.socket

import android.content.Context
import com.learning.chatapp.Constants
import com.learning.chatapp.data.local.entity.chat.MessageEntity
import com.learning.chatapp.domain.repository.chat.ChatRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class SocketManager @Inject constructor(
    private val repository: ChatRepository,
    private val connectivityObserver: ConnectivityObserver,
) : WebSocketListener() {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    private val _incomingMessages = MutableSharedFlow<MessageEntity>()
    val incomingMessages = _incomingMessages.asSharedFlow()

    private val unsentQueue = mutableListOf<MessageEntity>()

    private var isConnected = false
    private var onConnectedListener: (() -> Unit)? = null

    private var started = false

    fun startObserving(scope: CoroutineScope) {
        if (started) return
        started = true

        scope.launch {
            connectivityObserver.isConnected.collectLatest { hasNetwork ->
                if (hasNetwork && !isConnected) {
                    connect()
                }
            }
        }
    }


    fun connect() {
        val request = Request.Builder().url(Constants.SOCKET_URL).build()
        webSocket = client.newWebSocket(request, this)

    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnect")
        isConnected = false
    }


    fun setOnConnectedListener(listener: () -> Unit) {
        onConnectedListener = listener
    }

    fun sendMessage(messageEntity: MessageEntity): Boolean {
        if(isConnected.not()) connect()
        val json = """
            {
                "event": "private_message",
                "data": {
                    "id": "${messageEntity.id}",
                    "senderId": "${messageEntity.senderId}",
                    "receiverId": "${messageEntity.receiverId}",
                    "text": "${messageEntity.text}",
                    "timestamp": ${messageEntity.timestamp}
                }
            }
        """.trimIndent()

        return try {
            if (isConnected) {
                val result = webSocket?.send(json) ?: false
                if (!result) unsentQueue.add(messageEntity)
                result
            } else {
                unsentQueue.add(messageEntity)
                false
            }
        } catch (e: Exception) {
            unsentQueue.add(messageEntity)
            false
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        isConnected = true

        // Retry unsent messages
        val iterator = unsentQueue.iterator()
        while (iterator.hasNext()) {
            val msg = iterator.next()
            val success = sendMessage(msg)
            if (success) iterator.remove()
        }

        // Notify UI
        onConnectedListener?.invoke()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        try {
            val jsonObj = JSONObject(text)
            val event = jsonObj.optString("event")
            if (event == "private_message") {
                val data = jsonObj.getJSONObject("data")
                val msg = MessageEntity(
                    id = data.getString("id"),
                    senderId = data.getString("senderId"),
                    receiverId = data.getString("receiverId"),
                    text = data.getString("text"),
                    timestamp = System.currentTimeMillis()
                )
                CoroutineScope(Dispatchers.IO).launch {
                    repository.insertMessage(msg)
                    _incomingMessages.emit(msg)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        isConnected = false
        t.printStackTrace()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        isConnected = false
    }
}


