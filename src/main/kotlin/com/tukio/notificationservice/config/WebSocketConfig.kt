package com.tukio.notificationservice.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

/**
 * Configuration for WebSocket support
 */
@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        // Set prefix for messages FROM the server TO the client
        registry.enableSimpleBroker("/topic", "/queue", "/user")

        // Set prefix for messages FROM the client TO the server
        registry.setApplicationDestinationPrefixes("/app")

        // Enable user destination prefixes
        registry.setUserDestinationPrefix("/user")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // Register the "/ws" endpoint, enabling the SockJS protocol
        // SockJS is used to enable fallback options for browsers that don't support websocket
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*") // In production, restrict this to your domain
            .withSockJS()
    }
}