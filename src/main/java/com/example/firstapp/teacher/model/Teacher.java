package com.example.firstapp.teacher.model;

import com.example.firstapp.common.Language;
import com.example.firstapp.student.model.Student;
import com.mysql.cj.x.protobuf.MysqlxCursor;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "teacher_language")
    @Column(name = "language")
    private Set<Language> languages;
    @OneToMany(mappedBy = "teacher", fetch = FetchType.EAGER)
    private Set<Student> students;

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

}
