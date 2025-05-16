### ChatApp – Interview Assignment
A simple one-to-one real-time chat application demonstrating clean architecture, offline support, and Socket.IO integration using Jetpack Compose and Room.
#  How to Run

### Clone the repository

```bash
git clone https://github.com/yourname/ChatApp.git
cd ChatApp
```

### Open in Android Studio

Run the app on an emulator or physical device.

---

### Interact with PieSocket by sending this event via WebSocket:

```json
{
  "event": "private_message",
  "data": {
    "id": "32a2a35c-f42e-4524-9119-fcd803a93490",
    "senderId": "P1",
    "receiverId": "Me",
    "text": "Hi"
  }
}
```

> Replace `"P1"` with the other user ID (e.g., `P2`, `P3`) based on the app setup.

---

## 🧰 Libraries & Tools Used

| Purpose               | Library / Tool             |
|------------------------|-----------------------------|
| UI                     | Jetpack Compose             |
| Dependency Injection   | Hilt                        |
| Local Storage          | Room                        |
| Coroutines & Flows     | Kotlin Coroutines           |
| Real-time Messaging    |OkHttp WebSockets (via PieSocket)   |
| Architecture           | MVVM + Clean Architecture   |
| Testing                | JUnit, Mockito, Turbine     |

---

## 🗂️ Features Demonstrated

- Real-time one-to-one chat using PieSocket  
- Offline storage of messages using Room  
- Retry mechanism for failed messages  
- Clean, MVVM architecture  
- Jetpack Compose UI with elegant chat layout  
- Lightweight and maintainable code structure

## Here is the working video of the app
https://github.com/user-attachments/assets/7d5a94f4-bc90-4ce4-b3f1-b78723f17c21
