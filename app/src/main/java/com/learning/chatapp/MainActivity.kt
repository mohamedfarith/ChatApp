package com.learning.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.learning.chatapp.presentation.chat.ChatScreen
import com.learning.chatapp.presentation.splash.SplashScreen
import com.learning.chatapp.presentation.chat.UserListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "splash") {
                composable("splash") {
                    SplashScreen {
                        navController.navigate("user_list") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
                composable("user_list") {
                    UserListScreen { selectedUser ->
                        navController.navigate("chat/${selectedUser.id}")
                    }
                }

                composable(
                    "chat/{otherUserId}",
                    arguments = listOf(
                        navArgument("otherUserId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val otherUserId = backStackEntry.arguments?.getString("otherUserId") ?: "P1"
                    ChatScreen( currentUserId = Constants.CURRENT_USER_ID, otherUserId = otherUserId)
                }
            }
        }
    }
}
