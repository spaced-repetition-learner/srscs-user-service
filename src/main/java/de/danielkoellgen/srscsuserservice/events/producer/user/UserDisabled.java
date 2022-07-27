package de.danielkoellgen.srscsuserservice.events.producer.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.danielkoellgen.srscsuserservice.domain.domainprimitives.EventDateTime;
import de.danielkoellgen.srscsuserservice.events.producer.AbstractProducerEvent;
import de.danielkoellgen.srscsuserservice.events.producer.user.dto.UserDisabledDto;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserDisabled extends AbstractProducerEvent {

    private final @NotNull UserDisabledDto payloadDto;

    public static final String eventName = "user-disabled";

    public static final String eventTopic = "cdc.users.0";

    public UserDisabled(@NotNull String transactionId, @NotNull UserDisabledDto payloadDto) {
        super(UUID.randomUUID(), transactionId, eventName, eventTopic,
                EventDateTime.makeFromLocalDateTime(LocalDateTime.now()));
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
        return "UserDisabled{" +
                "payloadDto=" + payloadDto +
                ", " + super.toString() +
                '}';
    }
}
