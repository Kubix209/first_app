package com.example.firstapp.student;

import com.example.firstapp.common.Language;
import com.example.firstapp.common.exception.LanguageMismatchException;
import com.example.firstapp.student.model.Student;
import com.example.firstapp.teacher.TeacherRepository;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class StudentServiceTest {

    @InjectMocks
    private StudentService studentService;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Captor
    private ArgumentCaptor<Student> studentCaptor;

    @Test
    void testFindAll_HappyPath_ResultsInStudentsBeingReturned() {

        //given
        List<Student> students = List.of(new Student(), new Student());
        when(studentRepository.findAllByDeletedFalse()).thenReturn(students);

        //when
        List<Student> result = studentService.findAll();

        //then
        assertEquals(2, result.size());
        assertEquals(students, result);
        verify(studentRepository).findAllByDeletedFalse();
    }

    @Test
    void testDeleteById_HappyPath_ResultsInStudentBeingDeleted() {

        //given
        Student student = Student.builder()
                .id(1L)
                .deleted(false)
                .build();
        when(studentRepository.findByIdAndDeletedFalse(student.getId())).thenReturn(Optional.of(student));

        //when
        studentService.deleteById(student.getId());

        //then
        assertTrue(student.isDeleted());
    }

    @Test
    void testSave_HappyPath_ResultsInStudentBeingSaved() {

        //given
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("xdd")
                .lastName("xdd")
                .languages(Set.of(Language.JAVA, Language.KOTLIN))
                .build();

        Student student = Student.builder()
                .firstName("xd")
                .lastName("xd")
                .language(Language.JAVA)
                .build();
        when(teacherRepository.findWithLockByIdAndDeletedFalse(teacher.getId())).thenReturn(Optional.of(teacher));

        //when
        studentService.save(student, teacher.getId());

        //then
        verify(studentRepository).save(studentCaptor.capture());
        Student result = studentCaptor.getValue();
        assertEquals(teacher, result.getTeacher());
    }

    @Test
    void testFindById_HappyPath_ResultsInStudentBeingFound() {

        //given
        Student student = Student.builder()
                .id(1L)
                .build();
        when(studentRepository.findByIdAndDeletedFalse(student.getId())).thenReturn(Optional.of(student));

        //when
        Student result = studentService.findById(student.getId());

        //then
        assertEquals(student, result);
        verify(studentRepository).findByIdAndDeletedFalse(student.getId());
    }

    @Test
    void testChangeTeacher_HappyPath_ResultsInStudentTeacherBeingChanged() {

        //given
        Teacher existingTeacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.JAVA, Language.KOTLIN))
                .build();

        Teacher teacher = Teacher.builder()
                .id(2L)
                .languages(Set.of(Language.JAVA, Language.KOTLIN))
                .build();

        Student student = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .teacher(existingTeacher)
                .build();
        when(studentRepository.findByIdAndDeletedFalse(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findWithLockByIdAndDeletedFalse(teacher.getId())).thenReturn(Optional.of(teacher));

        //when
        studentService.changeTeacher(student.getId(), teacher.getId());

        //then
        assertEquals(teacher, student.getTeacher());
    }

    @Test
    void testChangeTeacher_studentIdMissing_IllegalArgumentException() {
        Long teacherId = 1L;

        assertThrows(IllegalArgumentException.class,
                () -> studentService.changeTeacher(null, teacherId));
    }

    @Test
    void testChangeTeacher_StudentLanguageMissing_IllegalArgumentException() {
        Student student = Student.builder()
                .id(1L)
                .build();
        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.KOTLIN))
                .build();
        when(studentRepository.findByIdAndDeletedFalse(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findWithLockByIdAndDeletedFalse(teacher.getId())).thenReturn(Optional.of(teacher));

        assertThrows(IllegalArgumentException.class,
                () -> studentService.changeTeacher(student.getId(), teacher.getId()));
    }

    @Test
    void testChangeTeacher_LanguageMismatchException() {
        Teacher teacher = Teacher.builder()
                .id(2L)
                .languages(Set.of(Language.KOTLIN))
                .build();

        Student student = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .teacher(new Teacher())
                .build();
        when(studentRepository.findByIdAndDeletedFalse(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findWithLockByIdAndDeletedFalse(teacher.getId())).thenReturn(Optional.of(teacher));

        assertThrows(LanguageMismatchException.class,
                () -> studentService.changeTeacher(student.getId(), teacher.getId()));
    }

    @Test
    void testChangeTeacher_TeacherAlreadyAssigned_IllegalArgumentException() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.JAVA))
                .build();
        Student student = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .teacher(teacher)
                .build();
        when(studentRepository.findByIdAndDeletedFalse(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findWithLockByIdAndDeletedFalse(teacher.getId())).thenReturn(Optional.of(teacher));

        assertThrows(IllegalArgumentException.class,
                () -> studentService.changeTeacher(student.getId(), teacher.getId()));
    }

    @Test
    void testFindById_StudentNotFound_EntityNotFoundException() {
        Long id = 1L;
        when(studentRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> studentService.findById(id));
        verify(studentRepository).findByIdAndDeletedFalse(id);
    }

    @Test
    void testSave_TeacherNotFound_EntityNotFoundException() {
        Long id = 1L;
        when(teacherRepository.findWithLockByIdAndDeletedFalse(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> studentService.save(new Student(), id));
        verify(studentRepository, never()).save(any());
    }


}
