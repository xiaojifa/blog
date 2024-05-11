<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>NSDA</title>
        <meta name="referrer" content="always">
        <meta name="apple-mobile-web-app-title" content="NSDA - 记录生活和工作"/>
        <meta name="keywords" content="NSDA 后端技术 前端技术 生活 学习 娱乐 游戏">
        <meta name="description" content="记录游戏和工作">
        <link rel="shortcut icon" href="/static/img/favicon.ico" type="image/x-icon">
        <link rel="icon" href="/static/img/favicon.ico" type="image/x-icon">
        <!-- ZUI 标准版压缩后的 CSS 文件 -->
        <link rel="stylesheet" href="//cdn.bootcdn.net/ajax/libs/zui/1.10.0/css/zui.min.css">
        <link rel="stylesheet" href="/css/common.css">
        <link rel="stylesheet" href="/css/zui-red.css">
        <link rel="stylesheet" href="/css/style.css">
        <!-- ZUI Javascript 依赖 jQuery -->
        <script src="//cdn.bootcdn.net/ajax/libs/zui/1.10.0/lib/jquery/jquery.js"></script>
        <!-- ZUI 标准版压缩后的 JavaScript 文件 -->
        <script src="//cdn.bootcdn.net/ajax/libs/zui/1.10.0/js/zui.min.js"></script>
<#--        <script src="/static/js/common.js"></script>-->
        <script src="/static/js/common.js"></script>
        <!-- first include any tsParticles plugin needed -->
        <script src="https://cdn.jsdelivr.net/npm/tsparticles/tsparticles.bundle.min.js"></script>
        <!-- then include jquery wrapper -->
        <script src="https://cdn.jsdelivr.net/npm/@tsparticles/jquery"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.2/sockjs.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
        <script>
            var stompClient = null;

            function connect() {
                var socket = new SockJS('/chat');
                stompClient = Stomp.over(socket);
                stompClient.connect({}, function (frame) {
                    console.log('Connected: ' + frame);
                    stompClient.subscribe('/topic/messages', function (message) {
                        showMessage(JSON.parse(message.body).content);
                    });
                });
            }

            function sendMessage() {
                var messageContent = document.getElementById('message').value;
                stompClient.send("/app/chat", {}, JSON.stringify({'content': messageContent}));
                document.getElementById('message').value = '';
            }

            function showMessage(message) {
                var messageArea = document.getElementById('messages');
                var messageElement = document.createElement('p');
                messageElement.textContent = message;
                messageArea.appendChild(messageElement);
            }
        </script>
    </head>
    <body style="background-color: #F5F5F5;">
        <script src="/js/particles.min.js"></script>
        <script src="/js/app.js"></script>
        <div id="particles-js"></div>
        <div class="container">