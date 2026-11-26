package com.example.firstapp.lesson;

import com.example.firstapp.common.Language;
import com.example.firstapp.lesson.dto.CreateLessonCommand;
import com.example.firstapp.lesson.dto.UpdateLessonCommand;
import com.example.firstapp.lesson.model.Lesson;
import com.example.firstapp.student.StudentRepository;
import com.example.firstapp.student.model.Student;
import com.example.firstapp.teacher.TeacherRepository;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class LessonControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Test
    void testFindAll_resultsInLessonListBeingReturned() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA))
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);

        Student student = Student.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacher(savedTeacher)
                .build();

        Student savedStudent = studentRepository.save(student);

        Lesson lesson1 = Lesson.builder()
                .teacher(savedTeacher)
                .student(savedStudent)
                .dateTime(LocalDateTime.now().plusDays(1))
                .build();

        Lesson lesson2 = Lesson.builder()
                .teacher(savedTeacher)
                .student(savedStudent)
                .dateTime(LocalDateTime.now().plusDays(1))
                .build();

        lessonRepository.saveAll(List.of(lesson1, lesson2));

        mockMvc.perform(get("/lessons"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testDeleteById_resultsInLessonBeingDeleted() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA))
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);

        Student student = Student.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacher(savedTeacher)
                .build();

        Student savedStudent = studentRepository.save(student);

        Lesson lesson = Lesson.builder()
                .teacher(savedTeacher)
                .student(savedStudent)
                .dateTime(LocalDateTime.now().plusDays(1))
                .build();

        Lesson savedLesson = lessonRepository.save(lesson);

        mockMvc.perform(delete("/lessons/{id}", savedLesson.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertFalse(lessonRepository.existsById(savedLesson.getId()));
    }

    @Test
    void testCreate_resultInLessonBeingCreated() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA))
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);

        Student student = Student.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacher(savedTeacher)
                .build();

        Student savedStudent = studentRepository.save(student);

        CreateLessonCommand command = CreateLessonCommand.builder()
                .teacherId(savedTeacher.getId())
                .studentId(savedStudent.getId())
                .dateTime(LocalDateTime.now().plusDays(1).withNano(0))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        String expectedDateTime = command.dateTime().format(DateTimeFormatter.ISO_DATE_TIME);

        mockMvc.perform(post("/lessons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teacherId").value(command.teacherId()))
                .andExpect(jsonPath("$.studentId").value(command.studentId()))
                .andExpect(jsonPath("$.dateTime").value(expectedDateTime));

        List<Lesson> lessons = lessonRepository.findAll();
        assertEquals(1, lessons.size());
        Lesson savedLesson = lessons.get(0);
        assertEquals(command.teacherId(), savedLesson.getTeacher().getId());
        assertEquals(command.studentId(), savedLesson.getStudent().getId());
        assertEquals(command.dateTime(), savedLesson.getDateTime());
    }

    @Test
    void testUpdate_resultsInLessonBeingUpdate() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA))
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);

        Student student = Student.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacher(savedTeacher)
                .build();

        Student savedStudent = studentRepository.save(student);

        Lesson lesson = Lesson.builder()
                .teacher(savedTeacher)
                .student(savedStudent)
                .dateTime(LocalDateTime.now().plusDays(1).withNano(0))
                .build();

        Lesson savedLesson = lessonRepository.save(lesson);

        UpdateLessonCommand command = UpdateLessonCommand.builder()
                .dateTime(LocalDateTime.of(2030, 1, 1, 10, 10))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        String expectedDateTime = command.dateTime().format(DateTimeFormatter.ISO_DATE_TIME);

        mockMvc.perform(patch("/lessons/{id}", savedLesson.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateTime").value(expectedDateTime));

        Lesson updatedLesson = lessonRepository.findById(savedLesson.getId()).orElseThrow();
        assertEquals(lesson.getTeacher().getId(), updatedLesson.getTeacher().getId());
        assertEquals(lesson.getStudent().getId(), updatedLesson.getStudent().getId());
        assertEquals(command.dateTime(), updatedLesson.getDateTime());
    }

    @Test
    void testCreate_withTeacherDateUnavailable_resultsInConflict() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA))
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);

        Student student = Student.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacher(savedTeacher)
                .build();

        Student savedStudent = studentRepository.save(student);

        LocalDateTime dateTime = LocalDateTime.now().plusDays(1).withNano(0);

        Lesson lesson = Lesson.builder()
                .teacher(savedTeacher)
                .student(savedStudent)
                .dateTime(dateTime)
                .build();

        lessonRepository.save(lesson);

        CreateLessonCommand command = CreateLessonCommand.builder()
                .teacherId(savedTeacher.getId())
                .studentId(savedStudent.getId())
                .dateTime(dateTime)
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post("/lessons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Lesson date is unavailable"));

        assertEquals(1L, lessonRepository.count());
    }

    @Test
    void testCreate_withTeacherNotAssignedToStudent_resultsInConflict() throws Exception {
        Teacher teacher1 = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA))
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher1);

        Student student = Student.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacher(savedTeacher)
                .build();

        Student savedStudent = studentRepository.save(student);

        Teacher teacher2 = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA))
                .build();

        Teacher newTeacher = teacherRepository.save(teacher2);

        CreateLessonCommand command = CreateLessonCommand.builder()
                .teacherId(newTeacher.getId())
                .studentId(savedStudent.getId())
                .dateTime(LocalDateTime.now().plusDays(1).withNano(0))
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post("/lessons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Teacher with id " + newTeacher.getId() + " is not assigned to student with id " + savedStudent.getId()));

        assertEquals(0L, lessonRepository.count());
    }

    @Test
    void testUpdate_withTeacherDateUnavailable_resultsInConflict() throws Exception {
        Teacher teacher = Teacher.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA))
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);

        Student student = Student.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacher(savedTeacher)
                .build();

        Student savedStudent = studentRepository.save(student);

        LocalDateTime firstLessonDateTime = LocalDateTime.now().plusDays(1).withNano(0);

        Lesson lesson1 = Lesson.builder()
                .teacher(savedTeacher)
                .student(savedStudent)
                .dateTime(firstLessonDateTime)
                .build();

        Lesson firstLesson = lessonRepository.save(lesson1);

        Lesson lesson2 = Lesson.builder()
                .teacher(savedTeacher)
                .student(savedStudent)
                .dateTime(LocalDateTime.now().plusDays(1).plusHours(2).withNano(0))
                .build();

        Lesson secondLesson = lessonRepository.save(lesson2);

        UpdateLessonCommand command = UpdateLessonCommand.builder()
                .dateTime(firstLessonDateTime)
                .build();

        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch("/lessons/{id}", secondLesson.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Lesson date is unavailable"));

        Lesson reloadedFirstLesson = lessonRepository.findById(firstLesson.getId()).orElseThrow();
        assertEquals(firstLesson.getDateTime(), reloadedFirstLesson.getDateTime());
        Lesson reloadedSecondLesson = lessonRepository.findById(secondLesson.getId()).orElseThrow();
        assertEquals(secondLesson.getDateTime(), reloadedSecondLesson.getDateTime());
    }
}
