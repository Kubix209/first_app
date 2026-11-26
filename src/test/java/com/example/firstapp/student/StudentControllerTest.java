package com.example.firstapp.student;

import com.example.firstapp.common.Language;
import com.example.firstapp.student.dto.ChangeStudentTeacherCommand;
import com.example.firstapp.student.dto.CreateStudentCommand;
import com.example.firstapp.student.dto.UpdateStudentCommand;
import com.example.firstapp.student.model.Student;
import com.example.firstapp.teacher.TeacherRepository;
import com.example.firstapp.teacher.dto.UpdateTeacherCommand;
import com.example.firstapp.teacher.model.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    void testFindAll_resultsInStudentListBeingReturned() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA, Language.KOTLIN, Language.PYTHON))
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);

        Student student1 = Student.builder()
                .firstName("Janusz")
                .lastName("Kowalewski")
                .language(Language.JAVA)
                .teacher(savedTeacher)
                .build();

        Student student2 = Student.builder()
                .firstName("Janek")
                .lastName("Kowalik")
                .language(Language.JAVA)
                .teacher(savedTeacher)
                .build();

        studentRepository.saveAll(List.of(student1, student2));

        mockMvc.perform(get("/students"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testDeleteById_resultsInStudentBeingDeleted() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA, Language.KOTLIN, Language.PYTHON))
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);

        Student student = Student.builder()
                .firstName("Janusz")
                .lastName("Kowalewski")
                .language(Language.JAVA)
                .teacher(savedTeacher)
                .build();

        Student savedStudent = studentRepository.save(student);

        mockMvc.perform(delete("/students/{id}", savedStudent.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        Student deletedStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        assertTrue(deletedStudent.isDeleted());
    }

    @Test
    void testSave_resultsInStudentBeingSaved() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA, Language.KOTLIN, Language.PYTHON))
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);

        CreateStudentCommand command = CreateStudentCommand.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacherId(savedTeacher.getId())
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(command.firstName()))
                .andExpect(jsonPath("$.lastName").value(command.lastName()))
                .andExpect(jsonPath("$.language").value(command.language().name()));

        List<Student> students = studentRepository.findAll();
        assertEquals(1, students.size());
        Student savedStudent = students.get(0);
        assertEquals(command.firstName(), savedStudent.getFirstName());
        assertEquals(command.lastName(), savedStudent.getLastName());
        assertEquals(command.language(), savedStudent.getLanguage());
        assertEquals(command.teacherId(), savedStudent.getTeacher().getId());
    }

    @Test
    void testUpdate_resultsInStudentBeingUpdated() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA, Language.KOTLIN, Language.PYTHON))
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);

        Student student = Student.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacher(savedTeacher)
                .build();

        Student savedStudent = studentRepository.save(student);

        UpdateStudentCommand command = UpdateStudentCommand.builder()
                .lastName("Kowalewski")
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch("/students/{id}", savedStudent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value(command.lastName()));

        Student updatedStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        assertEquals(student.getFirstName(), updatedStudent.getFirstName());
        assertEquals(command.lastName(), updatedStudent.getLastName());
        assertEquals(student.getLanguage(), updatedStudent.getLanguage());
        assertEquals(student.getTeacher().getId(), updatedStudent.getTeacher().getId());
    }

    @Test
    void testChangeTeacher_resultsInStudentTeacherBeingChanged() throws Exception {
        Teacher oldTeacher = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA, Language.KOTLIN, Language.PYTHON))
                .build();

        Teacher newTeacher = Teacher.builder()
                .firstName("Janek")
                .lastName("Kowalik")
                .languages(Set.of(Language.JAVA, Language.KOTLIN, Language.PYTHON))
                .build();

        teacherRepository.saveAll(List.of(oldTeacher, newTeacher));

        Student student = Student.builder()
                .firstName("Janusz")
                .lastName("Kowalewski")
                .language(Language.JAVA)
                .teacher(oldTeacher)
                .build();

        Student savedStudent = studentRepository.save(student);

        ChangeStudentTeacherCommand command = ChangeStudentTeacherCommand.builder()
                .teacherId(newTeacher.getId())
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(put("/students/{id}/teacher", savedStudent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        Student updatedStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        assertEquals(student.getFirstName(), updatedStudent.getFirstName());
        assertEquals(student.getLastName(), updatedStudent.getLastName());
        assertEquals(student.getLanguage(), updatedStudent.getLanguage());
        assertEquals(command.teacherId(), updatedStudent.getTeacher().getId());
    }

    @Test
    void testDeleteById_withNonExistingId_resultsInNotFound() throws Exception {
        Long nonExistingId = 1L;

        mockMvc.perform(delete("/students/{id}", nonExistingId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("student with Id " + nonExistingId + " not found"));
    }

    @Test
    void testSave_withTeacherNotTeachingStudentLanguage_resultsInBadRequest() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.PYTHON))
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);

        CreateStudentCommand command = CreateStudentCommand.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacherId(savedTeacher.getId())
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Teacher " + teacher.getFirstName() + " " + teacher.getLastName()
                        + " does not teach " + command.language()));

        assertEquals(0L, studentRepository.count());
    }

    @Test
    void testUpdate_withTeacherMissingLanguage_resultsInConflict() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);

        Student student = Student.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacher(savedTeacher)
                .build();

        Student savedStudent = studentRepository.save(student);

        UpdateStudentCommand command = UpdateStudentCommand.builder()
                .language(Language.KOTLIN)
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch("/students/{id}", savedStudent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Assigned teacher languages are missing"));

        Student reloadedStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        assertEquals(savedStudent.getLanguage(), reloadedStudent.getLanguage());
    }

    @Test
    void testChangeTeacher_withNewTeacherNotTeachingStudentLanguage_resultsInBadRequest() throws Exception {
        Teacher teacher1 = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA))
                .build();

        Teacher currentTeacher = teacherRepository.save(teacher1);

        Student student = Student.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacher(currentTeacher)
                .build();

        Student savedStudent = studentRepository.save(student);

        Teacher teacher2 = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.PYTHON))
                .build();

        Teacher newTeacher = teacherRepository.save(teacher2);

        ChangeStudentTeacherCommand command = ChangeStudentTeacherCommand.builder()
                .teacherId(newTeacher.getId())
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(put("/students/{id}/teacher", savedStudent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Teacher " + newTeacher.getFirstName() + " " + newTeacher.getLastName()
                        + " does not teach " + student.getLanguage()));

        Student reloadedStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        assertEquals(savedStudent.getTeacher().getId(), reloadedStudent.getTeacher().getId());
    }




}
