package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceImplTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    @Test
    void findAll_shouldReturnAllTeachers() {
        // given
        Teacher teacher1 = Teacher.builder().id(1L).firstName("Margot").lastName("Delahaye").build();
        Teacher teacher2 = Teacher.builder().id(2L).firstName("Hélène").lastName("Thiercelin").build();
        when(teacherRepository.findAll()).thenReturn(List.of(teacher1, teacher2));

        // when
        List<Teacher> result = teacherService.findAll();

        // then
        assertThat(result).containsExactly(teacher1, teacher2);
    }

    @Test
    void findById_shouldReturnTeacher_whenTeacherExists() {
        // given
        Teacher teacher = Teacher.builder().id(1L).firstName("Margot").lastName("Delahaye").build();
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        // when
        Teacher result = teacherService.findById(1L);

        // then
        assertThat(result).isEqualTo(teacher);
    }

    @Test
    void findById_shouldThrowNotFoundException_whenTeacherDoesNotExist() {
        // given
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> teacherService.findById(99L))
                .isInstanceOf(NotFoundException.class);
    }
}
