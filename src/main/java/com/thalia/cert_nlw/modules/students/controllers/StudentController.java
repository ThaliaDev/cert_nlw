package com.thalia.cert_nlw.modules.students.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thalia.cert_nlw.modules.students.dto.StudentCertificationAnswerDTO;
import com.thalia.cert_nlw.modules.students.dto.VerifyIfHasCertificationDTO;
import com.thalia.cert_nlw.modules.students.entities.CertificationStudentEntity;
import com.thalia.cert_nlw.modules.students.useCases.StudentCertificationAnswersUseCase;
import com.thalia.cert_nlw.modules.students.useCases.VerifyIfHasCertificationUseCase;

@RestController
@RequestMapping("/students")
public class StudentController {
    //Usando use case
    @Autowired
    private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;

    @Autowired
    private StudentCertificationAnswersUseCase studentCertificationAnswersUseCase;
    @PostMapping("/verifyIfHasCertification")
    public String verifyIfHasCertification(@RequestBody VerifyIfHasCertificationDTO verifyHasCertificationDTO ){
        //Email e tecnologia
        var result = this.verifyIfHasCertificationUseCase.execute(verifyHasCertificationDTO);
        if(result){
            return "Usuário já fez a prova";
        }
        System.out.println(verifyHasCertificationDTO);
        return "Usuário pode fazer a prova";
    }
    
    @PostMapping("/certification/answer")
    public ResponseEntity<Object> certificationAnswer(@RequestBody StudentCertificationAnswerDTO studentCertificationAnswerDTO) throws Exception{
        try {
            var result = this.studentCertificationAnswersUseCase.execute(studentCertificationAnswerDTO);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        
    }
}
