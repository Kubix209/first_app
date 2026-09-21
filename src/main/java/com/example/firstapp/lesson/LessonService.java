package com.example.firstapp.lesson;

import com.example.firstapp.common.exception.LanguageMismatchException;
import com.example.firstapp.common.exception.LessonDateUnavailableException;
import com.example.firstapp.common.exception.LessonInPastException;
import com.example.firstapp.lesson.model.Lesson;
import com.example.firstapp.student.StudentRepository;
import com.example.firstapp.student.model.Student;
import com.example.firstapp.teacher.TeacherRepository;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public List<Lesson> findAll() {
        return lessonRepository.findAll();
    }

    public void deleteById(Long id) {
        lessonRepository.deleteById(id);
    }

    public void save(Lesson lesson, Long studentId, Long teacherId) {

        if (lesson == null || lesson.getDateTime() == null || studentId == null || teacherId == null) {
            throw new IllegalArgumentException("Lesson data is missing");
        }

        if (lesson.getDateTime().isBefore(LocalDateTime.now())) {
            throw new LessonInPastException("Lesson cannot be scheduled in past");
        }

        Student student = studentRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException("student with Id " + studentId + " not found"));
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new EntityNotFoundException("teacher with Id " + teacherId + " not found"));

        LocalDateTime from = lesson.getDateTime().minusHours(1);
        LocalDateTime to = lesson.getDateTime().plusHours(1);
        if (lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThan(teacher, from, to)) {
            throw new LessonDateUnavailableException("Lesson date is unavailable");
        }

        if (student.getLanguage() == null || teacher.getLanguages() == null) {
            throw new IllegalArgumentException("Student language or teacher languages are missing");
        }

        if (!teacher.getLanguages().contains(student.getLanguage())) {
            throw new LanguageMismatchException(
                    "Teacher " + teacher.getFirstName() + " " + teacher.getLastName()
                            + " does not teach " + student.getLanguage());
        }

        lesson.setStudent(student);
        lesson.setTeacher(teacher);
        lessonRepository.save(lesson);
    }

    public Lesson findById(Long id) {
        return lessonRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("lesson with Id " + id + " not found"));
    }

}
