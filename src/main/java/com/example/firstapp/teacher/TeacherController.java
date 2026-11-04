package com.example.firstapp.teacher;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.dto.CreateTeacherCommand;
import com.example.firstapp.teacher.dto.TeacherDto;
import com.example.firstapp.teacher.dto.UpdateTeacherCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public List<TeacherDto> findAll() {
        return teacherService.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        teacherService.deleteById(id);
    }

    @PostMapping
    public TeacherDto save(@RequestBody CreateTeacherCommand command) {
        return teacherService.save(command);
    }

    @PatchMapping("/{id}")
    public TeacherDto update(@PathVariable Long id, @RequestBody UpdateTeacherCommand command) {
        return teacherService.update(command, id);
    }

    @GetMapping(params = "language")
    public List<TeacherDto> findByLanguage(@RequestParam Language language) {
        return teacherService.findAllByLanguage(language);
    }

}
