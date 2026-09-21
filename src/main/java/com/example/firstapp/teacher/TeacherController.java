package com.example.firstapp.teacher;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.dto.TeacherDto;
import com.example.firstapp.teacher.model.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("teachers", teacherService.findAll());
        return "teachers/list";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteById(@PathVariable Long id) {
        teacherService.deleteById(id);
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("languages", Language.values());
        return "teachers/register";
    }

    @PostMapping("/create")
    public String save(Teacher teacher) {
        teacherService.save(teacher);
        return "redirect:/teachers";
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

    @GetMapping(params = "language")
    @ResponseBody
    public List<TeacherDto> findByLanguage(@RequestParam Language language) {
        return teacherService.findAllByLanguage(language);
    }

}
