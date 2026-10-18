package com.example.firstapp.lesson;

import com.example.firstapp.lesson.model.Lesson;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    boolean existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThan(Teacher teacher, LocalDateTime from, LocalDateTime to);

    boolean existsByTeacherAndDateTimeGreaterThanAndDateTimeLessThanAndIdNot(Teacher teacher, LocalDateTime from, LocalDateTime to, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Lesson> findWithLockById(Long id);

}
