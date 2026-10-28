package com.example.firstapp.teacher;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.dto.TeacherDto;
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

    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return teacherRepository.findAllByDeletedFalse();
    }

    @Transactional
    public void deleteById(Long id) {
        Teacher teacher = findById(id);
        teacher.setDeleted(true);
    }

    @Transactional
    public void save(Teacher teacher) {
        teacherRepository.save(teacher);
    }

    @Transactional(readOnly = true)
    public Teacher findById(Long id) {
        return teacherRepository.findWithLockByIdAndDeletedFalse(id).orElseThrow(() -> new EntityNotFoundException("teacher with Id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<TeacherDto> findAllByLanguage(Language language) {
      return teacherRepository.findAllByLanguagesContainingAndDeletedFalse(language).stream()
                .map(teacher -> new TeacherDto(
                        teacher.getId(),
                        teacher.getFirstName(),
                        teacher.getLastName()
                ))
                .toList();
    }
}
