package de.danielkoellgen.srscsuserservice.domain.user.dto;

import de.danielkoellgen.srscsuserservice.domain.domainprimitives.MailAddress;
import de.danielkoellgen.srscsuserservice.domain.domainprimitives.Name;
import de.danielkoellgen.srscsuserservice.domain.domainprimitives.Username;
import de.danielkoellgen.srscsuserservice.domain.user.domain.User;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class UserDto {

    public UUID userId;

    public Username username;

    public MailAddress mailAddress;

    public Name firstName;

    public Name lastName;

    public Boolean isActive;

    public UserDto(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.mailAddress = user.getMailAddress();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.isActive = user.getIsActive();
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "userId=" + userId +
                ", username=" + username +
                ", mailAddress=" + mailAddress +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", isActive=" + isActive +
                '}';
    }
}
