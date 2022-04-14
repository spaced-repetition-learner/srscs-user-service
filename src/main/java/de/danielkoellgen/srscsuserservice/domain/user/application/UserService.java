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
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public @NotNull UserDto createNewUser(@NotNull UUID transactionId, @NotNull UserDto userDto) {
        User newUser = new User(userDto.username, userDto.mailAddress, userDto.firstName, userDto.lastName);
        userRepository.save(newUser);
        logger.info("New user '{}' created. [userId={}]", newUser.getUsername().getUsername(), newUser.getUserId());
        logger.trace("New user created: [{}]", newUser);
        kafkaProducer.send(new UserCreated(transactionId, new UserCreatedDto(newUser)));
        return new UserDto(newUser);
    }

    public @NotNull UserDto disableUser(@NotNull UUID transactionId, @NotNull UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.disableUser();
        userRepository.save(user);
        logger.info("User '{}' disabled. [userId={}]", user.getUsername().getUsername(), user.getUserId());
        kafkaProducer.send(new UserDisabled(transactionId, new UserDisabledDto(userId)));
        return new UserDto(user);
    }
}
