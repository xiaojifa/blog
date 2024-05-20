<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AI聊天界面</title>
    <style>
        #chat-container {
            width: 400px;
            height: 600px;
            border: 1px solid #ccc;
            padding: 10px;
            overflow-y: scroll;
        }

        .user-message, .ai-message {
            margin-bottom: 10px;
        }

        .user-message {
            text-align: right;
        }

        .ai-message {
            text-align: left;
        }
    </style>
</head>
<body>
    <div id="chat-container">
        <div id="messages"></div>
        <input type="text" id="input-message" placeholder="输入消息...">
        <button id="send-button">发送</button>
    </div>

    <script>
        const chatContainer = document.getElementById('chat-container');
        const messages = document.getElementById('messages');
        const inputMessage = document.getElementById('input-message');
        const sendButton = document.getElementById('send-button');

        // 创建WebSocket连接
        const socket = new WebSocket('ws://localhost:62632/webSocket');

        // 监听WebSocket连接打开事件
        socket.addEventListener('open', (event) => {
            console.log('WebSocket连接已打开：', event);
        });

        // 监听WebSocket接收到消息事件
        socket.addEventListener('message', (event) => {
            const message = event.data;
            addMessageToChat(message, false);
        });

        // 监听发送按钮点击事件
        sendButton.addEventListener('click', () => {
            const message = inputMessage.value;
            if (message) {
                addMessageToChat(message, true);
                socket.send(message);
                inputMessage.value = '';
            }
        });

        // 将消息添加到聊天界面
        function addMessageToChat(message, isUser) {
            const messageElement = document.createElement('div');
            messageElement.classList.add(isUser ? 'user-message' : 'ai-message');
            messageElement.textContent = message;
            messages.appendChild(messageElement);
            chatContainer.scrollTop = chatContainer.scrollHeight;
        }
    </script>
</body>
</html>
