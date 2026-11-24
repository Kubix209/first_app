package com.example.firstapp.teacher;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.model.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)//docelowo inna technika beforeAll, afterAll, afterEach, beforeEach
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

    //napisz reszte testow chcemy miec otestowane all endpoint
    //wybierz kontroler do ktorego napiszesz najwiecej testow, i odpal go z dirtiesContest i bez i zobaczy roznice w czasie
    // jak zrobisz happy pathy to spróbuj zrobic wszystkie nadHappyPathy - chceby zweryfikowac status na przyklad
}
