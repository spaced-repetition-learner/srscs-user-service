package de.danielkoellgen.srscsuserservice.controller.user.dtos;

import de.danielkoellgen.srscsuserservice.domain.user.domain.User;
import de.danielkoellgen.srscsuserservice.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserResponseDto {

    public UUID userId;

    public String username;

    public String mailAddress;

    public String firstName;

    public String lastName;

    public Boolean isActive;

    public UserResponseDto(UserDto userDto) {
        this.userId = userDto.userId;
        this.username = userDto.username.getUsername();
        this.mailAddress = userDto.mailAddress.getMailAddress();
        this.firstName = userDto.firstName.getName();
        this.lastName = userDto.lastName.getName();
        this.isActive = userDto.isActive;
    }

    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername().getUsername();
        this.mailAddress = user.getMailAddress().getMailAddress();
        this.firstName = user.getFirstName().getName();
        this.lastName = user.getLastName().getName();
        this.isActive = user.getIsActive();
    }
}
