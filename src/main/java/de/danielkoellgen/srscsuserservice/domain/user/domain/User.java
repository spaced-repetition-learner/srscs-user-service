package de.danielkoellgen.srscsuserservice.domain.user.domain;

import de.danielkoellgen.srscsuserservice.domain.domainprimitives.MailAddress;
import de.danielkoellgen.srscsuserservice.domain.domainprimitives.Name;
import de.danielkoellgen.srscsuserservice.domain.domainprimitives.Username;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "user_by_username", columnList = "username", unique = true),
                @Index(name = "user_by_mailAddress", columnList = "mail_address", unique = true)
        }
)
@NoArgsConstructor
public class User {

    @Id
    @Getter
    @Column(name = "user_id")
    @Type(type = "uuid-char")
    private UUID userId = UUID.randomUUID();

    @Getter
    @Embedded
    @AttributeOverride(name = "username", column = @Column(
            name = "username", nullable = false, unique = true, length = 20))
    private Username username;

    @Getter
    @Embedded
    @AttributeOverride(name = "mailAddress", column = @Column (
            name = "mail_address", nullable = false, unique = true, length = 50))
    private MailAddress mailAddress;

    @Getter
    @Embedded
    @AttributeOverride(name = "name", column = @Column(
            name = "first_name", nullable = false, length = 20))
    private Name firstName;

    @Getter
    @Embedded
    @AttributeOverride(name = "name", column = @Column(
            name = "last_name", nullable = false, length = 20))
    private Name lastName;

    @Getter
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Transient
    private final Logger log = LoggerFactory.getLogger(User.class);


    public User (@NotNull Username username, @NotNull MailAddress mailAddress,
                 @NotNull Name firstName, @NotNull Name lastName) {
        this.username = username;
        this.mailAddress = mailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void disableUser() {
        isActive = false;
        log.debug("User.isActive has been set to {}.", isActive);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username=" + username.getUsername() +
                ", mailAddress=" + mailAddress.getMailAddress() +
                ", firstName=" + firstName.getName() +
                ", lastName=" + lastName.getName() +
                ", isActive=" + isActive +
                '}';
    }
}
