package de.danielkoellgen.srscsuserservice.events.producer.user.dto;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record UserDisabledDto(

    @NotNull UUID userId

) {
}
