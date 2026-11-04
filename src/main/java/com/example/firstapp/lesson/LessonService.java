package com.example.firstapp.lesson;

import com.example.firstapp.common.exception.LanguageMismatchException;
import com.example.firstapp.common.exception.LessonDateUnavailableException;
import com.example.firstapp.common.exception.LessonInPastException;
import com.example.firstapp.common.exception.TeacherNotAssignedToStudentException;
import com.example.firstapp.lesson.dto.CreateLessonCommand;
import com.example.firstapp.lesson.dto.LessonDto;
import com.example.firstapp.lesson.dto.UpdateLessonCommand;
import com.example.firstapp.lesson.model.Lesson;
import com.example.firstapp.student.StudentRepository;
import com.example.firstapp.student.model.Student;
import com.example.firstapp.teacher.TeacherRepository;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public Lesson findById(Long id) {
        return lessonRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("lesson with Id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<LessonDto> findAll() {
        return lessonRepository.findAll().stream()
                .map(LessonDto::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteById(Long id) {
        lessonRepository.deleteById(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public LessonDto save(CreateLessonCommand command) {
        Teacher teacher = teacherRepository.findWithLockByIdAndDeletedFalse(command.teacherId()).orElseThrow(() -> new EntityNotFoundException("teacher with Id " + command.teacherId() + " not found"));
        Student student = studentRepository.findByIdAndDeletedFalse(command.studentId()).orElseThrow(() -> new EntityNotFoundException("student with Id " + command.studentId() + " not found"));
        Lesson lesson = CreateLessonCommand.toEntity(command, teacher, student);

        if (lesson.getDateTime() == null) {
            throw new IllegalArgumentException("Lesson data is missing");
        }

        if (lesson.getDateTime().isBefore(LocalDateTime.now())) {
            throw new LessonInPastException("Lesson cannot be scheduled in past");
        }

        LocalDateTime from = lesson.getDateTime().minusHours(1);
        LocalDateTime to = lesson.getDateTime().plusHours(1);
        if (lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThan(teacher, from, to)) {
            throw new LessonDateUnavailableException("Lesson date is unavailable");
        }

        if (student.getTeacher() == null || !student.getTeacher().getId().equals(teacher.getId())) {
            throw new TeacherNotAssignedToStudentException("Teacher with id " + teacher.getId() + " is not assigned to student with id " + student.getId());
        }

        if (student.getLanguage() == null || teacher.getLanguages() == null) {
            throw new IllegalArgumentException("Student language or teacher languages are missing");
        }

        if (!teacher.getLanguages().contains(student.getLanguage())) {
            throw new LanguageMismatchException("Teacher " + teacher.getFirstName() + " " + teacher.getLastName() + " does not teach " + student.getLanguage());
        }

        Lesson savedLesson = lessonRepository.save(lesson);
        return LessonDto.fromEntity(savedLesson);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public LessonDto update(Long id, UpdateLessonCommand command) {

        if (command.teacherId() == null && command.studentId() == null && command.dateTime() == null) {
            throw new IllegalArgumentException("No fields provided for lesson update");
        }

        Lesson lesson = lessonRepository.findWithLockById(id).orElseThrow(() -> new EntityNotFoundException("lesson with Id " + id + " not found"));

        Long teacherId;
        if (command.teacherId() != null) {
            teacherId = command.teacherId();
        } else {
            teacherId = lesson.getTeacher().getId();
        }
        Teacher teacher = teacherRepository.findWithLockByIdAndDeletedFalse(teacherId).orElseThrow(() -> new EntityNotFoundException("teacher with Id " + teacherId + " not found"));

        Long studentId;
        if (command.studentId() != null) {
            studentId = command.studentId();
        } else {
            studentId = lesson.getStudent().getId();
        }
        Student student = studentRepository.findByIdAndDeletedFalse(studentId).orElseThrow(() -> new EntityNotFoundException("student with Id " + studentId + " not found"));

        LocalDateTime dateTime;
        if (command.dateTime() != null) {
            dateTime = command.dateTime();
        } else {
            dateTime = lesson.getDateTime();
        }

        if (student.getTeacher() == null || !student.getTeacher().getId().equals(teacher.getId())) {
            throw new TeacherNotAssignedToStudentException("Teacher with id " + teacher.getId() + " is not assigned to student with id " + student.getId());
        }

        if (student.getLanguage() == null || teacher.getLanguages() == null) {
            throw new IllegalArgumentException("Student language or teacher languages are missing");
        }

        if (!teacher.getLanguages().contains(student.getLanguage())) {
            throw new LanguageMismatchException("Teacher " + teacher.getFirstName() + " " + teacher.getLastName() + " does not teach " + student.getLanguage());
        }

        if (dateTime == null) {
            throw new IllegalArgumentException("Lesson data is missing");
        }

        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new LessonInPastException("Lesson cannot be scheduled in past");
        }

        LocalDateTime from = dateTime.minusHours(1);
        LocalDateTime to = dateTime.plusHours(1);
        if (lessonRepository.existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThanAndIdNot(teacher, from, to, id)) {
            throw new LessonDateUnavailableException("Lesson date is unavailable");
        }

        lesson.setTeacher(teacher);
        lesson.setStudent(student);
        lesson.setDateTime(dateTime);

        return LessonDto.fromEntity(lesson);
    }

}
