package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
class TeacherControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacherRepository.deleteAll();
        teacher = teacherRepository.save(Teacher.builder().firstName("Margot").lastName("Delahaye").build());
    }

    @Test
    void findById_shouldReturnTeacher_whenTeacherExists() throws Exception {
        mockMvc.perform(get("/api/teacher/{id}", teacher.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Margot"))
                .andExpect(jsonPath("$.lastName").value("Delahaye"));
    }

    @Test
    void findById_shouldReturn404_whenTeacherDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/teacher/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_shouldReturn400_whenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/teacher/{id}", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_shouldReturnAllTeachers() throws Exception {
        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
