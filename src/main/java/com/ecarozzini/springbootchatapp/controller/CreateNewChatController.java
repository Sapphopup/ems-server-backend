package com.ecarozzini.springbootchatapp.controller;

import com.ecarozzini.springbootchatapp.DatabaseManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CreateNewChatController {
    DatabaseManager chatMan = new DatabaseManager();
    List<String> userIdsToAdd= new ArrayList<>();

    @RequestMapping("/cancelNewChat")
    ModelAndView cancelChatCreation(){
        userIdsToAdd.clear();
        return new ModelAndView("redirect:/chatapppages/chatApp.html");
    }

    @PostMapping("/addUserToChatList")
    public void insertUser(@RequestParam(value="userId")String uid){
        userIdsToAdd.add(uid);
    }

    @PostMapping("/removeUserFromChatList")
    public void removeUser(@RequestParam(value="userId")String uid){
        userIdsToAdd.remove(uid);
    }

    @PostMapping("/submitChat")
    public boolean createChat(@RequestParam(value="chatName")String name, List<String> userIdsToAdd){
        return createChat(userIdsToAdd,name);
    }

    public boolean createChat(List<String> members, String chatName){
        if(!chatMan.createNewChat(chatName)) {
            System.out.println("Failed to create chatroom");
            return false;
        }
        if(!chatMan.addUsersToChat(members,chatMan.getLastGeneratedChatID())){
            System.out.println("Failed to add members to created chat");
            return false;
        }
        return true;
    }
}