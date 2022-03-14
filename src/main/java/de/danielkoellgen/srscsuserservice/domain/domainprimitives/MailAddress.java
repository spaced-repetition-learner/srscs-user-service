package de.danielkoellgen.srscsuserservice.domain.domainprimitives;

import de.danielkoellgen.srscsuserservice.domain.core.AbstractStringValidation;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Embeddable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MailAddress extends AbstractStringValidation {

    public String mailAddress;

    private static final String pattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";


    public MailAddress(@NotNull String mailAddress) throws Exception {
        validateMailAddressOrThrow(mailAddress);
        this.mailAddress = mailAddress;
    }

    private void validateMailAddressOrThrow(@NotNull String mailAddress) throws Exception {
        validateRegexOrThrow(mailAddress, pattern, this::mapToException);
    }

    private Exception mapToException(String message) {
        return new MailAddressException(message);
    }

    @Override
    public String toString() {
        return "MailAddress{" +
                "mailAddress='" + mailAddress + '\'' +
                '}';
    }
}
