package com.ecarozzini.springbootchatapp.configsandtools;

import com.ecarozzini.springbootchatapp.datatypes.Message;
import com.ecarozzini.springbootchatapp.datatypes.MsgType;

import com.ecarozzini.springbootchatapp.datatypes.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("loggedInUsername");
        if (username != null) {
            log.info("user disconnected: {}", username);
            var chatMessage = Message.builder()
                    .type(MsgType.SYSTEM_INFO)
                    .sender(new User("u0","Emily",new ArrayList<>()))
                    .build();
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }

}
