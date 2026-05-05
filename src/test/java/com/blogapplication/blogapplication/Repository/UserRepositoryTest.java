package com.blogapplication.blogapplication.Repository;

import com.blogapplication.blogapplication.entity.User;
import com.blogapplication.blogapplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user1, user2;  @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("John Doe");
        user1.setUsername("johndoe");
        user1.setEmail("john@example.com");
        user1.setPassword("encoded123");

        user2 = new User();
        user2.setName("Jane Doe");
        user2.setUsername("janedoe");
        user2.setEmail("jane@example.com");
        user2.setPassword("encoded456");

    }


    @Test

    void findByUsername_Existing() {
        User user=userRepository.save(user1);
        Optional<User> found = userRepository.findByUsername("johndoe");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }
    @Test
    void findByUsername_NonExisting() {
        Optional<User> found = userRepository.findByUsername("unknown");
        assertThat(found).isEmpty();
    }

    @Test
    void findByEmail_Existing() {
        userRepository.save(user2);

        Optional<User> found = userRepository.findByEmail("jane@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("janedoe");
    }

    @Test
    void findByEmail_NonExisting() {
        Optional<User> found = userRepository.findByEmail("none@test.com");
        assertThat(found).isEmpty();
    }

    @Test
    void findByUsernameOrEmail_ByUsername() {
        userRepository.save(user1);

        Optional<User> found = userRepository.findByUsernameOrEmail("johndoe", "johndoe");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void findByUsernameOrEmail_ByEmail() {
        userRepository.save(user2);

        Optional<User> found = userRepository.findByUsernameOrEmail("jane@example.com", "jane@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Jane Doe");
    }

    @Test
    void existsByUsername_True() {
        userRepository.save(user1);

        assertThat(userRepository.existsByUsername("johndoe")).isTrue();
    }

    @Test
    void existsByUsername_False() {
        assertThat(userRepository.existsByUsername("nobody")).isFalse();
    }

    @Test
    void existsByEmail_True() {
        userRepository.save(user2);
        assertThat(userRepository.existsByEmail("jane@example.com")).isTrue();
    }

    @Test
    void existsByEmail_False() {
        assertThat(userRepository.existsByEmail("nobody@test.com")).isFalse();
    }
}
