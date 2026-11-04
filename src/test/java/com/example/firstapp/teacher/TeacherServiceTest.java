package com.example.firstapp.teacher;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.dto.CreateTeacherCommand;
import com.example.firstapp.teacher.dto.TeacherDto;
import com.example.firstapp.teacher.dto.UpdateTeacherCommand;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class TeacherServiceTest {

    @InjectMocks
    private TeacherService teacherService;
    @Mock
    private TeacherRepository teacherRepository;
    @Captor
    private ArgumentCaptor<Teacher> teacherCaptor;

    @Test
    void testFindAll_HappyPath_ResultsInTeacherDtoBeingReturned() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA))
                .deleted(false)
                .build();
        when(teacherRepository.findAllByDeletedFalse()).thenReturn(List.of(teacher));

        List<TeacherDto> teacherDtos = teacherService.findAll();

        assertEquals(1, teacherDtos.size());
        TeacherDto result = teacherDtos.get(0);
        assertEquals(teacher.getId(), result.id());
        assertEquals(teacher.getFirstName(), result.firstName());
        assertEquals(teacher.getLastName(), result.lastName());
        assertEquals(teacher.getLanguages(), result.languages());
        verify(teacherRepository).findAllByDeletedFalse();
    }

    @Test
    void testDeleteById_HappyPath_ResultsInTeacherBeingDeleted() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .deleted(false)
                .build();
        when(teacherRepository.findWithLockByIdAndDeletedFalse(teacher.getId())).thenReturn(Optional.of(teacher));

        teacherService.deleteById(teacher.getId());

        assertTrue(teacher.isDeleted());
    }

    @Test
    void testSave_HappyPath_ResultsInTeacherBeingSaved() {
        CreateTeacherCommand command = CreateTeacherCommand.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA, Language.KOTLIN, Language.PYTHON))
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA, Language.KOTLIN, Language.PYTHON))
                .build();
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        TeacherDto teacherDto = teacherService.save(command);

        verify(teacherRepository).save(teacherCaptor.capture());
        Teacher result = teacherCaptor.getValue();
        assertEquals(command.firstName(), result.getFirstName());
        assertEquals(command.lastName(), result.getLastName());
        assertEquals(command.languages(), result.getLanguages());
        assertEquals(teacher.getId(), teacherDto.id());
        assertEquals(teacher.getFirstName(), teacherDto.firstName());
        assertEquals(teacher.getLastName(), teacherDto.lastName());
        assertEquals(teacher.getLanguages(), teacherDto.languages());
    }

    @Test
    void testUpdate_HappyPath_ResultsInTeacherBeingUpdated() {
        UpdateTeacherCommand command = UpdateTeacherCommand.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName(" ")
                .lastName(" ")
                .languages(Set.of(Language.JAVA, Language.KOTLIN, Language.PYTHON))
                .build();
        when(teacherRepository.findWithLockByIdAndDeletedFalse(teacher.getId())).thenReturn(Optional.of(teacher));

        TeacherDto teacherDto = teacherService.update(command, teacher.getId());

        assertEquals(command.firstName(), teacher.getFirstName());
        assertEquals(command.lastName(), teacher.getLastName());
        assertEquals(1L, teacherDto.id());
        assertEquals(command.firstName(), teacherDto.firstName());
        assertEquals(command.lastName(), teacherDto.lastName());
        assertEquals(Set.of(Language.JAVA, Language.KOTLIN, Language.PYTHON), teacherDto.languages());
        verify(teacherRepository).findWithLockByIdAndDeletedFalse(1L);
    }

    @Test
    void testFindAllByLanguage_HappyPath_ResultsInTeacherDtoBeingReturned() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .languages(Set.of(Language.JAVA, Language.KOTLIN, Language.PYTHON))
                .build();
        when(teacherRepository.findAllByLanguagesContainingAndDeletedFalse(Language.JAVA)).thenReturn(List.of(teacher));

        List<TeacherDto> teacherDtos = teacherService.findAllByLanguage(Language.JAVA);

        assertEquals(1, teacherDtos.size());
        TeacherDto result = teacherDtos.get(0);
        assertEquals(teacher.getId(), result.id());
        assertEquals(teacher.getFirstName(), result.firstName());
        assertEquals(teacher.getLastName(), result.lastName());
        assertEquals(teacher.getLanguages(), result.languages());
        verify(teacherRepository).findAllByLanguagesContainingAndDeletedFalse(Language.JAVA);
    }

    @Test
    void testDeleteById_TeacherNotFound_EntityNotFoundException() {
        Long id = 1L;
        when(teacherRepository.findWithLockByIdAndDeletedFalse(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> teacherService.deleteById(id));
        verify(teacherRepository).findWithLockByIdAndDeletedFalse(id);
    }

    @Test
    void testUpdate_TeacherNotFound_EntityNotFoundException() {
        Long id = 1L;
        when(teacherRepository.findWithLockByIdAndDeletedFalse(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> teacherService.update(new UpdateTeacherCommand(null, null, null), id));
        verify(teacherRepository).findWithLockByIdAndDeletedFalse(id);
    }

}