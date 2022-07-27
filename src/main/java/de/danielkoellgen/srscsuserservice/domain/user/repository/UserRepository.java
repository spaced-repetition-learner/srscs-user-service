package de.danielkoellgen.srscsuserservice.domain.user.repository;

import de.danielkoellgen.srscsuserservice.domain.user.domain.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

  Optional<User> findUserByUsername_Username(@NotNull String username);

  Optional<User> findUserByMailAddress_MailAddress(@NotNull String mailAddress);
}
