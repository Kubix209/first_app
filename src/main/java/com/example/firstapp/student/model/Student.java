package com.example.firstapp.student.model;

import com.example.firstapp.common.Language;
import com.example.firstapp.lesson.model.Lesson;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Language language;
    @ManyToOne
    private Teacher teacher;
    @OneToMany(mappedBy = "student")
    private Set<Lesson> lessons;
    private boolean deleted;

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

}
