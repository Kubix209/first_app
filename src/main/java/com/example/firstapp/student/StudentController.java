package com.example.firstapp.student;

import com.example.firstapp.common.Language;
import com.example.firstapp.student.model.Student;
import com.example.firstapp.teacher.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final TeacherService teacherService;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "students/list";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteById(@PathVariable Long id) {
        studentService.deleteById(id);
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("languages", Language.values());
        model.addAttribute("teachers", teacherService.findAll());
        return "students/register";
    }

    @PostMapping("/create")
    public String save(Student student, @RequestParam Long teacherId) {
        studentService.save(student, teacherId);
        return "redirect:/students";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id);
        model.addAttribute("student", student);
        model.addAttribute("languages", Language.values());
        model.addAttribute("teachers", teacherService.findAll());
        return "students/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, Student student, @RequestParam Long teacherId) {
        student.setId(id);
        studentService.save(student, teacherId);
        return "redirect:/students";
    }

    @GetMapping("/{id}/change-teacher")
    public String changeTeacherForm(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id);
        model.addAttribute("student", student);
        model.addAttribute("teachers", teacherService.findAllByLanguage(student.getLanguage()));
        return "students/change-teacher";
    }

    @PostMapping("/{id}/change-teacher")
    public String changeTeacher(@PathVariable Long id, @RequestParam Long teacherId) {
        studentService.changeTeacher(id, teacherId);
        return "redirect:/students";
    }

}
