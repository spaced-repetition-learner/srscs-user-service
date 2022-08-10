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
        log.info("POST /users: Create new User '{}'... {}", userRestRequestDto.username,
                userRestRequestDto);
        UserDto userRequestDto;
        try {
            userRequestDto = userRestRequestDto.mapToUserDto();

        } catch (Exception e) {
            log.info("Request failed w/ 400. Request-Body arguments invalid.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RequestBody arguments invalid", e);
        }
        try {
            UserDto userResponseDto = userService.createNewUser(userRequestDto);
            UserResponseDto userRestResponseDto = new UserResponseDto(userResponseDto);
            log.info("Request successful. Responding w/ 201.");
            log.debug("Response: {}", userRestResponseDto);
            return new ResponseEntity<>(userRestResponseDto, HttpStatus.CREATED);

        } catch (Exception e) {
            log.info("Request failed w/ 403. {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unknown", e);
        }
    }

    @GetMapping(value = "/users", produces = {"application/json"})
    @NewSpan("controller-get-user")
    public ResponseEntity<UserResponseDto> getUser(
            @RequestParam(name = "user-id") Optional<UUID> userId,
            @RequestParam(name = "username") Optional<String> username,
            @RequestParam(name = "mail-address") Optional<String> mailAddress) {

        log.info("GET /users: Fetch user... [userId={}, username={}, mailAddress={}]", userId,
                username, mailAddress);
        User fetchedUser = null;
        try {
            if (userId.isPresent()) {
                log.trace("Fetching User by id '{}'...", userId.get());
                fetchedUser = userRepository.findById(userId.get()).orElseThrow();
                log.debug("Fetched User by id: {}", fetchedUser);
            }
            if (username.isPresent()) {
                log.trace("Fetching User by username '{}'...", username.get());
                fetchedUser = userRepository.findUserByUsername_Username(username.get()).orElseThrow();
                log.debug("Fetched User by username: {}", fetchedUser);
            }
            if (mailAddress.isPresent()) {
                log.trace("Fetching User by mailAddress '{}'...", mailAddress.get());
                fetchedUser = userRepository.findUserByMailAddress_MailAddress(mailAddress.get()).orElseThrow();
                log.debug("Fetched User by mailAddress: {}", fetchedUser);
            }
        } catch (NoSuchElementException e) {
            log.info("Request failed w/ 404. {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.", e);
        }
        if (fetchedUser == null) {
            log.info("Request failed /w 400. Filter is missing.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing query-parameter");
        }
        {
            UserResponseDto userRestResponseDto = new UserResponseDto(fetchedUser);
            log.info("Request successful. Responding w/ 201.");
            log.debug("Response: {}", userRestResponseDto);
            return new ResponseEntity<>(userRestResponseDto, HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/users/{user-id}")
    @NewSpan("controller-disable-user")
    public ResponseEntity<HttpStatus> disableUser(@PathVariable(name = "user-id") UUID userId) {
        log.info("DELETE /users/{}: Disable User...", userId);

        try {
            userService.disableUser(userId);
            log.info("Request successful. Responding w/ 200.");
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (NoSuchElementException e) {
            log.info("Request failed w/ 404. User not found.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.", e);
        }
    }
}
