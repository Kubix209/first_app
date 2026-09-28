package com.example.firstapp.teacher;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.dto.TeacherDto;
import com.example.firstapp.teacher.model.Teacher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        when(teacherRepository.findAll()).thenReturn(teachers);

        //when
        List<Teacher> result = teacherService.findAll();

        //then
        assertEquals(2, result.size());
        assertEquals(teachers, result);
        verify(teacherRepository).findAll();
    }

    @Test
    void testDeleteById_HappyPath_ResultsInTeacherBeingDeleted() {

        //given
        Long id = 1L;

        //when
        teacherService.deleteById(id);

        //then
        verify(teacherRepository).deleteById(id);
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
        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

        //when
        Teacher result = teacherService.findById(teacher.getId());

        //then
        assertEquals(teacher, result);
        verify(teacherRepository).findById(teacher.getId());
    }

    @Test
    void testFindAllByLanguage_HappyPath_ResultsInTeacherDtoBeingReturned() {

        //given
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("xd")
                .lastName("xdd")
                .build();
        when(teacherRepository.findAllByLanguagesContaining(Language.JAVA)).thenReturn(List.of(teacher));

        //when
        List<TeacherDto> teacherDtos = teacherService.findAllByLanguage(Language.JAVA);

        //then
        assertEquals(1, teacherDtos.size());
        TeacherDto result = teacherDtos.get(0);
        assertEquals(teacher.getId(), result.id());
        assertEquals(teacher.getFirstName(), result.firstName());
        assertEquals(teacher.getLastName(), result.lastName());
        verify(teacherRepository).findAllByLanguagesContaining(Language.JAVA);
    }
}
