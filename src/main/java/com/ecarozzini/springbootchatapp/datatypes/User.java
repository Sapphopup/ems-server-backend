package com.ecarozzini.springbootchatapp.datatypes;

import com.ecarozzini.springbootchatapp.DatabaseManager;
import jakarta.persistence.EmbeddedId;
import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Getter
public class User {
    DatabaseManager dbMan;
    String username;
    @EmbeddedId
    String userId;
    List<Chatroom> chatroomList;

    public User(String uId, String n, List<Chatroom> cL){
        username = n;
        userId = uId;
        chatroomList = cL;
    }
    @SneakyThrows
    public User(String username){
        dbMan= new DatabaseManager();
        this.username = username;
        String query  = "SELECT userID FROM Users WHERE userName = '"+username+"';";
        ResultSet rs  = dbMan.connectToDbAndQueryForData(query);
        if(rs.next()){
            this.userId = rs.getString("userID");
        }
        chatroomList = new ArrayList<>();
    }
}
