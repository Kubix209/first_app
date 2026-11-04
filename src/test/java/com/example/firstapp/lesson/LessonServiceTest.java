package com.example.firstapp.lesson;

import com.example.firstapp.common.Language;
import com.example.firstapp.common.exception.LanguageMismatchException;
import com.example.firstapp.common.exception.LessonDateUnavailableException;
import com.example.firstapp.common.exception.LessonInPastException;
import com.example.firstapp.common.exception.TeacherNotAssignedToStudentException;
import com.example.firstapp.lesson.dto.CreateLessonCommand;
import com.example.firstapp.lesson.dto.LessonDto;
import com.example.firstapp.lesson.dto.UpdateLessonCommand;
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
    void testFindAll_HappyPath_ResultsInLessonDtoBeingReturned() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .build();

        Student student = Student.builder()
                .id(1L)
                .build();

        Lesson lesson = Lesson.builder()
                .id(1L)
                .teacher(teacher)
                .student(student)
                .dateTime(LocalDateTime.of(2077, 12, 10, 21, 37))
                .build();
        when(lessonRepository.findAll()).thenReturn(List.of(lesson));

        List<LessonDto> lessonDtos = lessonService.findAll();

        assertEquals(1, lessonDtos.size());
        LessonDto result = lessonDtos.get(0);
        assertEquals(lesson.getId(), result.id());
        assertEquals(lesson.getTeacher().getId(), result.teacherId());
        assertEquals(lesson.getStudent().getId(), result.studentId());
        assertEquals(lesson.getDateTime(), result.dateTime());
        verify(lessonRepository).findAll();
    }

    @Test
    void testDeleteById_HappyPath_ResultsInLessonBeingDeleted() {
        Lesson lesson = Lesson.builder()
                .id(1L)
                .build();

        lessonService.deleteById(lesson.getId());

        verify(lessonRepository).deleteById(lesson.getId());
    }

    @Test
    void testSave_HappyPath_ResultsInLessonBeingSaved() {
        CreateLessonCommand command = CreateLessonCommand.builder()
                .teacherId(1L)
                .studentId(1L)
                .dateTime(LocalDateTime.of(2077, 12, 10, 21, 37))
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.JAVA, Language.KOTLIN))
                .build();

        Student student = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .teacher(teacher)
                .build();

        Lesson lesson = Lesson.builder()
                .id(1L)
                .teacher(teacher)
                .student(student)
                .dateTime(LocalDateTime.of(2077, 12, 10, 21, 37))
                .build();
        when(teacherRepository.findWithLockByIdAndDeletedFalse(command.teacherId())).thenReturn(Optional.of(teacher));
        when(studentRepository.findByIdAndDeletedFalse(command.studentId())).thenReturn(Optional.of(student));
        when(lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThan(any(), any(), any())).thenReturn(false);
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);

        LessonDto lessonDto = lessonService.save(command);

        verify(lessonRepository).save(lessonCaptor.capture());
        Lesson result = lessonCaptor.getValue();
        assertEquals(command.teacherId(), result.getTeacher().getId());
        assertEquals(command.studentId(), result.getStudent().getId());
        assertEquals(command.dateTime(), result.getDateTime());
        assertEquals(lesson.getId(), lessonDto.id());
        assertEquals(lesson.getTeacher().getId(), lessonDto.teacherId());
        assertEquals(lesson.getStudent().getId(), lessonDto.studentId());
        assertEquals(lesson.getDateTime(), lessonDto.dateTime());
    }

    @Test
    void testUpdate_HappyPath_ResultsInLessonBeingUpdated() {
        UpdateLessonCommand command = UpdateLessonCommand.builder()
                .teacherId(1L)
                .studentId(1L)
                .dateTime(LocalDateTime.of(2077, 12, 10, 21, 37))
                .build();

        Teacher teacher = Teacher.builder()
                .id(99L)
                .languages(Set.of(Language.JAVA))
                .build();

        Student student = Student.builder()
                .id(99L)
                .language(Language.JAVA)
                .teacher(teacher)
                .build();

        Lesson lesson = Lesson.builder()
                .id(1L)
                .teacher(teacher)
                .student(student)
                .dateTime(LocalDateTime.now().plusHours(1))
                .build();

        Teacher updatedTeacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.JAVA))
                .build();

        Student updatedStudent = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .teacher(updatedTeacher)
                .build();

        when(lessonRepository.findWithLockById(lesson.getId())).thenReturn(Optional.of(lesson));
        when(teacherRepository.findWithLockByIdAndDeletedFalse(command.teacherId())).thenReturn(Optional.of(updatedTeacher));
        when(studentRepository.findByIdAndDeletedFalse(command.studentId())).thenReturn(Optional.of(updatedStudent));
        when(lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThanAndIdNot(any(), any(), any(), any())).thenReturn(false);

        LessonDto lessonDto = lessonService.update(lesson.getId(), command);

        assertEquals(command.teacherId(), lessonDto.teacherId());
        assertEquals(command.studentId(), lessonDto.studentId());
        assertEquals(command.dateTime(), lessonDto.dateTime());
        assertEquals(command.teacherId(), lesson.getTeacher().getId());
        assertEquals(command.studentId(), lesson.getStudent().getId());
        assertEquals(command.dateTime(), lesson.getDateTime());
    }

    @Test
    void testSave_LessonDateUnavailableException() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.JAVA))
                .build();

        Student student = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .teacher(teacher)
                .build();

        CreateLessonCommand command = CreateLessonCommand.builder()
                .teacherId(1L)
                .studentId(1L)
                .dateTime(LocalDateTime.of(2077, 12, 10, 21, 37))
                .build();
        when(teacherRepository.findWithLockByIdAndDeletedFalse(command.teacherId())).thenReturn(Optional.of(teacher));
        when(studentRepository.findByIdAndDeletedFalse(command.studentId())).thenReturn(Optional.of(student));
        when(lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThan(any(), any(), any())).thenReturn(true);

        assertThrows(LessonDateUnavailableException.class,
                () -> lessonService.save(command));
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    void testUpdate_TeacherNotAssignedToStudentException() {
        UpdateLessonCommand command = UpdateLessonCommand.builder()
                .teacherId(99L)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.JAVA))
                .build();

        Student student = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .teacher(teacher)
                .build();

        Teacher updatedTeacher = Teacher.builder()
                .id(99L)
                .languages(Set.of(Language.JAVA))
                .build();

        Lesson lesson = Lesson.builder()
                .id(1L)
                .teacher(teacher)
                .student(student)
                .dateTime(LocalDateTime.of(2077, 12, 10, 21, 37))
                .build();
        when(lessonRepository.findWithLockById(lesson.getId())).thenReturn(Optional.of(lesson));
        when(teacherRepository.findWithLockByIdAndDeletedFalse(command.teacherId())).thenReturn(Optional.of(updatedTeacher));
        when((studentRepository.findByIdAndDeletedFalse(student.getId()))).thenReturn(Optional.of(student));

        assertThrows(TeacherNotAssignedToStudentException.class,
                () -> lessonService.update(lesson.getId(), command));
        assertEquals(teacher.getId(), lesson.getTeacher().getId());
    }



}
