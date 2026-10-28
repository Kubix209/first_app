package com.example.firstapp.teacher;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.dto.TeacherDto;
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

import static org.junit.jupiter.api.Assertions.*;
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
    void testFindAll_HappyPath_ResultsInTeacherBeingReturned() {

        //given
        List<Teacher> teachers = List.of(new Teacher(), new Teacher());
        when(teacherRepository.findAllByDeletedFalse()).thenReturn(teachers);

        //when
        List<Teacher> result = teacherService.findAll();

        //then
        assertEquals(2, result.size());
        assertEquals(teachers, result);
        verify(teacherRepository).findAllByDeletedFalse();
    }

    @Test
    void testDeleteById_HappyPath_ResultsInTeacherBeingDeleted() {

        //given
        Teacher teacher = Teacher.builder()
                .id(1L)
                .deleted(false)
                .build();
        when(teacherRepository.findWithLockByIdAndDeletedFalse(teacher.getId())).thenReturn(Optional.of(teacher));

        //when
        teacherService.deleteById(teacher.getId());

        //then
        assertTrue(teacher.isDeleted());
    }

    @Test
    void testSave_HappyPath_ResultsInTeacherBeingSaved() {

        //given
        Teacher teacher = new Teacher();

        //when
        teacherService.save(teacher);

        //then
        verify(teacherRepository).save(teacherCaptor.capture());
        Teacher result = teacherCaptor.getValue();
        assertEquals(teacher, result);
    }

    @Test
    void testFindById_HappyPath_ResultsInTeacherBeingFound() {

        //given
        Teacher teacher = Teacher.builder()
                .id(1L)
                .build();
        when(teacherRepository.findWithLockByIdAndDeletedFalse(teacher.getId())).thenReturn(Optional.of(teacher));

        //when
        Teacher result = teacherService.findById(teacher.getId());

        //then
        assertEquals(teacher, result);
        verify(teacherRepository).findWithLockByIdAndDeletedFalse(teacher.getId());
    }

    @Test
    void testFindAllByLanguage_HappyPath_ResultsInTeacherDtoBeingReturned() {

        //given
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("xd")
                .lastName("xdd")
                .build();
        when(teacherRepository.findAllByLanguagesContainingAndDeletedFalse(Language.JAVA)).thenReturn(List.of(teacher));

        //when
        List<TeacherDto> teacherDtos = teacherService.findAllByLanguage(Language.JAVA);

        //then
        assertEquals(1, teacherDtos.size());
        TeacherDto result = teacherDtos.get(0);
        assertEquals(teacher.getId(), result.id());
        assertEquals(teacher.getFirstName(), result.firstName());
        assertEquals(teacher.getLastName(), result.lastName());
        verify(teacherRepository).findAllByLanguagesContainingAndDeletedFalse(Language.JAVA);
    }

    @Test
    void testFindById_TeacherNotFound_EntityNotFoundException() {
        Long id = 1L;
        when(teacherRepository.findWithLockByIdAndDeletedFalse(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> teacherService.findById(id));
        verify(teacherRepository).findWithLockByIdAndDeletedFalse(id);
    }
}
