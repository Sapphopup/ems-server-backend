package com.ecarozzini.springbootchatapp.datatypes;

import lombok.Getter;

import java.util.List;

@Getter
public class Chatroom {
    String chatName;
    List<String> participantIds;
    List<Message> chatHistory;
    String chatId;


    public Chatroom(String cID, List<String> pIDs,List<Message> cH, String n){
        chatId = cID;
        participantIds = pIDs;
        chatHistory = cH;
        chatName = n;
    }
}
