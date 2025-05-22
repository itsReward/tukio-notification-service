package com.tukio.notificationservice.config

import com.tukio.notificationservice.dto.NotificationRequestDTO
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.support.converter.RecordMessageConverter
import org.springframework.kafka.support.converter.StringJsonMessageConverter

@Configuration
class KafkaConfig {

    /**
     * Create Kafka topics
     */
    @Bean
    fun notificationTopic(): NewTopic {
        return TopicBuilder.name("notifications")
            .partitions(3)
            .replicas(1)
            .build()
    }

    @Bean
    fun eventReminderTopic(): NewTopic {
        return TopicBuilder.name("event-reminders")
            .partitions(3)
            .replicas(1)
            .build()
    }

    /**
     * Configure message converter for JSON payloads
     */
    @Bean
    fun messageConverter(): RecordMessageConverter {
        return StringJsonMessageConverter()
    }
}