package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.GradeDto;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.service.GradeService;
import com.fivednevnik.api.service.GradeViewService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
public class GradeController {


    private final GradeService gradeService;
    private final GradeViewService gradeViewService;

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getMyGrades() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(gradeViewService.getGradesByStudentUsername(username));
    }

    @GetMapping("/my/period")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getMyGradesByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(gradeViewService.getGradesByStudentUsernameAndPeriod(username, from, to));
    }

    @GetMapping("/my/academic-period/{period}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getMyGradesByAcademicPeriod(@PathVariable String period) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User student = gradeService.getStudentByUsername(username);
        return ResponseEntity.ok(gradeService.getGradesByStudentAndPeriod(student.getId(), period));
    }

    @GetMapping("/my/final/{period}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getMyFinalGradesByPeriod(@PathVariable String period) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User student = gradeService.getStudentByUsername(username);
        return ResponseEntity.ok(gradeService.getFinalGradesByStudentAndPeriod(student.getId(), period));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getGradesByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeViewService.getGradesByStudentId(studentId));
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}/period/{period}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getGradesByStudentAndSubjectAndPeriod(
            @PathVariable Long studentId,
            @PathVariable Long subjectId,
            @PathVariable String period) {
        return ResponseEntity.ok(gradeService.getGradesByStudentAndSubjectAndPeriod(studentId, subjectId, period));
    }

    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getGradesBySubjectId(@PathVariable Long subjectId) {
        return ResponseEntity.ok(gradeViewService.getGradesBySubjectId(subjectId));
    }

    @GetMapping("/subject/{subjectId}/period/{period}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getGradesBySubjectAndPeriod(
            @PathVariable Long subjectId,
            @PathVariable String period) {
        return ResponseEntity.ok(gradeService.getGradesBySubjectAndPeriod(subjectId, period));
    }

    @GetMapping("/class/{className}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getGradesByClassName(@PathVariable String className) {
        return ResponseEntity.ok(gradeViewService.getGradesByClassName(className));
    }

    @GetMapping("/class/{className}/period/{period}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getGradesByClassNameAndPeriod(
            @PathVariable String className,
            @PathVariable String period) {
        return ResponseEntity.ok(gradeService.getGradesByClassNameAndPeriod(className, period));
    }

    @GetMapping("/class/subject")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getGradesByClassNameAndSubject(
            @RequestParam String className,
            @RequestParam Long subjectId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        if (!authorities.stream().anyMatch(a -> 
            a.getAuthority().equals("ROLE_TEACHER") || 
            a.getAuthority().equals("ROLE_CLASS_TEACHER") || 
            a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("У вас нет прав для выполнения этого действия");
        }
        
        return ResponseEntity.ok(gradeViewService.getGradesByClassNameAndSubject(className, subjectId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<GradeDto> createGrade(@Valid @RequestBody GradeDto gradeDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherUsername = authentication.getName();


        if (gradeDto.getGradeValue() == null) {
            GradeDto newGradeDto = GradeDto.builder()
                .id(gradeDto.getId())
                .studentId(gradeDto.getStudentId())
                .studentName(gradeDto.getStudentName())
                .subjectId(gradeDto.getSubjectId())
                .subjectName(gradeDto.getSubjectName())
                .teacherId(gradeDto.getTeacherId())
                .teacherName(gradeDto.getTeacherName())
                .gradeValue(0)
                .weight(gradeDto.getWeight())
                .description(gradeDto.getDescription())
                .date(gradeDto.getDate())
                .isFinal(gradeDto.isFinal())
                .period(gradeDto.getPeriod())
                .gradeType(gradeDto.getGradeType())
                .type(gradeDto.getType())
                .periodId(gradeDto.getPeriodId())
                .periodType(gradeDto.getPeriodType())
                .classId(gradeDto.getClassId())
                .build();
            gradeDto = newGradeDto;
        }
        
        return ResponseEntity.ok(gradeService.createGrade(gradeDto, teacherUsername));
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> createBatchGrades(@Valid @RequestBody List<GradeDto> gradeDtos) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherUsername = authentication.getName();
        return ResponseEntity.ok(gradeService.createBatchGrades(gradeDtos, teacherUsername));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<GradeDto> updateGrade(
            @PathVariable Long id,
            @Valid @RequestBody GradeDto gradeDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherUsername = authentication.getName();
        return ResponseEntity.ok(gradeService.updateGrade(id, gradeDto, teacherUsername));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherUsername = authentication.getName();
        gradeService.deleteGrade(id, teacherUsername);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sync")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN', 'STUDENT')")
    public ResponseEntity<List<GradeDto>> syncGrades() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            return ResponseEntity.ok(gradeViewService.getGradesByStudentUsername(username));
        } else {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/force-sync")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN', 'STUDENT')")
    public ResponseEntity<List<GradeDto>> forceSyncGrades() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<GradeDto> grades = gradeService.forceSyncGradesFromServer(username);
        return ResponseEntity.ok(grades);
    }

    @PostMapping("/with-sync")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<GradeDto> createGradeWithSync(@Valid @RequestBody GradeDto gradeDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherUsername = authentication.getName();
        return ResponseEntity.ok(gradeService.createGradeWithSync(gradeDto, teacherUsername));
    }

    @PostMapping("/with-date")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<GradeDto> createGradeWithDate(
            @Valid @RequestBody GradeDto gradeDto,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherUsername = authentication.getName();
        return ResponseEntity.ok(gradeService.createGradeWithDate(gradeDto, teacherUsername, date));
    }
} 
