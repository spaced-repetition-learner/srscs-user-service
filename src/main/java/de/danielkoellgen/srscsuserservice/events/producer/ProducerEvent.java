package de.danielkoellgen.srscsuserservice.events.producer;

import de.danielkoellgen.srscsuserservice.domain.domainprimitives.EventDateTime;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ProducerEvent {

    @NotNull UUID getEventId();

    @NotNull String getEventName();

    @NotNull String getTransactionId();

    @NotNull EventDateTime getOccurredAt();

    @NotNull String getTopic();

    @NotNull String getSerializedContent();
}
