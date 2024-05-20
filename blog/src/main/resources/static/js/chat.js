// chat.js
document.addEventListener('DOMContentLoaded', function () {
    const userInput = document.getElementById('user-input');
    const sendBtn = document.getElementById('send-btn');
    const chatHistory = document.getElementById('chat-history');

    sendBtn.addEventListener('click', function () {
        const message = userInput.value.trim();
        if (message) {
            // 清空输入框
            userInput.value = '';
            // 发送消息给AI并获取响应
            sendMessageToAI(message).then(response => {
                // 将AI的响应添加到聊天记录中
                appendMessageToHistory('AI', response);
            });
        }
    });

    userInput.addEventListener('keydown', function (event) {
        if (event.keyCode === 13) { // 当按下回车键时触发发送事件
            sendBtn.click();
        }
    });
});

function sendMessageToAI(message) {
    // 这里是调用讯飞星火API的代码，返回Promise对象
    // 你需要根据讯飞提供的文档来实现这个函数
    return new Promise((resolve, reject) => {
        // 假设这里有一个名为sendRequest的函数，用于发送请求到讯飞星火API
        sendRequest(message)
            .then(response => {
                // 处理响应数据
                const result = processResponse(response);
                resolve(result);
            })
            .catch(error => {
                reject(error);
            });
    });
}

function appendMessageToHistory(sender, message) {
    const messageDiv = document.createElement('div');
    messageDiv.classList.add(`message-${sender}`);
    messageDiv.textContent = message;
    chatHistory.appendChild(messageDiv);
}
