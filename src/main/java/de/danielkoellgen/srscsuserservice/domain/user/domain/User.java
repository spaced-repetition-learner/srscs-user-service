package de.danielkoellgen.srscsuserservice.domain.user.domain;

import de.danielkoellgen.srscsuserservice.domain.domainprimitives.MailAddress;
import de.danielkoellgen.srscsuserservice.domain.domainprimitives.Name;
import de.danielkoellgen.srscsuserservice.domain.domainprimitives.Username;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username=" + username +
                ", mailAddress=" + mailAddress +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", isActive=" + isActive +
                '}';
    }
}
