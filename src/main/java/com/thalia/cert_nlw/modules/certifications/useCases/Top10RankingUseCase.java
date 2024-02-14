package com.thalia.cert_nlw.modules.certifications.useCases;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thalia.cert_nlw.modules.students.repositories.CertificationStudentRepository;
import com.thalia.cert_nlw.modules.students.entities.CertificationStudentEntity;;
@Service
public class Top10RankingUseCase {
    @Autowired
    private CertificationStudentRepository certificationStudentRepository;

    public List<CertificationStudentEntity> execute(){
        return this.certificationStudentRepository.findTop10ByOrderByGradeDesc();
    }
}
