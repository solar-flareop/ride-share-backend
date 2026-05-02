package com.solarflare.rideservice.config;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topic.ride-requested}")
    private String topicRideRequested;

    @Value("${kafka.topic.ride-matched}")
    private String topicRideMatched;


    // Topic where Ride Service published ride request
    // Matching Service subscribers to this topic

    @Bean
    public NewTopic rideRequestedTopic(){
        return TopicBuilder.name(topicRideRequested)
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Topic where Matching Service publishes match results
    // Ride Service subscribers to this topic

    @Bean
    public NewTopic rideMatchedTopic(){
        return TopicBuilder.name(topicRideMatched)
                .partitions(3)
                .replicas(1)
                .build();
    }


}
