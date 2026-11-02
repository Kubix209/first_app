package com.example.firstapp.teacher;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.dto.CreateTeacherCommand;
import com.example.firstapp.teacher.dto.TeacherDto;
import com.example.firstapp.teacher.dto.UpdateTeacherCommand;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public TeacherDto findById(Long id) {
        Teacher teacher = teacherRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new EntityNotFoundException("teacher with Id " + id + " not found"));
        return TeacherDto.fromEntity(teacher);
    }

    @Transactional(readOnly = true)
    public List<TeacherDto> findAll() {
        return teacherRepository.findAllByDeletedFalse().stream()
                .map(TeacherDto::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteById(Long id) {
        Teacher teacher = teacherRepository.findWithLockByIdAndDeletedFalse(id).orElseThrow(() -> new EntityNotFoundException("teacher with Id " + id + " not found"));
        teacher.setDeleted(true);
    }

    @Transactional
    public TeacherDto save(CreateTeacherCommand command) {
        Teacher teacher = CreateTeacherCommand.toEntity(command);
        Teacher savedTeacher = teacherRepository.save(teacher);
        return TeacherDto.fromEntity(savedTeacher);
    }

    @Transactional
    public TeacherDto update(UpdateTeacherCommand command, Long id) {
        Teacher teacher = teacherRepository.findWithLockByIdAndDeletedFalse(id).orElseThrow(() -> new EntityNotFoundException("teacher with Id " + id + " not found"));
        Teacher updatedTeacher = UpdateTeacherCommand.updateEntity(command, teacher);
        return TeacherDto.fromEntity(updatedTeacher);
    }

    @Transactional(readOnly = true)
    public List<TeacherDto> findAllByLanguage(Language language) {
      return teacherRepository.findAllByLanguagesContainingAndDeletedFalse(language).stream()
                .map(TeacherDto::fromEntity)
                .toList();
    }
}
