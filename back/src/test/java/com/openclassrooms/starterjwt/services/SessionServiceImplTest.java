package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Test
    void create_shouldSaveAndReturnSession() {
        // given
        Session session = Session.builder().name("Hatha Yoga").build();
        Session savedSession = Session.builder().id(1L).name("Hatha Yoga").build();
        when(sessionRepository.save(session)).thenReturn(savedSession);

        // when
        Session result = sessionService.create(session);

        // then
        assertThat(result).isEqualTo(savedSession);
    }

    @Test
    void getById_shouldReturnSession_whenSessionExists() {
        // given
        Session session = Session.builder().id(1L).name("Hatha Yoga").build();
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        // when
        Session result = sessionService.getById(1L);

        // then
        assertThat(result).isEqualTo(session);
    }

    @Test
    void getById_shouldThrowNotFoundException_whenSessionDoesNotExist() {
        // given
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> sessionService.getById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAll_shouldReturnAllSessions() {
        // given
        Session session1 = Session.builder().id(1L).name("Hatha Yoga").build();
        Session session2 = Session.builder().id(2L).name("Vinyasa Flow").build();
        when(sessionRepository.findAll()).thenReturn(List.of(session1, session2));

        // when
        List<Session> result = sessionService.findAll();

        // then
        assertThat(result).containsExactly(session1, session2);
    }

    @Test
    void update_shouldSetIdAndSaveSession() {
        // given
        Session session = Session.builder().name("Hatha Yoga updated").build();
        Session savedSession = Session.builder().id(1L).name("Hatha Yoga updated").build();
        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
        when(sessionRepository.save(captor.capture())).thenReturn(savedSession);

        // when
        Session result = sessionService.update(1L, session);

        // then
        assertThat(captor.getValue().getId()).isEqualTo(1L);
        assertThat(result).isEqualTo(savedSession);
    }

    @Test
    void delete_shouldDeleteSession_whenSessionExists() {
        // given
        Session session = Session.builder().id(1L).name("Hatha Yoga").build();
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        // when
        sessionService.delete(1L);

        // then
        verify(sessionRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenSessionDoesNotExist() {
        // given
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> sessionService.delete(99L))
                .isInstanceOf(NotFoundException.class);
        verify(sessionRepository, never()).deleteById(any());
    }

    @Test
    void participate_shouldAddUserToSession() {
        // given
        User user = User.builder().id(10L).email("test@yoga.com").firstName("Test").lastName("User").password("encoded").admin(false).build();
        Session session = Session.builder().id(1L).name("Hatha Yoga").users(new ArrayList<>()).build();
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        // when
        sessionService.participate(1L, 10L);

        // then
        assertThat(session.getUsers()).containsExactly(user);
        verify(sessionRepository).save(session);
    }

    @Test
    void participate_shouldThrowNotFoundException_whenSessionDoesNotExist() {
        // given
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());
        when(userRepository.findById(10L)).thenReturn(Optional.of(User.builder().id(10L).email("test@yoga.com").firstName("Test").lastName("User").password("encoded").admin(false).build()));

        // when / then
        assertThatThrownBy(() -> sessionService.participate(99L, 10L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void participate_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // given
        Session session = Session.builder().id(1L).name("Hatha Yoga").users(new ArrayList<>()).build();
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> sessionService.participate(1L, 99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void participate_shouldThrowBadRequestException_whenUserAlreadyParticipates() {
        // given
        User user = User.builder().id(10L).email("test@yoga.com").firstName("Test").lastName("User").password("encoded").admin(false).build();
        Session session = Session.builder().id(1L).name("Hatha Yoga").users(new ArrayList<>(List.of(user))).build();
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        // when / then
        assertThatThrownBy(() -> sessionService.participate(1L, 10L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void noLongerParticipate_shouldRemoveUserFromSession() {
        // given
        User user = User.builder().id(10L).email("test@yoga.com").firstName("Test").lastName("User").password("encoded").admin(false).build();
        Session session = Session.builder().id(1L).name("Hatha Yoga").users(new ArrayList<>(List.of(user))).build();
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        // when
        sessionService.noLongerParticipate(1L, 10L);

        // then
        assertThat(session.getUsers()).isEmpty();
        verify(sessionRepository).save(session);
    }

    @Test
    void noLongerParticipate_shouldThrowNotFoundException_whenSessionDoesNotExist() {
        // given
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> sessionService.noLongerParticipate(99L, 10L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void noLongerParticipate_shouldThrowBadRequestException_whenUserDoesNotParticipate() {
        // given
        Session session = Session.builder().id(1L).name("Hatha Yoga").users(new ArrayList<>()).build();
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        // when / then
        assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 10L))
                .isInstanceOf(BadRequestException.class);
    }
}
