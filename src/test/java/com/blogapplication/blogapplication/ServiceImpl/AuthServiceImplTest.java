package com.blogapplication.blogapplication.ServiceImpl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.blogapplication.blogapplication.entity.Role;
import com.blogapplication.blogapplication.entity.User;
import com.blogapplication.blogapplication.exception.BlogAPIException;
import com.blogapplication.blogapplication.payload.LoginDTO;
import com.blogapplication.blogapplication.payload.RegisterDTO;
import com.blogapplication.blogapplication.repository.RoleRepository;
import com.blogapplication.blogapplication.repository.UserRepository;
import com.blogapplication.blogapplication.security.JwtTokenProvider;
import com.blogapplication.blogapplication.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;
    private LoginDTO loginDTO;
    private RegisterDTO registerDTO;
    private Role roleUser;
    private User user;
    @BeforeEach
    void setUp() {
        loginDTO = new LoginDTO();
        loginDTO.setUsernameOrEmail("johndoe");
        loginDTO.setPassword("password123");

        registerDTO = new RegisterDTO();
        registerDTO.setName("John Doe");
        registerDTO.setUsername("johndoe");
        registerDTO.setEmail("john@example.com");
        registerDTO.setPassword("password123");

        roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
    }
    @Test
    void login_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt.token.string");
        String token = authService.login(loginDTO);
        assertThat(token).isEqualTo("jwt.token.string");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
    @Test

    void login_BadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        assertThatThrownBy(() -> authService.login(loginDTO))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials");
    }
    @Test
    void register_Success() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = authService.register(registerDTO);
        assertThat(result).isEqualTo("Registration Successful");
        verify(userRepository).save(any(User.class));
    }
    @Test
    void register_UsernameExists() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerDTO))
                .isInstanceOf(BlogAPIException.class)
                .hasMessageContaining("Username already exists")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(userRepository, never()).save(any());
    }
        @Test

        void register_EmailExists() {
            when(userRepository.existsByUsername("johndoe")).thenReturn(false);
            when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.register(registerDTO))
                    .isInstanceOf(BlogAPIException.class)
                    .hasMessageContaining("Email already exists")
                    .extracting("status")
                    .isEqualTo(HttpStatus.BAD_REQUEST);
            verify(userRepository, never()).save(any());
        }
    @Test

    void register_RoleNotFound() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.register(registerDTO))
                .isInstanceOf(BlogAPIException.class)
                .hasMessageContaining("ROLE_USER not found")
                .extracting("status")
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(userRepository, never()).save(any());
    }
    @Test
    void register_UserHasRoleAssigned() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(userRepository.save(any(User.class))).thenReturn(user);

        authService.register(registerDTO);

        verify(roleRepository).findByName("ROLE_USER");
        verify(userRepository).save(any(User.class));
    }

}
