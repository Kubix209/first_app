package com.example.firstapp.teacher;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.dto.CreateTeacherCommand;
import com.example.firstapp.teacher.dto.TeacherDto;
import com.example.firstapp.teacher.dto.UpdateTeacherCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TeacherDto> findAll() {
        return teacherService.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        teacherService.deleteById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherDto save(@Valid @RequestBody CreateTeacherCommand command) {
        return teacherService.save(command);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TeacherDto update(@Valid @RequestBody UpdateTeacherCommand command,@PathVariable Long id) {
        return teacherService.update(command, id);
    }

    @GetMapping(params = "language")
    @ResponseStatus(HttpStatus.OK)
    public List<TeacherDto> findByLanguage(@RequestParam Language language) {
        return teacherService.findAllByLanguage(language);
    }
}
