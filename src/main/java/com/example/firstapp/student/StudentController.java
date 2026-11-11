package com.example.firstapp.student;

import com.example.firstapp.student.dto.ChangeStudentTeacherCommand;
import com.example.firstapp.student.dto.CreateStudentCommand;
import com.example.firstapp.student.dto.StudentDto;
import com.example.firstapp.student.dto.UpdateStudentCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<StudentDto> findAll() {
        return studentService.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        studentService.deleteById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentDto save(@Valid @RequestBody CreateStudentCommand command) {
        return studentService.save(command);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StudentDto update(@Valid @RequestBody UpdateStudentCommand command, @PathVariable Long id) {
        return studentService.update(id, command);
    }

    @PutMapping("/{id}/teacher")
    @ResponseStatus(HttpStatus.OK)
    public StudentDto changeTeacher(@Valid @PathVariable Long id, @RequestBody ChangeStudentTeacherCommand command) {
        return studentService.changeTeacher(id, command);
    }
}
