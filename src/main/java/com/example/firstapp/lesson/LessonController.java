package com.example.firstapp.lesson;

import com.example.firstapp.lesson.dto.CreateLessonCommand;
import com.example.firstapp.lesson.dto.LessonDto;
import com.example.firstapp.lesson.dto.UpdateLessonCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    public List<LessonDto> findAll() {
        return lessonService.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        lessonService.deleteById(id);
    }

    @PostMapping
    public LessonDto create(@RequestBody CreateLessonCommand command) {
        return lessonService.save(command);
    }

    @PatchMapping("/{id}")
    public LessonDto update(@PathVariable Long id, @RequestBody UpdateLessonCommand command) {
        return lessonService.update(id, command);
    }

}
