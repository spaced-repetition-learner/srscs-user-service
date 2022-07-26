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
        log.info("POST /users: Create new user '{}'...", userRestRequestDto.username);
        log.debug("{}", userRestRequestDto);
        UserDto userRequestDto;
        try {
            userRequestDto = userRestRequestDto.mapToUserDto();
        } catch (Exception e) {
            logger.trace("Request failed. Invalid mapping. Responding 400. [tid={}, message={}].",
                    transactionId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RequestBody arguments invalid", e);
        }
        try {
            UserDto userResponseDto = userService.createNewUser(userRequestDto);
            UserResponseDto userRestResponseDto = new UserResponseDto(userResponseDto);
            logger.trace("User created. Responding 201. [tid={}, payload={}]",
                    transactionId, userRestResponseDto);
            return new ResponseEntity<>(userRestResponseDto, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.trace("Request failed. Responding 403. [tid={}, message={}]",
                    transactionId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unknown", e);
        }
    }

    @GetMapping(value = "/users", produces = {"application/json"})
    @NewSpan("controller-get-user")
    public ResponseEntity<UserResponseDto> getUser(
            @RequestParam(name = "user-id") Optional<UUID> userId,
            @RequestParam(name = "username") Optional<String> username,
            @RequestParam(name = "mail-address") Optional<String> mailAddress) {
        UUID transactionId = UUID.randomUUID();
        logger.trace("GET /users: Fetch User by userId={}, username={}, mailAddress={}. [tid={}]",
                userId, username, mailAddress, transactionId);
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
            logger.trace("Request failed. User not found. Responding 404. [tid={}]", transactionId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.", e);
        }
        if (fetchedUser == null) {
            logger.trace("Request failed. Missing query-parameter. Responding 400. [tid={}]",
                    transactionId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing query-parameter");
        }
        UserResponseDto userRestResponseDto = new UserResponseDto(fetchedUser);
        logger.trace("User found. Responding 200. [tid={}, payload={}]",
                transactionId, userRestResponseDto);
        return new ResponseEntity<>(userRestResponseDto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/users/{user-id}")
    @NewSpan("controller-disable-user")
    public ResponseEntity<HttpStatus> disableUser(@PathVariable(name = "user-id") UUID userId) {
        UUID transactionId = UUID.randomUUID();
        logger.trace("DELETE /users/{}: Disable User. [tid={}",
                userId, transactionId);
        try {
            userService.disableUser(userId);
        } catch (NoSuchElementException e) {
            logger.trace("User not found. Responding 404. [tid={}, userId={}]",
                    transactionId, userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.", e);
        }
        logger.trace("User disabled. Responding 200. [tid={}, userId={}]",
                transactionId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
