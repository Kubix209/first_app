package com.example.firstapp.student;

import com.example.firstapp.common.Language;
import com.example.firstapp.common.exception.LanguageMismatchException;
import com.example.firstapp.student.dto.ChangeStudentTeacherCommand;
import com.example.firstapp.student.dto.CreateStudentCommand;
import com.example.firstapp.student.dto.StudentDto;
import com.example.firstapp.student.dto.UpdateStudentCommand;
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
    void testFindAll_HappyPath_ResultsInStudentDtoBeingReturned() {
        Student student = Student.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .deleted(false)
                .build();
        when(studentRepository.findAllByDeletedFalse()).thenReturn(List.of(student));

        List<StudentDto> studentDtos = studentService.findAll();

        assertEquals(1, studentDtos.size());
        StudentDto result = studentDtos.get(0);
        assertEquals(student.getId(), result.id());
        assertEquals(student.getFirstName(), result.firstName());
        assertEquals(student.getLastName(), result.lastName());
        assertEquals(student.getLanguage(), result.language());
        verify(studentRepository).findAllByDeletedFalse();
    }

    @Test
    void testDeleteById_HappyPath_ResultsInStudentBeingDeleted() {
        Student student = Student.builder()
                .id(1L)
                .deleted(false)
                .build();
        when(studentRepository.findByIdAndDeletedFalse(student.getId())).thenReturn(Optional.of(student));

        studentService.deleteById(student.getId());

        assertTrue(student.isDeleted());
    }

    @Test
    void testSave_HappyPath_ResultsInStudentBeingSaved() {
        CreateStudentCommand command = CreateStudentCommand.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacherId(1L)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.JAVA))
                .build();

        Student student = Student.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacher(teacher)
                .build();
        when(teacherRepository.findWithLockByIdAndDeletedFalse(command.teacherId())).thenReturn(Optional.of(teacher));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentDto studentDto = studentService.save(command);

        verify(studentRepository).save(studentCaptor.capture());
        Student result = studentCaptor.getValue();
        assertEquals(command.firstName(), result.getFirstName());
        assertEquals(command.lastName(), result.getLastName());
        assertEquals(command.language(), result.getLanguage());
        assertEquals(command.teacherId(), result.getTeacher().getId());
        assertEquals(student.getId(), studentDto.id());
        assertEquals(student.getFirstName(), studentDto.firstName());
        assertEquals(student.getLastName(), studentDto.lastName());
        assertEquals(student.getLanguage(), studentDto.language());
    }

    @Test
    void testUpdate_HappyPath_ResultsInStudentBeingUpdated() {
        UpdateStudentCommand command = UpdateStudentCommand.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.JAVA))
                .build();

        Student student = Student.builder()
                .id(1L)
                .firstName(" ")
                .lastName(" ")
                .language(Language.JAVA)
                .teacher(teacher)
                .build();
        when(studentRepository.findByIdAndDeletedFalse(student.getId())).thenReturn(Optional.of(student));

        StudentDto studentDto = studentService.update(student.getId(), command);

        assertEquals(command.firstName(), student.getFirstName());
        assertEquals(command.lastName(), student.getLastName());
        assertEquals(command.language(), student.getLanguage());
        assertEquals(1L, studentDto.id());
        assertEquals(command.firstName(), studentDto.firstName());
        assertEquals(command.lastName(), studentDto.lastName());
        assertEquals(command.language(), studentDto.language());
    }

    @Test
    void testChangeTeacher_HappyPath_ResultsInStudentTeacherBeingChanged() {

        Teacher existingTeacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.JAVA, Language.KOTLIN))
                .build();

        ChangeStudentTeacherCommand command = ChangeStudentTeacherCommand.builder()
                .teacherId(2L)
                .build();

        Teacher teacher = Teacher.builder()
                .id(2L)
                .languages(Set.of(Language.JAVA))
                .build();

        Student student = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .teacher(existingTeacher)
                .build();
        when(studentRepository.findByIdAndDeletedFalse(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findWithLockByIdAndDeletedFalse(command.teacherId())).thenReturn(Optional.of(teacher));

        studentService.changeTeacher(student.getId(), command);

        assertEquals(command.teacherId(), student.getTeacher().getId());
    }

    @Test
    void testDeleteById_StudentNotFound_EntityNotFoundException() {
        Student student = Student.builder()
                .id(1L)
                .deleted(false)
                .build();
        when(studentRepository.findByIdAndDeletedFalse(student.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> studentService.deleteById(student.getId()));
        verify(studentRepository).findByIdAndDeletedFalse(student.getId());
    }

    @Test
    void testSave_LanguageMismatchException() {
        CreateStudentCommand command = CreateStudentCommand.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacherId(1L)
                .build();

        Teacher teacher = Teacher.builder()
                .id(command.teacherId())
                .firstName("teacher")
                .lastName("teacher")
                .languages(Set.of(Language.KOTLIN))
                .build();
        when(teacherRepository.findWithLockByIdAndDeletedFalse(command.teacherId())).thenReturn(Optional.of(teacher));

        assertThrows(LanguageMismatchException.class,
                () -> studentService.save(command));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testUpdate_LanguageMismatchException() {
        UpdateStudentCommand command = UpdateStudentCommand.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.KOTLIN)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.JAVA))
                .build();

        Student student = Student.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .language(Language.JAVA)
                .teacher(teacher)
                .build();
        when(studentRepository.findByIdAndDeletedFalse(student.getId())).thenReturn(Optional.of(student));

        assertThrows(LanguageMismatchException.class,
                () -> studentService.update(student.getId(), command));
    }

    @Test
    void testChangeTeacher_LanguageMismatchException() {
        ChangeStudentTeacherCommand command = ChangeStudentTeacherCommand.builder()
                .teacherId(1L)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .languages(Set.of(Language.KOTLIN))
                .build();

        Student student = Student.builder()
                .id(1L)
                .language(Language.JAVA)
                .teacher(new Teacher())
                .build();
        when(studentRepository.findByIdAndDeletedFalse(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findWithLockByIdAndDeletedFalse(command.teacherId())).thenReturn(Optional.of(teacher));

        assertThrows(LanguageMismatchException.class,
                () -> studentService.changeTeacher(student.getId(), command));
    }

}