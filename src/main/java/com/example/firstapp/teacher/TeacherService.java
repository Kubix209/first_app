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
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    private Teacher findById(Long id) {
        return teacherRepository.findWithLockByIdAndDeletedFalse(id).orElseThrow(() -> new EntityNotFoundException("teacher with Id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<TeacherDto> findAll() {
        return teacherRepository.findAllByDeletedFalse().stream()
                .map(TeacherDto::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteById(Long id) {
        Teacher teacher = findById(id);
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
        Teacher teacher = findById(id);
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
