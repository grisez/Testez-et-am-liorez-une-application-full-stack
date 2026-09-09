package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    void toDto_shouldReturnNull_whenListIsNull() {
        // given / when
        List<TeacherDto> result = teacherMapper.toDto((List<Teacher>) null);

        // then
        assertThat(result).isNull();
    }

    @Test
    void toDto_shouldMapList_whenGivenTeachers() {
        // given
        Teacher teacher1 = Teacher.builder().id(1L).firstName("Margot").lastName("Delahaye").build();
        Teacher teacher2 = Teacher.builder().id(2L).firstName("Hélène").lastName("Thiercelin").build();

        // when
        List<TeacherDto> result = teacherMapper.toDto(List.of(teacher1, teacher2));

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Margot");
    }

    @Test
    void toEntity_shouldReturnNull_whenListIsNull() {
        // given / when
        List<Teacher> result = teacherMapper.toEntity((List<TeacherDto>) null);

        // then
        assertThat(result).isNull();
    }

    @Test
    void toEntity_shouldMapList_whenGivenDtos() {
        // given
        TeacherDto dto1 = new TeacherDto();
        dto1.setId(1L);
        dto1.setFirstName("Margot");
        dto1.setLastName("Delahaye");

        // when
        List<Teacher> result = teacherMapper.toEntity(List.of(dto1));

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Margot");
    }
}
