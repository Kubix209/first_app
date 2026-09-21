package com.example.firstapp.student;

import com.example.firstapp.common.exception.LanguageMismatchException;
import com.example.firstapp.student.model.Student;
import com.example.firstapp.teacher.TeacherRepository;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    public void save(Student student, Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new EntityNotFoundException("teacher with Id " + teacherId + " not found"));
        student.setTeacher(teacher);
        studentRepository.save(student);
    }

    public Student findById(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("student with Id " + id + " not found"));
    }

    public void changeTeacher(Long studentId, Long teacherId) {

        if (studentId == null || teacherId == null) {
            throw new IllegalArgumentException("Student id or teacher id are missing");
        }

        Student student = studentRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException("student with Id " + studentId + " not found"));
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new EntityNotFoundException("teacher with Id " + teacherId + " not found"));

        if (student.getLanguage() == null || teacher.getLanguages() == null) {
            throw new IllegalArgumentException("Student language or teacher languages are missing");
        }

        if (!teacher.getLanguages().contains(student.getLanguage())) {
            throw new LanguageMismatchException("Teacher " + teacher.getFirstName() + " " + teacher.getLastName()
                    + " does not teach " + student.getLanguage());
        }

        if (student.getTeacher() != null && student.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("Student is already assigned to this teacher");
        }

        student.setTeacher(teacher);
        studentRepository.save(student);
    }


}
