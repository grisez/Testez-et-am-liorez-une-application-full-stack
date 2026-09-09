package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
class SessionControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    private Teacher teacher;
    private Session session;
    private User user;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();
        teacher = teacherRepository.save(Teacher.builder().firstName("Margot").lastName("Delahaye").build());
        user = userRepository.save(User.builder()
                .email("participant@mail.com")
                .lastName("Doe")
                .firstName("John")
                .password("encoded")
                .admin(false)
                .build());
        session = sessionRepository.save(Session.builder()
                .name("Yoga du matin")
                .date(new Date())
                .description("Une session de yoga")
                .teacher(teacher)
                .users(List.of())
                .build());
    }

    @Test
    void findById_shouldReturnSession_whenSessionExists() throws Exception {
        mockMvc.perform(get("/api/session/{id}", session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga du matin"));
    }

    @Test
    void findById_shouldReturn404_whenSessionDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/session/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_shouldReturnAllSessions() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void update_shouldReturnUpdatedSession_whenSessionIsValid() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Yoga du matin (mis a jour)");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(teacher.getId());
        sessionDto.setDescription("Description mise a jour");

        mockMvc.perform(put("/api/session/{id}", session.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga du matin (mis a jour)"))
                .andExpect(jsonPath("$.description").value("Description mise a jour"));
    }

    @Test
    void update_shouldReturn400_whenSessionDataIsInvalid() throws Exception {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(teacher.getId());
        sessionDto.setDescription("Description mise a jour");

        mockMvc.perform(put("/api/session/{id}", session.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn201_whenSessionIsValid() throws Exception {
        // Session.teacher is a unique @OneToOne relation: needs its own teacher,
        // distinct from the one already linked to the session created in setUp().
        Teacher anotherTeacher = teacherRepository.save(Teacher.builder().firstName("Hélène").lastName("Thiercelin").build());
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Yoga du soir");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(anotherTeacher.getId());
        sessionDto.setDescription("Une autre session");

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Yoga du soir"))
                .andExpect(jsonPath("$.description").value("Une autre session"))
                .andExpect(jsonPath("$.teacher_id").value(anotherTeacher.getId()));
    }

    @Test
    void create_shouldReturn400_whenSessionDataIsInvalid() throws Exception {
        Teacher anotherTeacher = teacherRepository.save(Teacher.builder().firstName("Hélène").lastName("Thiercelin").build());
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(anotherTeacher.getId());
        sessionDto.setDescription("Une autre session");

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_shouldReturn204_whenSessionExists() throws Exception {
        mockMvc.perform(delete("/api/session/{id}", session.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturn404_whenSessionDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/session/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void participate_shouldReturn204_whenUserIsNotYetParticipating() throws Exception {
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", session.getId(), user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void participate_shouldReturn400_whenUserAlreadyParticipates() throws Exception {
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", session.getId(), user.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/session/{id}/participate/{userId}", session.getId(), user.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void noLongerParticipate_shouldReturn204_whenUserWasParticipating() throws Exception {
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", session.getId(), user.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", session.getId(), user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void noLongerParticipate_shouldReturn400_whenUserWasNotParticipating() throws Exception {
        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", session.getId(), user.getId()))
                .andExpect(status().isBadRequest());
    }
}
