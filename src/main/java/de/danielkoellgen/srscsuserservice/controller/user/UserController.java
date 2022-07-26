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
import org.springframework.cloud.sleuth.annotation.NewSpan;
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

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "/users", consumes = {"application/json"}, produces = {"application/json"})
    @NewSpan("controller-create-new-user")
    public ResponseEntity<UserResponseDto> createNewUser(@RequestBody NewUserRequestDto userRestRequestDto) {
        log.info("POST /users: Create new user '{}'... {}", userRestRequestDto.username,
                userRestRequestDto);
        UserDto userRequestDto;
        try {
            userRequestDto = userRestRequestDto.mapToUserDto();
        } catch (Exception e) {
            log.trace("Request failed with 400.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RequestBody arguments invalid", e);
        }
        try {
            UserDto userResponseDto = userService.createNewUser(userRequestDto);
            UserResponseDto userRestResponseDto = new UserResponseDto(userResponseDto);
            log.trace("Responding 201.");
            log.debug("{}", userRestResponseDto);
            return new ResponseEntity<>(userRestResponseDto, HttpStatus.CREATED);
        } catch (Exception e) {
            log.trace("Responding 403. {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unknown", e);
        }
    }

    @GetMapping(value = "/users", produces = {"application/json"})
    @NewSpan("controller-get-user")
    public ResponseEntity<UserResponseDto> getUser(
            @RequestParam(name = "user-id") Optional<UUID> userId,
            @RequestParam(name = "username") Optional<String> username,
            @RequestParam(name = "mail-address") Optional<String> mailAddress) {

        log.info("GET /users: Fetch user by userId={}, username={}, mailAddress={}...", userId,
                username, mailAddress);
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
            log.trace("Responding 404. {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.", e);
        }
        if (fetchedUser == null) {
            log.trace("Responding 400. Filter missing.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing query-parameter");
        }
        {
            UserResponseDto userRestResponseDto = new UserResponseDto(fetchedUser);
            log.trace("Responding 201.");
            log.debug("{}", userRestResponseDto);
            return new ResponseEntity<>(userRestResponseDto, HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/users/{user-id}")
    @NewSpan("controller-disable-user")
    public ResponseEntity<HttpStatus> disableUser(@PathVariable(name = "user-id") UUID userId) {
        log.info("DELETE /users/{}: Disable user...", userId);

        try {
            userService.disableUser(userId);
        } catch (NoSuchElementException e) {
            log.trace("Responding 404. User not found.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.", e);
        }
        {
            log.trace("Responding 200.");
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
