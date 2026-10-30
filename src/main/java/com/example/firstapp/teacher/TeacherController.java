package com.example.firstapp.teacher;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.dto.CreateTeacherCommand;
import com.example.firstapp.teacher.dto.TeacherDto;
import com.example.firstapp.teacher.model.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
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
    public TeacherDto save(CreateTeacherCommand command) {
        return teacherService.save(command);
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.findById(id);
        model.addAttribute("teacher", teacher);
        model.addAttribute("languages", Language.values());
        return "teachers/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, Teacher teacher) {
        teacher.setId(id);
        teacherService.save(teacher);
        return "redirect:/teachers";
    }

    //endpoint GET zwracający JSON, z parametrem zapytania(query parameter)
    //http://localhost:8080/teachers?language=JAVA
    @GetMapping(params = "language")
    @ResponseBody
    public List<TeacherDto> findByLanguage(@RequestParam Language language) {
        return teacherService.findAllByLanguage(language);
    }

}
