package de.danielkoellgen.srscsuserservice.controller.user.dtos;

import de.danielkoellgen.srscsuserservice.domain.domainprimitives.MailAddress;
import de.danielkoellgen.srscsuserservice.domain.domainprimitives.Name;
import de.danielkoellgen.srscsuserservice.domain.domainprimitives.Username;
import de.danielkoellgen.srscsuserservice.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequestDto {

    public String username;

    public String mailAddress;

    public String firstName;

    public String lastName;

    public UserDto mapToUserDto() throws Exception {
        Username username = new Username(this.username);
        MailAddress mailAddress = new MailAddress(this.mailAddress);
        Name firstname = new Name(this.firstName);
        Name lastname = new Name(this.lastName);
        return new UserDto(null, username, mailAddress, firstname, lastname, null);
    }

    @Override
    public String toString() {
        return "NewUserRequestDto{" +
                "username='" + username + '\'' +
                ", mailAddress='" + mailAddress + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
