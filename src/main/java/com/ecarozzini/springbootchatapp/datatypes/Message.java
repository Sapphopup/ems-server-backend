package com.ecarozzini.springbootchatapp.datatypes;

import java.sql.Timestamp;
import java.time.Instant;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Message {
    MsgType type;
    String content;
    User sender;
    String senderId;

    Timestamp timeStamp; //format: hh/mm/ss dd/mm/yy
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    String messageId;

    public Message(String mId, MsgType t, String c, User s, Timestamp tS){
        type = t;
        content = c;
        sender = s;
        timeStamp = tS;
        messageId = mId;

    }



    public Message(String mId, MsgType t, String c, User s){
        type = t;
        content = c;
        sender = s;
        timeStamp =  new Timestamp(Instant.now().toEpochMilli());
        messageId = mId;
    }
}
