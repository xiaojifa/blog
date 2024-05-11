package com.hzy.blog.controller;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/11 16:50
 */
@Controller
public class ChatController {
    @MessageMapping("/view/chat")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        return message;
    }
}
