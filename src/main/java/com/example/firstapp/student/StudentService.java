package com.example.firstapp.student;

import com.example.firstapp.common.exception.LanguageMismatchException;
import com.example.firstapp.student.dto.ChangeStudentTeacherCommand;
import com.example.firstapp.student.dto.CreateStudentCommand;
import com.example.firstapp.student.dto.StudentDto;
import com.example.firstapp.student.dto.UpdateStudentCommand;
import com.example.firstapp.student.model.Student;
import com.example.firstapp.teacher.TeacherRepository;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    private Student findById(Long id) {
        return studentRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new EntityNotFoundException("student with Id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<StudentDto> findAll() {
        return studentRepository.findAllByDeletedFalse().stream()
                .map(StudentDto::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteById(Long id) {
        Student student = findById(id);
        student.setDeleted(true);
    }

    @Transactional
    public StudentDto save(CreateStudentCommand command) {

        Teacher teacher = teacherRepository.findWithLockByIdAndDeletedFalse(command.teacherId()).orElseThrow(() -> new EntityNotFoundException("teacher with Id " + command.teacherId() + " not found"));
        Student student = CreateStudentCommand.toEntity(command, teacher);

        if (student.getLanguage() == null || teacher.getLanguages() == null) {
            throw new IllegalArgumentException("Student language or teacher languages are missing");
        }

        if (!teacher.getLanguages().contains(student.getLanguage())) {
            throw new LanguageMismatchException("Teacher " + teacher.getFirstName() + " " + teacher.getLastName()
                    + " does not teach " + student.getLanguage());
        }

        Student savedStudent = studentRepository.save(student);
        return StudentDto.fromEntity(savedStudent);
    }

    @Transactional
    public StudentDto update(Long id, UpdateStudentCommand command) {

        Student student = findById(id);
        Student updatedStudent = UpdateStudentCommand.updateEntity(command, student);

        if (student.getLanguage() == null || student.getTeacher().getLanguages() == null) {
            throw new IllegalArgumentException("Student language or teacher languages are missing");
        }

        if (!student.getTeacher().getLanguages().contains(student.getLanguage())) {
            throw new LanguageMismatchException("Teacher " + student.getTeacher().getFirstName() + " " + student.getTeacher().getLastName()
                    + " does not teach " + student.getLanguage());
        }

        return StudentDto.fromEntity(updatedStudent);
    }

    @Transactional
    public StudentDto changeTeacher(Long id, ChangeStudentTeacherCommand command) {

        if (id == null || command.teacherId() == null) {
            throw new IllegalArgumentException("Student id or teacher id are missing");
        }

        Student student = findById(id);
        Teacher teacher = teacherRepository.findWithLockByIdAndDeletedFalse(command.teacherId()).orElseThrow(() -> new EntityNotFoundException("teacher with Id " + command.teacherId() + " not found"));

        if (student.getLanguage() == null || teacher.getLanguages() == null) {
            throw new IllegalArgumentException("Student language or teacher languages are missing");
        }

        if (!teacher.getLanguages().contains(student.getLanguage())) {
            throw new LanguageMismatchException("Teacher " + teacher.getFirstName() + " " + teacher.getLastName()
                    + " does not teach " + student.getLanguage());
        }

        student.setTeacher(teacher);
        return StudentDto.fromEntity(student);
    }


}
