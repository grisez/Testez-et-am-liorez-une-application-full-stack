package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TeacherMapperTest {

    private final TeacherMapper teacherMapper = new TeacherMapperImpl();

    @Test
    void toDto_shouldMapAllFields() {
        // given
        Teacher teacher = Teacher.builder().id(1L).firstName("Margot").lastName("Delahaye").build();

        // when
        TeacherDto dto = teacherMapper.toDto(teacher);

        // then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("Margot");
        assertThat(dto.getLastName()).isEqualTo("Delahaye");
    }

    @Test
    void toEntity_shouldMapAllFields() {
        // given
        TeacherDto dto = new TeacherDto();
        dto.setId(1L);
        dto.setFirstName("Margot");
        dto.setLastName("Delahaye");

        // when
        Teacher teacher = teacherMapper.toEntity(dto);

        // then
        assertThat(teacher.getId()).isEqualTo(1L);
        assertThat(teacher.getFirstName()).isEqualTo("Margot");
        assertThat(teacher.getLastName()).isEqualTo("Delahaye");
    }

    @Test
    void toDto_shouldReturnNull_whenTeacherIsNull() {
        // given / when
        TeacherDto dto = teacherMapper.toDto((Teacher) null);

        // then
        assertThat(dto).isNull();
    }
}
