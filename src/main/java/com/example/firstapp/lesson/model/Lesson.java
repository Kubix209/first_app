package com.example.firstapp.lesson.model;

import com.example.firstapp.student.model.Student;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Teacher teacher;
    @ManyToOne
    private Student student;
    private LocalDateTime dateTime;

    @Override
    public String toString() {
        return "Lesson id: " + id + "\n" +
                "Teacher: " + teacher + "\n" +
                "Students: " + student + "\n" +
                "Date: " + dateTime;
    }

}
