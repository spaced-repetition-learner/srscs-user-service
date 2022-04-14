package de.danielkoellgen.srscsuserservice.events.producer.user.dto;

import de.danielkoellgen.srscsuserservice.domain.user.domain.User;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record UserCreatedDto(

    @NotNull UUID userId,

    @NotNull String username

) {
    public UserCreatedDto(@NotNull User user) {
        this(user.getUserId(), user.getUsername().getUsername());
    }
}
