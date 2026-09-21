package com.example.firstapp.lesson;

import com.example.firstapp.lesson.model.Lesson;
import com.example.firstapp.teacher.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    boolean existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThan(Teacher teacher, LocalDateTime from, LocalDateTime to);
}
