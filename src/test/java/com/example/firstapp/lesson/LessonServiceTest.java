package com.example.firstapp.lesson;

import com.example.firstapp.common.Language;
import com.example.firstapp.common.exception.LanguageMismatchException;
import com.example.firstapp.common.exception.LessonDateUnavailableException;
import com.example.firstapp.common.exception.LessonInPastException;
import com.example.firstapp.lesson.model.Lesson;
import com.example.firstapp.student.StudentRepository;
import com.example.firstapp.student.model.Student;
import com.example.firstapp.teacher.TeacherRepository;
import com.example.firstapp.teacher.model.Teacher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class LessonServiceTest {

    @InjectMocks
    private LessonService lessonService;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Captor
    private ArgumentCaptor<Lesson> lessonCaptor;

    @Test
    void testFindAll_HappyPath_ResultsInLessonBeingReturned() {
        //given
        List<Lesson> lessons = List.of(new Lesson(), new Lesson());
        when(lessonRepository.findAll()).thenReturn(lessons);

        //when
        List<Lesson> result = lessonService.findAll();

        //then
        assertEquals(2, result.size());
        assertEquals(lessons, result);
        verify(lessonRepository).findAll();

    }

    @Test
    void testSave_HappyPath_ResultsInLessonBeingSaved() {
        //given
        Student student = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.JAVA, Language.KOTLIN))
                .build();

        Lesson lesson = Lesson.builder()
                .dateTime(LocalDateTime.now().plusHours(1))
                .build();
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findWithLockById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThan(any(), any(), any())).thenReturn(false);

        //when
        lessonService.save(lesson, student.getId(), teacher.getId());

        //then
        verify(lessonRepository).save(lessonCaptor.capture());
        Lesson savedLesson = lessonCaptor.getValue();
        assertEquals(student, savedLesson.getStudent());
        assertEquals(teacher, savedLesson.getTeacher());
    }

    @Test
    void testDeleteById_HappyPath_ResultsInLessonBeingDeleted() {

        //given
        Lesson lesson = Lesson.builder()
                .id(1L)
                .build();

        //when
        lessonService.deleteById(lesson.getId());

        //then
        verify(lessonRepository).deleteById(lesson.getId());
    }

    @Test
    void testFindById_HappyPath_ResultsInLessonBeingFound() {

        //given
        Lesson lesson = Lesson.builder()
                .id(1L)
                .build();
        when(lessonRepository.findById(lesson.getId())).thenReturn(Optional.of(lesson));

        //when
        Lesson result = lessonService.findById(lesson.getId());

        //then
        assertEquals(lesson, result);
        verify(lessonRepository).findById(lesson.getId());
    }

    @Test
    void testUpdate_HappyPath_ResultsInLessonBeingUpdated() {

        //given
        Lesson existingLesson = Lesson.builder()
                .id(1L)
                .dateTime(LocalDateTime.now().plusHours(1))
                .build();

        Student student = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.JAVA, Language.KOTLIN))
                .build();

        Lesson lesson = Lesson.builder()
                .dateTime(LocalDateTime.now().plusDays(1))
                .build();
        when(lessonRepository.findWithLockById(existingLesson.getId())).thenReturn(Optional.of(existingLesson));
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findWithLockById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThanAndIdNot(any(), any(), any(), any())).thenReturn(false);

        //when
        lessonService.update(existingLesson.getId(), lesson, student.getId(), teacher.getId());

        //then
        assertEquals(1L, existingLesson.getId());
        assertEquals(lesson.getDateTime(), existingLesson.getDateTime());
        assertEquals(student, existingLesson.getStudent());
        assertEquals(teacher, existingLesson.getTeacher());
    }

    @Test
    void testChangeDateTime_HappyPath_ResultsInLessonDateTimeBeingChanged() {

        //given
        Teacher teacher = Teacher.builder()
                .id(1L)
                .build();

        Lesson existingLesson = Lesson.builder()
                .id(1L)
                .teacher(teacher)
                .dateTime(LocalDateTime.now().plusHours(1))
                .build();

        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);

        when(lessonRepository.findWithLockById(existingLesson.getId())).thenReturn(Optional.of(existingLesson));
        when(teacherRepository.findWithLockById(existingLesson.getTeacher().getId())).thenReturn(Optional.of(teacher));
        when(lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThanAndIdNot(any(), any(), any(), any())).thenReturn(false);

        //when
        lessonService.changeDateTime(existingLesson.getId(), dateTime);

        //then
        assertEquals(dateTime, existingLesson.getDateTime());
    }

    @Test
    void testSave_MissingData_IllegalArgumentException() {
        Lesson lesson = new Lesson();

        Student student = Student.builder()
                .id(1L)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> lessonService.save(lesson, student.getId(), teacher.getId()));
        verify(lessonRepository, never()).save(any());
    }

    @Test
    void testSave_DateUnavailable_LessonInPastException() {
        Student student = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.KOTLIN))
                .build();

        Lesson lesson = Lesson.builder()
                .dateTime(LocalDateTime.now().minusDays(1))
                .build();

        assertThrows(LessonInPastException.class, () -> lessonService.save(lesson, student.getId(), teacher.getId()));
        verify(lessonRepository, never()).save(any());
    }

    @Test
    void testSave_LessonIdProvided_IllegalArgumentException() {
        Lesson lesson = Lesson.builder()
                .id(1L)
                .dateTime(LocalDateTime.now().plusHours(1))
                .build();

        Long studentId = 1L;
        Long teacherId = 1L;

        assertThrows(IllegalArgumentException.class,
                () -> lessonService.save(lesson, studentId, teacherId));
        verify(lessonRepository, never()).save(any());
    }

    @Test
    void testSave_TermUnavailable_LessonDateUnavailableException() {
        Student student = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.JAVA, Language.KOTLIN))
                .build();

        Lesson lesson = Lesson.builder()
                .dateTime(LocalDateTime.now().plusHours(1))
                .build();
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findWithLockById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThan(any(), any(), any())).thenReturn(true);

        assertThrows(LessonDateUnavailableException.class,
                () -> lessonService.save(lesson, student.getId(), teacher.getId()));
        verify(lessonRepository, never()).save(any());
    }

    @Test
    void testSave_StudentLanguageMissing_IllegalArgumentException() {
        Lesson lesson = Lesson.builder()
                .dateTime(LocalDateTime.now().plusHours(1))
                .build();

        Student student = Student.builder()
                .id(1L)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.JAVA))
                .build();
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findWithLockById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThan(any(), any(), any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> lessonService.save(lesson, student.getId(), teacher.getId()));
        verify(lessonRepository, never()).save(any());
    }

    @Test
    void testSave_LanguageMismatchException() {
        Student student = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.KOTLIN))
                .build();

        Lesson lesson = Lesson.builder()
                .dateTime(LocalDateTime.now().plusHours(1))
                .build();
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findWithLockById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThan(any(), any(), any())).thenReturn(false);

        assertThrows(LanguageMismatchException.class,
                () -> lessonService.save(lesson, student.getId(), teacher.getId()));
        verify(lessonRepository, never()).save(any());
    }

    @Test
    void testUpdate_IdMissing_IllegalArgumentException() {
        Lesson lesson = Lesson.builder()
                .dateTime(LocalDateTime.now().plusHours(1))
                .build();

        Long studentId = 1L;
        Long teacherId = 1L;

        assertThrows(IllegalArgumentException.class,
                () -> lessonService.update(null, lesson, studentId, teacherId));
    }

    @Test
    void testUpdate_DateInPast_LessonInPastException() {
        Long id = 1L;
        Lesson lesson = Lesson.builder()
                .dateTime(LocalDateTime.now().minusDays(1))
                .build();
        Long studentId = 1L;
        Long teacherId = 1L;
        when(lessonRepository.findWithLockById(id)).thenReturn(Optional.of(new Lesson()));

        assertThrows(LessonInPastException.class,
                () -> lessonService.update(id, lesson, studentId, teacherId));
    }

    @Test
    void testUpdate_TermUnavailable_LessonDateUnavailableException() {
        Long id = 1L;
        Lesson lesson = Lesson.builder()
                .dateTime(LocalDateTime.now().plusDays(1))
                .build();

        Student student = Student.builder()
                .id(1L)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .build();
        when(lessonRepository.findWithLockById(id)).thenReturn(Optional.of(new Lesson()));
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findWithLockById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThanAndIdNot(any(), any(), any(), any())).thenReturn(true);

        assertThrows(LessonDateUnavailableException.class,
                () -> lessonService.update(id, lesson, student.getId(), teacher.getId()));
    }

    @Test
    void testUpdate_TeacherLanguageIsMissing_IllegalArgumentException() {
        Long id = 1L;
        Lesson lesson = Lesson.builder()
                .dateTime(LocalDateTime.now().plusHours(1))
                .build();
        Student student = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .build();
        Teacher teacher = Teacher.builder()
                .id(1L)
                .build();
        when(lessonRepository.findWithLockById(id)).thenReturn(Optional.of(new Lesson()));
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findWithLockById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThanAndIdNot(any(), any(), any(), any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> lessonService.update(id, lesson, student.getId(), teacher.getId()));
    }

    @Test
    void testUpdate_LanguageMismatchException() {
        Long id = 1L;
        Lesson lesson = Lesson.builder()
                .dateTime(LocalDateTime.now().plusHours(1))
                .build();
        Student student = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .build();
        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.KOTLIN))
                .build();
        when(lessonRepository.findWithLockById(id)).thenReturn(Optional.of(new Lesson()));
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findWithLockById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThanAndIdNot(any(), any(), any(), any())).thenReturn(false);

        assertThrows(LanguageMismatchException.class,
                () -> lessonService.update(id, lesson, student.getId(), teacher.getId()));
    }

    @Test
    void testChangeDateTime_DateInPast_LessonInPastException() {
        Lesson lesson = Lesson.builder()
                .id(1L)
                .dateTime(LocalDateTime.now().minusDays(1))
                .build();


        assertThrows(LessonInPastException.class,
                () -> lessonService.changeDateTime(lesson.getId(), lesson.getDateTime()));
    }

    @Test
    void testChangeDateTime_TermUnavailable_LessonDateUnavailableException() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .build();

        Lesson lesson = Lesson.builder()
                .id(1L)
                .teacher(teacher)
                .dateTime(LocalDateTime.now().plusDays(1))
                .build();
        when(lessonRepository.findWithLockById(lesson.getId())).thenReturn(Optional.of(lesson));
        when(teacherRepository.findWithLockById(lesson.getTeacher().getId())).thenReturn(Optional.of(teacher));
        when(lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThanAndIdNot(any(), any(), any(), any())).thenReturn(true);

        assertThrows(LessonDateUnavailableException.class,
                () -> lessonService.changeDateTime(lesson.getId(), lesson.getDateTime()));
    }


}
