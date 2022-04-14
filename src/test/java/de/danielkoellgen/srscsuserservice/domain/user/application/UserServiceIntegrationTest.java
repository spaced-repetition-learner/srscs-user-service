package de.danielkoellgen.srscsuserservice.domain.user.application;

import de.danielkoellgen.srscsuserservice.domain.domainprimitives.MailAddress;
import de.danielkoellgen.srscsuserservice.domain.domainprimitives.Name;
import de.danielkoellgen.srscsuserservice.domain.domainprimitives.Username;
import de.danielkoellgen.srscsuserservice.domain.user.application.UserService;
import de.danielkoellgen.srscsuserservice.domain.user.domain.User;
import de.danielkoellgen.srscsuserservice.domain.user.dto.UserDto;
import de.danielkoellgen.srscsuserservice.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserServiceIntegrationTest {

    private final UserService userService;
    private final UserRepository userRepository;

    private final UserDto testUser1;

    @Autowired
    public UserServiceIntegrationTest(UserService userService, UserRepository userRepository) throws Exception {
        this.userService = userService;
        this.userRepository = userRepository;

        this.testUser1 = new UserDto(null, new Username("dadepu"), new MailAddress("danielkoellgen@gmail.com"),
                new Name("Daniel"), new Name("Koellgen"), null);
    }

    @AfterEach
    public void cleanUp() {
        this.userRepository.deleteAll();
    }

    @Test
    public void shouldAllowToMakeNewUsers() throws Exception {
        // given
        UserDto dto = testUser1;

        // when
        UserDto responseDto = userService.createNewUser(UUID.randomUUID(), dto);

        // then
        User fetchedUser = userRepository.findById(responseDto.userId).orElseThrow();
        assertThat(fetchedUser.getUsername())
                .isEqualTo(dto.username);
        assertThat(fetchedUser.getMailAddress())
                .isEqualTo(dto.mailAddress);
        assertThat(fetchedUser.getFirstName())
                .isEqualTo(dto.firstName);
        assertThat(fetchedUser.getLastName())
                .isEqualTo(dto.lastName);
        assertThat(fetchedUser.getIsActive())
                .isTrue();
    }

    @Test
    public void shouldFindUserViaUsername() throws Exception {
        // given
        UserDto responseDto = userService.createNewUser(UUID.randomUUID(), testUser1);

        // when
        User fetchedUser = userRepository.findUserByUsername_Username(testUser1.username.getUsername()).orElseThrow();

        // then
        assertThat(fetchedUser.getUserId())
                .isEqualTo(responseDto.userId);
    }

    @Test
    public void shouldFindUserViaMailAddress() throws Exception {
        // given
        UserDto responseDto = userService.createNewUser(UUID.randomUUID(), testUser1);

        // when
        User fetchedUser = userRepository.findUserByMailAddress_MailAddress(testUser1.mailAddress.getMailAddress())
                .orElseThrow();

        // then
        assertThat(fetchedUser.getUserId())
                .isEqualTo(responseDto.userId);
    }

    @Test
    public void shouldAllowToDisableUsers() throws Exception {
        // given
        UserDto responseDto = userService.createNewUser(UUID.randomUUID(), testUser1);

        // when
        UserDto disabledDto = userService.disableUser(UUID.randomUUID(), responseDto.userId);

        // then
        assertThat(disabledDto.isActive)
                .isFalse();

        // and then
        User fetchedUser = userRepository.findById(responseDto.userId).orElseThrow();
        assertThat(fetchedUser.getIsActive())
                .isFalse();
    }

    @Test
    public void shouldPreventDuplicatedUsernames() throws Exception {
        // given
        userService.createNewUser(UUID.randomUUID(), testUser1);

        UserDto testUser2 = new UserDto(null, new Username("dadepu"),
                new MailAddress("anyotheraddress@gmail.com"), new Name("Daniel2"), new Name("Koellgen2"), null);

        // when then
        assertThrows(Exception.class, () -> {
            userService.createNewUser(UUID.randomUUID(), testUser2);
        });
    }

    @Test
    public void shouldPreventDuplicatedMailAddresses() throws Exception {
        // given
        userService.createNewUser(UUID.randomUUID(), testUser1);

        UserDto testUser2 = new UserDto(null, new Username("anyOtherName"),
                new MailAddress("danielkoellgen@gmail.com"), new Name("Daniel2"), new Name("Koellgen2"), null);

        // when then
        assertThrows(Exception.class, () -> {
            userService.createNewUser(UUID.randomUUID(), testUser2);
        });
    }
}
