package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "margot@teacher.com")
class UserControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("margot@teacher.com")
                .lastName("Delahaye")
                .firstName("Margot")
                .password("encodedPassword")
                .admin(false)
                .build());
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() throws Exception {
        mockMvc.perform(get("/api/user/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("margot@teacher.com"));
    }

    @Test
    void findById_shouldReturn404_whenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/user/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_shouldReturn400_whenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/user/{id}", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_shouldReturn204_whenRequestingUserIsOwner() throws Exception {
        mockMvc.perform(delete("/api/user/{id}", user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "someone.else@mail.com")
    void delete_shouldReturn401_whenRequestingUserIsNotOwner() throws Exception {
        mockMvc.perform(delete("/api/user/{id}", user.getId()))
                .andExpect(status().isUnauthorized());
    }
}
