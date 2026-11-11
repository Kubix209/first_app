package com.example.firstapp.lesson;

import com.example.firstapp.lesson.dto.CreateLessonCommand;
import com.example.firstapp.lesson.dto.LessonDto;
import com.example.firstapp.lesson.dto.UpdateLessonCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LessonDto> findAll() {
        return lessonService.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        lessonService.deleteById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LessonDto create(@Valid @RequestBody CreateLessonCommand command) {
        return lessonService.save(command);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LessonDto update(@Valid @RequestBody UpdateLessonCommand command, @PathVariable Long id) {
        return lessonService.update(id, command);
    }
}
