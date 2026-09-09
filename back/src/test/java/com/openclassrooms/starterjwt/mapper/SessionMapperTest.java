package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionMapperTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SessionMapperImpl sessionMapper;

    @Test
    void toEntity_shouldResolveTeacherAndUsers_fromTheirIds() {
        // given
        Teacher teacher = Teacher.builder().id(1L).firstName("Margot").lastName("Delahaye").build();
        User user = User.builder().id(2L).email("user@mail.com").lastName("Doe").firstName("John").password("pwd").admin(false).build();
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(userService.findById(2L)).thenReturn(user);

        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Yoga du matin");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Une session");
        sessionDto.setTeacher_id(1L);
        sessionDto.setUsers(List.of(2L));

        // when
        Session session = sessionMapper.toEntity(sessionDto);

        // then
        assertThat(session.getTeacher()).isEqualTo(teacher);
        assertThat(session.getUsers()).containsExactly(user);
    }

    @Test
    void toEntity_shouldLeaveTeacherNull_whenTeacherIdIsNull() {
        // given
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Yoga du matin");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Une session");
        sessionDto.setTeacher_id(null);

        // when
        Session session = sessionMapper.toEntity(sessionDto);

        // then
        assertThat(session.getTeacher()).isNull();
    }

    @Test
    void toDto_shouldExtractTeacherIdAndUserIds_fromEntities() {
        // given
        Teacher teacher = Teacher.builder().id(1L).firstName("Margot").lastName("Delahaye").build();
        User user = User.builder().id(2L).email("user@mail.com").lastName("Doe").firstName("John").password("pwd").admin(false).build();
        Session session = Session.builder()
                .name("Yoga du matin")
                .date(new Date())
                .description("Une session")
                .teacher(teacher)
                .users(List.of(user))
                .build();

        // when
        SessionDto sessionDto = sessionMapper.toDto(session);

        // then
        assertThat(sessionDto.getTeacher_id()).isEqualTo(1L);
        assertThat(sessionDto.getUsers()).containsExactly(2L);
    }

    @Test
    void toDto_shouldReturnNullTeacherId_whenSessionHasNoTeacher() {
        // given
        Session session = Session.builder()
                .name("Yoga du matin")
                .date(new Date())
                .description("Une session")
                .teacher(null)
                .users(List.of())
                .build();

        // when
        SessionDto sessionDto = sessionMapper.toDto(session);

        // then
        assertThat(sessionDto.getTeacher_id()).isNull();
        assertThat(sessionDto.getUsers()).isEmpty();
    }

    @Test
    void toEntity_shouldReturnEmptyUsersList_whenSessionDtoHasNoUsers() {
        // given
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Yoga du matin");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Une session");
        sessionDto.setTeacher_id(null);
        sessionDto.setUsers(null);

        // when
        Session session = sessionMapper.toEntity(sessionDto);

        // then
        assertThat(session.getUsers()).isEmpty();
    }
}
