package com.theninjadev.taskflowapi.config;

import com.theninjadev.taskflowapi.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.WebSocketAnnotationMethodMessageHandler;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            var token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                var jwt = token.substring(7);

                if (!jwtService.isExpired(jwt) && "access".equals(jwtService.getTypeOfToken(jwt))) {
                    var userId = jwtService.getSubject(jwt);
                    var authentication = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                    accessor.setUser(authentication);
                }
            }
        }

        return message;
    }
}