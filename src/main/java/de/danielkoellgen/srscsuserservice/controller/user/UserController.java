package de.danielkoellgen.srscsuserservice.controller.user;

import de.danielkoellgen.srscsuserservice.controller.user.dtos.NewUserRequestDto;
import de.danielkoellgen.srscsuserservice.controller.user.dtos.UserResponseDto;
import de.danielkoellgen.srscsuserservice.domain.user.application.UserService;
import de.danielkoellgen.srscsuserservice.domain.user.domain.User;
import de.danielkoellgen.srscsuserservice.domain.user.dto.UserDto;
import de.danielkoellgen.srscsuserservice.domain.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "/users", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<UserResponseDto> makeNewUser(@RequestBody NewUserRequestDto userRestRequestDto) {
        UserDto userRequestDto;
        try {
            userRequestDto = userRestRequestDto.mapToUserDto();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RequestBody arguments invalid", e);
        }
        try {
            UserDto userResponseDto = userService.makeUser(userRequestDto);
            UserResponseDto userRestResponseDto = new UserResponseDto(userResponseDto);
            return new ResponseEntity<>(userRestResponseDto, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unknown", e);
        }
    }

    @GetMapping(value = "/users", produces = {"application/json"})
    public ResponseEntity<UserResponseDto> getUser(
            @RequestParam(name = "user-id") Optional<UUID> userId,
            @RequestParam(name = "username") Optional<String> username,
            @RequestParam(name = "mail-address") Optional<String> mailAddress) {
        User fetchedUser = null;
        try {
            if (userId.isPresent()) {
                fetchedUser = userRepository.findById(userId.get()).orElseThrow();
            }
            if (username.isPresent()) {
                fetchedUser = userRepository.findUserByUsername_Username(username.get()).orElseThrow();
            }
            if (mailAddress.isPresent()) {
                fetchedUser = userRepository.findUserByMailAddress_MailAddress(mailAddress.get()).orElseThrow();
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.", e);
        }
        if (fetchedUser == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing query-parameter");
        }
        UserResponseDto userRestResponseDto = new UserResponseDto(fetchedUser);
        return new ResponseEntity<>(userRestResponseDto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/users/{user-id}")
    public ResponseEntity<HttpStatus> disableUser(@PathVariable(name = "user-id") UUID userId) {
        try {
            userService.disableUser(userId);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
