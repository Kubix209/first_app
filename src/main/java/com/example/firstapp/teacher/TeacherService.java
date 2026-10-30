package com.example.firstapp.teacher;

import com.example.firstapp.common.Language;
import com.example.firstapp.teacher.dto.CreateTeacherCommand;
import com.example.firstapp.teacher.dto.TeacherDto;
import com.example.firstapp.teacher.model.Teacher;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public List<TeacherDto> findAll() {
        return teacherRepository.findAllByDeletedFalse().stream()
                .map(TeacherDto::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteById(Long id) {
        Teacher teacher = findById(id);
        teacher.setDeleted(true);
    }

    @Transactional
    public TeacherDto save(CreateTeacherCommand command) {
        // metoda save w repo przyjhmuje teacher wiec musze zmapowac commanda na teacghera analogicznie do tego jak w dto to zrobilisnmy
        // tylko w druga strona
        // to przekazac do metody save jako argument
        // i metoda save zwraca teahcera a my musimy zwrocic teacher dto
        // wiec znowu musimy zmapowac na teacher dto
        //dostosowac reszte teachera, calego studenta, cale lesson i wszystkie testy a jak to zrobie to sie zglos do bosa
    }

    @Transactional(readOnly = true)
    public Teacher findById(Long id) {
        return teacherRepository.findWithLockByIdAndDeletedFalse(id).orElseThrow(() -> new EntityNotFoundException("teacher with Id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<TeacherDto> findAllByLanguage(Language language) {
      return teacherRepository.findAllByLanguagesContainingAndDeletedFalse(language).stream()
                .map(teacher -> new TeacherDto(
                        teacher.getId(),
                        teacher.getFirstName(),
                        teacher.getLastName(),
                        Set.of(Language.JAVA)
                ))
                .toList();
    }
}
