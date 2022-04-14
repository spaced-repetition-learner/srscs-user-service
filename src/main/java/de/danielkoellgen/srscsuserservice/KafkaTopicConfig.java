package de.danielkoellgen.srscsuserservice;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    private final String bootstrapAddress;

    @Autowired
    public KafkaTopicConfig(@Value("${kafka.bootstrapAddress}") String bootstrapAddress) {
        this.bootstrapAddress = bootstrapAddress;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic sub_cdc_users_0() {
        return new NewTopic("cdc.users.0", 1, (short) 1);
    }
}
