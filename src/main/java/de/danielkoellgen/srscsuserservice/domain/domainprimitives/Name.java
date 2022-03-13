package de.danielkoellgen.srscsuserservice.domain.domainprimitives;

import de.danielkoellgen.srscsuserservice.domain.core.AbstractStringValidation;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Embeddable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Name extends AbstractStringValidation {

    public String name;

    public Name(@NotNull String name) throws Exception {
        validateOrThrowName(name);
        this.name = name;
    }

    private void validateOrThrowName(@NotNull String name) throws Exception {
        validateOrThrowMinLength(name, 3, this::mapToException);
        validateOrThrowMaxLength(name, 12, this::mapToException);
        validateOrThrowRegex(name, "^([A-Za-z0-9 ]){3,12}$", this::mapToException);
    }

    private Exception mapToException(String message) {
        return new NameException(message);
    }
}
