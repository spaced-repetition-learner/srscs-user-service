package de.danielkoellgen.srscsuserservice.domain.user.application;

import de.danielkoellgen.srscsuserservice.domain.user.domain.User;
import de.danielkoellgen.srscsuserservice.domain.user.dto.UserDto;
import de.danielkoellgen.srscsuserservice.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto makeUser(UserDto user) throws Exception {
        User newUser = new User(user.username, user.mailAddress, user.firstName, user.lastName);
        userRepository.save(newUser);
        return new UserDto(newUser);
    }

    public UserDto disableUser(UUID userId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow();
        user.disableUser();
        userRepository.save(user);
        return new UserDto(user);
    }
}
