package de.danielkoellgen.srscsuserservice.domain.user.application;

import de.danielkoellgen.srscsuserservice.domain.user.domain.User;
import de.danielkoellgen.srscsuserservice.domain.user.dto.UserDto;
import de.danielkoellgen.srscsuserservice.domain.user.repository.UserRepository;
import de.danielkoellgen.srscsuserservice.events.producer.KafkaProducer;
import de.danielkoellgen.srscsuserservice.events.producer.user.UserCreated;
import de.danielkoellgen.srscsuserservice.events.producer.user.UserDisabled;
import de.danielkoellgen.srscsuserservice.events.producer.user.dto.UserCreatedDto;
import de.danielkoellgen.srscsuserservice.events.producer.user.dto.UserDisabledDto;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;

    @Autowired
    private Tracer tracer;

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public @NotNull UserDto createNewUser(@NotNull UserDto userDto) {
        log.trace("Creating new user '{}'...", userDto.username);

        User newUser = new User(userDto.username, userDto.mailAddress, userDto.firstName,
                userDto.lastName);
        userRepository.save(newUser);
        log.info("User '{}' created.", newUser.getUsername().getUsername());
        log.debug("{}", newUser);

        kafkaProducer.send(new UserCreated(getTraceIdOrEmptyString(), new UserCreatedDto(newUser)));
        return new UserDto(newUser);
    }

    public @NotNull UserDto disableUser(@NotNull UUID userId) {
        log.trace("Disabling user...");

        User user = userRepository.findById(userId).orElseThrow();
        log.debug("User fetched by user-id. {}", user);

        user.disableUser();
        userRepository.save(user);
        log.info("User '{}' disabled.", user.getUsername().getUsername());
        log.debug("{}", user);

        kafkaProducer.send(new UserDisabled(getTraceIdOrEmptyString(), new UserDisabledDto(userId)));
        return new UserDto(user);
    }

    private String getTraceIdOrEmptyString() {
        try {
            return tracer.currentSpan().context().traceId();
        } catch (Exception e) {
            return "";
        }
    }
}
