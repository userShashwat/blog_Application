package com.blogapplication.blogapplication.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import com.blogapplication.blogapplication.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.blogapplication.blogapplication.entity.Role;

@DataJpaTest
@DisplayName("Role Repository Tests")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        Role roleUser = new Role();
        roleUser.setName("ROLE_USER");
        roleRepository.save(roleUser);

        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");
        roleRepository.save(roleAdmin);
    }

    @Test
    @DisplayName("1. findByName – ROLE_USER returns role")
    void findByName_RoleUser() {
        Optional<Role> found = roleRepository.findByName("ROLE_USER");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("2. findByName – ROLE_ADMIN returns role")
    void findByName_RoleAdmin() {
        Optional<Role> found = roleRepository.findByName("ROLE_ADMIN");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("3. findByName – non-existing returns empty")
    void findByName_NonExisting() {
        Optional<Role> found = roleRepository.findByName("ROLE_MANAGER");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("4. Save role – persists with ID")
    void saveRole_Persists() {
        Role role = new Role();
        role.setName("ROLE_MODERATOR");
        Role saved = roleRepository.save(role);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    @DisplayName("5. findAll – returns all roles")
    void findAll_ReturnsAll() {
        assertThat(roleRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("6. deleteById – removes role")
    void deleteById_Removes() {
        roleRepository.deleteById(1L);
        assertThat(roleRepository.findById(1L)).isEmpty();
        assertThat(roleRepository.count()).isEqualTo(1);
    }
}