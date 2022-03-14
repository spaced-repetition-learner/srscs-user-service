package de.danielkoellgen.srscsuserservice.domain.user.application;

import de.danielkoellgen.srscsuserservice.domain.user.domain.User;
import de.danielkoellgen.srscsuserservice.domain.user.dto.UserDto;
import de.danielkoellgen.srscsuserservice.domain.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto makeUser(UserDto user) throws Exception {
        User newUser = new User(user.username, user.mailAddress, user.firstName, user.lastName);
        userRepository.save(newUser);
        logger.info("New user '{}' created. [userId={}]", newUser.getUsername().getUsername(), newUser.getUserId());
        logger.trace("New user created: [{}]", newUser);
        return new UserDto(newUser);
    }

    public UserDto disableUser(UUID userId) throws NoSuchElementException {
        User user = userRepository.findById(userId).orElseThrow();
        user.disableUser();
        userRepository.save(user);
        logger.info("User '{}' disabled. [userId={}]", user.getUsername().getUsername(), user.getUserId());
        return new UserDto(user);
    }
}
