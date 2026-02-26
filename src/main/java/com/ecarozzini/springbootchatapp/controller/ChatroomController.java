package com.ecarozzini.springbootchatapp.controller;

import com.ecarozzini.springbootchatapp.DatabaseManager;
import com.ecarozzini.springbootchatapp.datatypes.Message;
import lombok.SneakyThrows;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;

@Controller
public class ChatroomController {
    DatabaseManager dbMan;
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(
            @Payload Message chatMessage
    ) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Message addUser(
            @Payload Message chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        // Add username in web socket session
        System.out.println(chatMessage.getSender().getUsername());
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender().getUsername());
        return chatMessage;
    }

    @ResponseBody
    @RequestMapping(value = "/postMessageToSpring", consumes = "application/json")
    @SneakyThrows
    String saveMessageToChatLogs(@RequestBody @Validated Message messageToSave, @CookieValue("openChatroomId") String chatId){
        dbMan = new DatabaseManager();
        System.out.println("Received payload: "+messageToSave.getContent());
        messageToSave.setTimeStamp(new Timestamp(Instant.now().toEpochMilli()));
        messageToSave.setMessageId(generateMessageId());
        messageToSave.setSender(dbMan.loadUser(messageToSave.getSenderId()));
        System.out.println("Sender: "+dbMan.loadUser(messageToSave.getSenderId())+" SenderId: "+messageToSave.getSenderId());
        System.out.println("Message content: "+messageToSave.getContent());
        dbMan.sendMessageToChatroom(messageToSave,chatId);
        return "ok";

    }
    @SneakyThrows
    String generateMessageId(){
        String query = "SELECT COUNT(messageID) AS count FROM Messages";
        ResultSet rs = dbMan.connectToDbAndQueryForData(query);
        if(rs.next()){
            return "M"+rs.getInt("count");
        }
        return null;
    }
}
