package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.EmailAlreadyTakenException;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void isAdmin_shouldReturnTrue_whenUserExistsAndIsAdmin() {
        // given
        User admin = User.builder().id(1L).email("admin@yoga.com").firstName("Admin").lastName("Studio").password("encoded").admin(true).build();
        when(userRepository.findByEmail("admin@yoga.com")).thenReturn(Optional.of(admin));

        // when
        boolean result = authService.isAdmin("admin@yoga.com");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void isAdmin_shouldReturnFalse_whenUserExistsAndIsNotAdmin() {
        // given
        User user = User.builder().id(2L).email("user@yoga.com").firstName("Regular").lastName("User").password("encoded").admin(false).build();
        when(userRepository.findByEmail("user@yoga.com")).thenReturn(Optional.of(user));

        // when
        boolean result = authService.isAdmin("user@yoga.com");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void isAdmin_shouldReturnFalse_whenUserDoesNotExist() {
        // given
        when(userRepository.findByEmail("unknown@yoga.com")).thenReturn(Optional.empty());

        // when
        boolean result = authService.isAdmin("unknown@yoga.com");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void register_shouldEncodePasswordAndSaveUser_whenEmailIsFree() {
        // given
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("new.user@yoga.com");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        signupRequest.setPassword("plainPassword");

        when(userRepository.existsByEmail("new.user@yoga.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        authService.register(signupRequest);

        // then
        User savedUser = captor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("new.user@yoga.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.isAdmin()).isFalse();
    }

    @Test
    void register_shouldThrowEmailAlreadyTakenException_whenEmailAlreadyExists() {
        // given
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("existing@yoga.com");
        signupRequest.setFirstName("Existing");
        signupRequest.setLastName("User");
        signupRequest.setPassword("plainPassword");

        when(userRepository.existsByEmail("existing@yoga.com")).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> authService.register(signupRequest))
                .isInstanceOf(EmailAlreadyTakenException.class);
        verify(userRepository, never()).save(any());
    }
}
