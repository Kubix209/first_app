package com.example.firstapp.teacher;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.dto.TeacherDto;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    public void deleteById(Long id) {
        teacherRepository.deleteById(id);
    }

    public void save(Teacher teacher) {
        teacherRepository.save(teacher);
    }

    public Teacher findById(Long id) {
        return teacherRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("teacher with Id " + id + " not found"));
    }

    public List<TeacherDto> findAllByLanguage(Language language) {
      return teacherRepository.findAllByLanguagesContaining(language).stream()
                .map(teacher -> new TeacherDto(
                        teacher.getId(),
                        teacher.getFirstName(),
                        teacher.getLastName()
                ))
                .toList();
    }
}
