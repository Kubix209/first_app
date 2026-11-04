package com.example.firstapp.teacher;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    List<Teacher> findAllByLanguagesContainingAndDeletedFalse(Language language);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Teacher> findWithLockByIdAndDeletedFalse(Long id);

    List<Teacher> findAllByDeletedFalse();
}
