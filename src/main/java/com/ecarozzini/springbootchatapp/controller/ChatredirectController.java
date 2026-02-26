package com.ecarozzini.springbootchatapp.controller;

import com.ecarozzini.springbootchatapp.DatabaseManager;
import com.ecarozzini.springbootchatapp.datatypes.Chatroom;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;

@Controller
public class ChatredirectController {
    DatabaseManager dbMan;
    List<Chatroom> usersChats;
    String loggedInUserId="";

    public ChatredirectController() {
        dbMan = new DatabaseManager();
    }


    @RequestMapping("/redirect")
    public ModelAndView redirection(
            @CookieValue(value = "loggedInUserid", defaultValue = "NoLoggedInUserHere") String userID
    ) {
        Pattern validUIdRegex = Pattern.compile("^U[0-9]+$");
        Matcher matcher = validUIdRegex.matcher(userID);
        System.out.println("called chatapp getmapping");
        ModelAndView mav;

        if (matcher.find()) {
            System.out.println("Logged in user found");
            System.out.println(userID);
            loggedInUserId=userID;
            usersChats = dbMan.loadChatrooms(userID);
            mav = new ModelAndView("redirect:/chatapppages/chatApp.html");
            mav.addObject("loggedIn", true);
            mav.addObject("userName",dbMan.loadUsername(userID));
            mav.addObject("Attribute", "redirectWithRedirectPrefix");
            System.out.println("redirecting to chatapp");
        }
        else{
            System.out.println("No logged in user found");
            mav = new ModelAndView("redirect:/chatapppages/chatRedirect.html");
            mav.addObject("Attribute", "redirectWithRedirectPrefix");
            System.out.println("redirecting to redirect page");
        }
        System.out.println(userID);
        System.out.println(mav.toString());
        return mav;
    }
}
