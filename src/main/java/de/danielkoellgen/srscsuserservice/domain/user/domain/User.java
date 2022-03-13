package de.danielkoellgen.srscsuserservice.domain.user.domain;

import de.danielkoellgen.srscsuserservice.domain.domainprimitives.MailAddress;
import de.danielkoellgen.srscsuserservice.domain.domainprimitives.Name;
import de.danielkoellgen.srscsuserservice.domain.domainprimitives.Username;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Type;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "user-by-username", columnList = "username", unique = true),
                @Index(name = "user-by-mailAddress", columnList = "mail-address", unique = true)
        }
)
public class User {

    @Id
    @Getter
    @Column(name = "user-id", nullable = false)
    @Type(type = "uuid-char")
    private final UUID userId = UUID.randomUUID();

    @Getter
    @Embedded
    @AttributeOverride(name = "username", column = @Column(
            name = "username", nullable = false, unique = true, length = 20))
    private Username username;

    @Getter
    @Embedded
    @AttributeOverride(name = "mailAddress", column = @Column (
            name = "mail-address", nullable = false, unique = true, length = 50))
    private MailAddress mailAddress;

    @Getter
    @Embedded
    @AttributeOverride(name = "name", column = @Column(
            name = "first-name", nullable = false, length = 20))
    private Name firstName;

    @Getter
    @Embedded
    @AttributeOverride(name = "name", column = @Column(
            name = "last-name", nullable = false, length = 20))
    private Name lastName;

    @Getter
    @Column(name = "is-active", nullable = false)
    private Boolean isActive = true;


    public User (@NotNull Username username, @NotNull MailAddress mailAddress,
                 @NotNull Name firstName, @NotNull Name lastName) {
        this.username = username;
        this.mailAddress = mailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Boolean disableUser() {
        isActive = false;
        return true;
    }
}
