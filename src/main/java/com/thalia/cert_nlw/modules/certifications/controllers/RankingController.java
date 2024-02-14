package com.thalia.cert_nlw.modules.certifications.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thalia.cert_nlw.modules.certifications.useCases.Top10RankingUseCase;
import com.thalia.cert_nlw.modules.students.entities.CertificationStudentEntity;

@RestController
@RequestMapping("/ranking")
public class RankingController {

    @Autowired
    private Top10RankingUseCase rankingUseCase;

    @GetMapping("/top10")
    public List<CertificationStudentEntity> top10(){
        return this.rankingUseCase.execute();
    }
}
