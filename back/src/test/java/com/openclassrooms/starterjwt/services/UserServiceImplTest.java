package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.UnauthorizedException;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        // given
        User user = User.builder().id(1L).email("margot@teacher.com").lastName("Delahaye").firstName("Margot").password("encoded").admin(false).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        User result = userService.findById(1L);

        // then
        assertThat(result).isEqualTo(user);
    }

    @Test
    void findById_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_shouldDeleteUser_whenRequestingUserIsOwner() {
        // given
        User user = User.builder().id(1L).email("margot@teacher.com").lastName("Delahaye").firstName("Margot").password("encoded").admin(false).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        userService.delete(1L, "margot@teacher.com");

        // then
        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.delete(99L, "margot@teacher.com"))
                .isInstanceOf(NotFoundException.class);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void delete_shouldThrowUnauthorizedException_whenRequestingUserIsNotOwner() {
        // given
        User user = User.builder().id(1L).email("margot@teacher.com").lastName("Delahaye").firstName("Margot").password("encoded").admin(false).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when / then
        assertThatThrownBy(() -> userService.delete(1L, "someone.else@mail.com"))
                .isInstanceOf(UnauthorizedException.class);
        verify(userRepository, never()).deleteById(any());
    }
}
