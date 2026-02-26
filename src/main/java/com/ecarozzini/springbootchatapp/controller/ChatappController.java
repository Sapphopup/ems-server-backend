package com.ecarozzini.springbootchatapp.controller;

import com.ecarozzini.springbootchatapp.DatabaseManager;
import com.ecarozzini.springbootchatapp.datatypes.Chatroom;
import com.ecarozzini.springbootchatapp.datatypes.Message;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.sql.ResultSet;
import java.util.Map;

@Controller
public class ChatappController {
    DatabaseManager dbMan = new DatabaseManager();
    @GetMapping("/logout")
    public ModelAndView logoutUser(ModelMap modelMap, HttpServletResponse response){
        System.out.println("logout method called");
        Cookie cookieId = new Cookie("loggedInUserid",null);
        cookieId.setMaxAge(0);
        response.addCookie(cookieId);
        Cookie cookieU = new Cookie("loggedInUsername",null);
        cookieU.setMaxAge(0);
        response.addCookie(cookieU);
        modelMap.addAttribute("Attribute", "redirectWithRedirectPrefix");
        modelMap.addAttribute("loggedIn", false);
        return new ModelAndView("redirect://localhost:8090/chatapppages/chatRedirect.html",modelMap);
    }
    @RequestMapping("/joinForum")
    public ModelAndView joinForum(HttpServletResponse response){
        String chatRoomId = "C0"; //this should be dynamic later on
        jakarta.servlet.http.Cookie cChatId = new Cookie("openChatroomId", chatRoomId);
        cChatId.setMaxAge(-1);
        response.addCookie(cChatId);

        Chatroom cRoom = dbMan.loadChatroom(chatRoomId);
        WebClient webClient = WebClient.create();
        System.out.println("Post request: "+webClient.post().uri("http://localhost:8090/chatHistoryPost")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cRoom.getChatHistory())
                .retrieve());
        return new ModelAndView("redirect:http://localhost:8090/chatapppages/chatRoom.html");
    }
}
