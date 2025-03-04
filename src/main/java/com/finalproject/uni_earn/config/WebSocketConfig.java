package com.finalproject.uni_earn.config;

import com.finalproject.uni_earn.service.impl.JwtServiceIMPL;
import com.finalproject.uni_earn.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtServiceIMPL jwtService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000") // Explicitly list your frontend origin
                .withSockJS(); // Enable SockJS fallback
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/reports");
        registry.enableSimpleBroker("/admins");
        registry.enableSimpleBroker("/topic", "/user"); // Enable broker for /topic and /user
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user"); // Important for user-specific destinations
    }
/*
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                List<String> authHeader = accessor.getNativeHeader("Authorization");
                if (authHeader != null && !authHeader.isEmpty()) {
                    String token = authHeader.get(0).replace("Bearer ", "");
                    try {
                        // Extract username and validate token using JwtUtil
                        String username = jwtUtil.extractUsername(token);
                        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                            UserDetails userDetails = jwtService.loadUserByUsername(username);

                            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                                // Create authentication token
                                UsernamePasswordAuthenticationToken authToken =
                                        new UsernamePasswordAuthenticationToken(
                                                userDetails,
                                                null,
                                                userDetails.getAuthorities()
                                        );

                                // Set authentication in SecurityContext
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                                accessor.setUser(authToken);

                                // Optional: Role-based check (e.g., only STUDENTS can receive notifications)
                                String role = jwtUtil.extractRole(token);
                                if (!"STUDENT".equals(role)) { // Adjust role as needed (e.g., "ADMIN")
                                    System.out.println("User role " + role + " is not authorized for notifications");
                                    return null; // Drop message if not a student
                                }
                            } else {
                                System.out.println("JWT Token validation failed");
                                return null;
                            }
                        }
                    } catch (io.jsonwebtoken.ExpiredJwtException e) {
                        System.out.println("JWT Token has expired: " + e.getMessage());
                        return null;
                    } catch (Exception e) {
                        System.out.println("JWT Validation failed: " + e.getMessage());
                        return null;
                    }
                }
                return message;
            }
        });
    }
*/
}
