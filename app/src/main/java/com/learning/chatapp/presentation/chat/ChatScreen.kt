package com.learning.chatapp.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.learning.chatapp.Resource
import com.learning.chatapp.domain.models.MessageStatus
import com.learning.chatapp.formatTimestampToIST
import com.learning.chatapp.onError
import com.learning.chatapp.onLoading
import com.learning.chatapp.onSuccess
import com.learning.chatapp.presentation.chat.viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentUserId: String,
    otherUserId: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.observeAsState(initial = Resource.Loading)
    var text by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.fetchMessages()
    }

    messages.onSuccess { chatMessages ->

        LaunchedEffect(chatMessages.size) {
            if (chatMessages.isNotEmpty()) {
                listState.animateScrollToItem(chatMessages.lastIndex)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.Bottom // ensures bottom alignment
            ) {
                items(chatMessages) { message ->
                    val isCurrentUser = message.senderId == currentUserId

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
                    ) {
                        Column(horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = when {
                                                message.status == MessageStatus.FAILED -> Color.LightGray
                                                message.status == MessageStatus.SENT -> Color.Gray.copy(alpha = 0.3f)
                                                isCurrentUser -> Color(0xFFDCF8C6)
                                                else -> Color(0xFFEFEFEF)
                                            },
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(10.dp)
                                        .widthIn(max = 250.dp)
                                ) {
                                    Text(
                                        text = message.text,
                                        color = Color.Black
                                    )
                                }

                                if (isCurrentUser) {
                                    when (message.status) {
                                        MessageStatus.FAILED -> {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Retry",
                                                modifier = Modifier
                                                    .padding(start = 4.dp)
                                                    .size(20.dp)
                                                    .clickable {
                                                        viewModel.retryFailedMessages()
                                                    },
                                                tint = Color.Red
                                            )
                                        }

                                        MessageStatus.SENT -> {
                                            CircularProgressIndicator(
                                                modifier = Modifier
                                                    .padding(start = 6.dp)
                                                    .size(16.dp),
                                                strokeWidth = 2.dp
                                            )
                                        }

                                        else -> Unit
                                    }
                                }
                            }

                            Text(
                                text = formatTimestampToIST(message.timestamp),
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp, end = 4.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(0.dp, RoundedCornerShape(12.dp))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Type a message…") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF3F51B5),
                        unfocusedBorderColor = Color(0xFFBDBDBD)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (text.isNotBlank()) {
                            viewModel.sendMessage(text)
                            text = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF3F51B5), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }

        }
    }

    messages.onLoading {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    messages.onError { s, throwable ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error loading messages")
        }
    }
}



