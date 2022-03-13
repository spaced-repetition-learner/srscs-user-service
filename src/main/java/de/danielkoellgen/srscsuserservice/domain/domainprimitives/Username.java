package de.danielkoellgen.srscsuserservice.domain.domainprimitives;

import de.danielkoellgen.srscsuserservice.domain.core.AbstractStringValidation;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Embeddable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Username extends AbstractStringValidation {

    public String username;

    public Username(@NotNull String username) throws Exception {
        validateOrThrowUsername(username);
        this.username = username;
    }

    private void validateOrThrowUsername(@NotNull String username) throws Exception {
        validateOrThrowMinLength(username, 4, this::mapUserException);
        validateOrThrowMaxLength(username, 16, this::mapUserException);
        validateOrThrowRegex(username, "^([A-Za-z0-9]){4,16}$", this::mapUserException);
    }

    private Exception mapUserException(String message) {
        return new UsernameException(message);
    }
}
