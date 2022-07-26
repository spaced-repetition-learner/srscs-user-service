package de.danielkoellgen.srscsuserservice.events.producer.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.danielkoellgen.srscsuserservice.domain.domainprimitives.EventDateTime;
import de.danielkoellgen.srscsuserservice.events.producer.AbstractProducerEvent;
import de.danielkoellgen.srscsuserservice.events.producer.user.dto.UserCreatedDto;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserCreated extends AbstractProducerEvent {

    private final @NotNull UserCreatedDto payloadDto;

    public static final String eventName = "user-created";

    public static final String eventTopic = "cdc.users.0";

    public UserCreated(@NotNull String transactionId, @NotNull UserCreatedDto payloadDto) {
        super(UUID.randomUUID(), transactionId, eventName, eventTopic,
                EventDateTime.makeFromLocalDateTime(LocalDateTime.now())
        );
        this.payloadDto = payloadDto;
    }

    @Override
    public @NotNull String getSerializedContent() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        try {
            return objectMapper.writeValueAsString(payloadDto);
        } catch (Exception e) {
            throw new RuntimeException("ObjectMapper conversion failed.");
        }
    }

    @Override
    public String toString() {
        return "UserCreated{" +
                "payloadDto=" + payloadDto +
                ", " + super.toString() +
                '}';
    }
}
