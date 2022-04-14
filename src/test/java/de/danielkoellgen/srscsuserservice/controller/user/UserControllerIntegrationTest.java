package de.danielkoellgen.srscsuserservice.controller.user;

import de.danielkoellgen.srscsuserservice.controller.user.UserController;
import de.danielkoellgen.srscsuserservice.controller.user.dtos.NewUserRequestDto;
import de.danielkoellgen.srscsuserservice.controller.user.dtos.UserResponseDto;
import de.danielkoellgen.srscsuserservice.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:29092", "port=29092"})
public class UserControllerIntegrationTest {

    private final WebTestClient webTestClient;

    private final UserRepository userRepository;

    @Autowired
    public UserControllerIntegrationTest(UserController userController, UserRepository userRepository) {
        this.webTestClient = WebTestClient.bindToController(userController).build();
        this.userRepository = userRepository;
    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    public void shouldAllowToCreateNewUsers() {
        // given
        NewUserRequestDto requestDto = new NewUserRequestDto("dadepu", "dadepu@gmail.com",
                "abc123", "Daniel", "Koellgen");

        // when
        UserResponseDto userCreatedDto = webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class)
                .returnResult()
                .getResponseBody();
        assert userCreatedDto != null;
        UUID userId = userCreatedDto.userId;

        // then
        UserResponseDto userFetchedDto = webTestClient.get().uri("/users?user-id="+userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult()
                .getResponseBody();
        assertThat(userFetchedDto)
                .isEqualTo(userCreatedDto);
    }

    @Test
    public void shouldNotAllowNewUsersWhenUsernameAlreadyExists() {
        // given
        UserResponseDto testUserDto = postTestUser("dadepu", "dadepu@gmail.com");
        NewUserRequestDto userSameUsernameDto = new NewUserRequestDto("dadepu", "somemail@gmail.com",
                "abc123","anyName", "anyName");

        // when then
        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userSameUsernameDto)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void shouldNotAllowNewUsersWhenMailAddressAlreadyExists() {
        // given
        UserResponseDto testUserDto = postTestUser("dadepu", "dadepu@gmail.com");
        NewUserRequestDto userSameMailDto = new NewUserRequestDto("anyOther", "dadepu@gmail.com",
                "abc123","anyName", "anyName");

        // when then
        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userSameMailDto)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void shouldAllowToFetchUsersByUsernameAndMailAddress() {
        // given
        UserResponseDto testUserDto = postTestUser("dadepu", "dadepu@gmail.com");

        // when
        UserResponseDto fetchedUserUsername = webTestClient.get().uri("/users?username="+testUserDto.username)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(UserResponseDto.class)
                .returnResult()
                .getResponseBody();

        // and when
        UserResponseDto fetchedUserMail = webTestClient.get().uri("/users?mail-address="+testUserDto.mailAddress)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(UserResponseDto.class)
                .returnResult()
                .getResponseBody();

        // then
        assertThat(testUserDto)
                .isEqualTo(fetchedUserUsername);
        assertThat(testUserDto)
                .isEqualTo(fetchedUserMail);
    }

    @Test
    public void shouldAllowToDisableUsers() {
        // given
        UserResponseDto testUserDto = postTestUser("dadepu", "dadepu@gmail.com");
        UUID userId = testUserDto.userId;

        // when
        webTestClient.delete().uri("/users/"+userId)
                .exchange()
                .expectStatus().isOk();

        // then
        UserResponseDto fetchedUser = webTestClient.get().uri("/users?user-id="+userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(UserResponseDto.class)
                .returnResult()
                .getResponseBody();
        assert fetchedUser != null;
        assertThat(fetchedUser.isActive)
                .isFalse();
    }

    private UserResponseDto postTestUser(String username, String mailAddress) {
        NewUserRequestDto requestDto = new NewUserRequestDto(username, mailAddress,
                "abc123", "anyName", "anyName");

        UserResponseDto userCreatedDto = webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectBody(UserResponseDto.class)
                .returnResult()
                .getResponseBody();
        assert userCreatedDto != null;
        return userCreatedDto;
    }
}
