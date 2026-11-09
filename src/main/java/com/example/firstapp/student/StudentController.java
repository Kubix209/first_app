package com.example.firstapp.student;

import com.example.firstapp.student.dto.ChangeStudentTeacherCommand;
import com.example.firstapp.student.dto.CreateStudentCommand;
import com.example.firstapp.student.dto.StudentDto;
import com.example.firstapp.student.dto.UpdateStudentCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public List<StudentDto> findAll() {
        return studentService.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        studentService.deleteById(id);
    }

    @PostMapping
    public StudentDto save(@RequestBody CreateStudentCommand command) {
        return studentService.save(command);
    }

    @PatchMapping("/{id}")
    public StudentDto update(@PathVariable Long id, @RequestBody UpdateStudentCommand command) {
        return studentService.update(id, command);
    }

    @PutMapping("/{id}/teacher")
    public StudentDto changeTeacher(@PathVariable Long id, @RequestBody ChangeStudentTeacherCommand command) {
        return studentService.changeTeacher(id, command);
    }

}
