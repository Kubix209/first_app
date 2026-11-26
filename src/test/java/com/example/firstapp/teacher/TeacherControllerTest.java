package com.example.firstapp.teacher;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.dto.CreateTeacherCommand;
import com.example.firstapp.teacher.dto.UpdateTeacherCommand;
import com.example.firstapp.teacher.model.Teacher;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//docelowo inna technika beforeAll, afterAll, afterEach, beforeEach
@ActiveProfiles("test") //napewno polacz sie z testowa baza danych nalezy pamietac aby stworzyc propertisy testowe i podpiac klase testowa za pomoca tej adnotacji ją do tych propertisow jezeli to sie powiedzie to w logach gdy aplikacja startuej zobaczymy ze odpalila sie z profilem test a nei default
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    void testFindAll_resultsInTeacherListBeingReturned() throws Exception {
        //given
        Teacher teacher1 = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA))
                .build();

        Teacher teacher2 = Teacher.builder()
                .firstName("Paweł")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA))
                .build();

        teacherRepository.save(teacher1);
        teacherRepository.save(teacher2);

        //when-then
        mockMvc.perform(get("/teachers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testDeleteById_resultsInTeacherBeingDeleted() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA))
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);

        mockMvc.perform(delete("/teachers/{id}", savedTeacher.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        Teacher deletedTeacher = teacherRepository.findById(savedTeacher.getId()).orElseThrow();
        assertTrue(deletedTeacher.isDeleted());
    }

    @Test
    void testSave_resultsInTeacherBeingSaved() throws Exception {
        CreateTeacherCommand command = CreateTeacherCommand.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA, Language.KOTLIN, Language.PYTHON))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        String[] expectedLanguages = command.languages().stream()
                .map(Language::name)
                .toArray(String[]::new);

        mockMvc.perform(post("/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(command.firstName()))
                .andExpect(jsonPath("$.lastName").value(command.lastName()))
                .andExpect(jsonPath("$.languages", containsInAnyOrder(expectedLanguages)));

        List<Teacher> teachers = teacherRepository.findAll();
        assertEquals(1, teachers.size());
        Teacher savedTeacher = teachers.get(0);
        assertEquals(command.firstName(), savedTeacher.getFirstName());
        assertEquals(command.lastName(), savedTeacher.getLastName());
        assertEquals(command.languages(), savedTeacher.getLanguages());
    }

    @Test
    void testUpdate_resultsInTeacherBeingUpdated() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA))
                .build();
        Teacher savedTeacher = teacherRepository.save(teacher);

        UpdateTeacherCommand command = UpdateTeacherCommand.builder()
                .languages(Set.of(Language.JAVA, Language.KOTLIN, Language.PYTHON))
                .build();

        String[] expectedLanguages = command.languages().stream()
                .map(Language::name)
                .toArray(String[]::new);

        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch("/teachers/{id}", savedTeacher.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.languages", containsInAnyOrder(expectedLanguages)));

        Teacher updatedTeacher = teacherRepository.findById(savedTeacher.getId()).orElseThrow();
        assertEquals(teacher.getFirstName(), updatedTeacher.getFirstName());
        assertEquals(teacher.getLastName(), updatedTeacher.getLastName());
        assertEquals(command.languages(), updatedTeacher.getLanguages());
    }

    @Test
    void testFindByLanguage_resultsInTeacherListBeingReturned() throws Exception {
        teacherRepository.saveAll(List.of(
                Teacher.builder()
                        .firstName("Jan")
                        .lastName("Kowalski")
                        .languages(Set.of(Language.JAVA))
                        .build(),
                Teacher.builder()
                        .firstName("Janusz")
                        .lastName("Kowalewski")
                        .languages(Set.of(Language.KOTLIN))
                        .build()));

        mockMvc.perform(get("/teachers").param("language", "JAVA"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("Jan"))
                .andExpect(jsonPath("$[0].lastName").value("Kowalski"))
                .andExpect(jsonPath("$[0].languages", containsInAnyOrder("JAVA")));
    }

    @Test
    void testSave_withInvalidFirstName_resultsInBadRequest() throws Exception {
        CreateTeacherCommand command = CreateTeacherCommand.builder()
                .firstName("jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations[0].field").value("firstName"))
                .andExpect(jsonPath("$.violations[0].message").value("PATTERN_MISMATCH"));

        assertEquals(0L, teacherRepository.count());
    }

    @Test
    void testUpdate_withNonExistingId_resultsInNotFound() throws Exception {
        UpdateTeacherCommand command = UpdateTeacherCommand.builder()
                .languages(Set.of(Language.JAVA, Language.KOTLIN, Language.PYTHON))
                .build();

        Long nonExistingId = 67L;

        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch("/teachers/{id}", nonExistingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("teacher with Id " + nonExistingId + " not found"));
    }

    @Test
    void testFindByLanguage_withUnknownLanguage_resultsInBadRequest() throws Exception {
        mockMvc.perform(get("/teachers").param("language", "UNKNOWN"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    //napisz reszte testow chcemy miec otestowane all endpoint
    //wybierz kontroler do ktorego napiszesz najwiecej testow, i odpal go z dirtiesContest i bez i zobaczy roznice w czasie
    // jak zrobisz happy pathy to spróbuj zrobic wszystkie nadHappyPathy - chceby zweryfikowac status na przyklad
}
