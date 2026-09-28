package com.example.firstapp.lesson;

import com.example.firstapp.common.Language;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
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
                .student(student)
                .teacher(teacher)
                .build();
        when(lessonRepository.findById(existingLesson.getId())).thenReturn(Optional.of(existingLesson));
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThanAndIdNot(any(), any(), any(), any())).thenReturn(false);

        //when
        lessonService.update(existingLesson.getId(), lesson, student.getId(), teacher.getId());

        //then
        verify(lessonRepository).save(lessonCaptor.capture());
        Lesson result = lessonCaptor.getValue();
        assertEquals(1L, result.getId());
        assertEquals(lesson.getDateTime(), result.getDateTime());
        assertEquals(student, result.getStudent());
        assertEquals(teacher, result.getTeacher());
    }

    @Test
    void testChangeDateTime_HappyPath_ResultsInLessonDateTimeBeingChanged() {

        //given
        Lesson existingLesson = Lesson.builder()
                .id(1L)
                .dateTime(LocalDateTime.now().plusHours(1))
                .build();

        Lesson lesson = Lesson.builder()
                .dateTime(LocalDateTime.now().plusDays(1))
                .build();
        when(lessonRepository.findById(existingLesson.getId())).thenReturn(Optional.of(existingLesson));
        when(lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThanAndIdNot(any(), any(), any(), any())).thenReturn(false);

        //when
        lessonService.changeDateTime(existingLesson.getId(), lesson.getDateTime());

        //then
        verify(lessonRepository).save(lessonCaptor.capture());
        Lesson result = lessonCaptor.getValue();
        assertEquals(1L, result.getId());
        assertEquals(lesson.getDateTime(), result.getDateTime());
    }


}
