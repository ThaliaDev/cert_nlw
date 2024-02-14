package com.thalia.cert_nlw.modules.students.useCases;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thalia.cert_nlw.modules.questions.entities.QuestionsEntity;
import com.thalia.cert_nlw.modules.questions.repositories.QuestionRepository;
import com.thalia.cert_nlw.modules.students.dto.StudentCertificationAnswerDTO;
import com.thalia.cert_nlw.modules.students.dto.VerifyIfHasCertificationDTO;
import com.thalia.cert_nlw.modules.students.entities.AnswersCertificationsEntity;
import com.thalia.cert_nlw.modules.students.entities.CertificationStudentEntity;
import com.thalia.cert_nlw.modules.students.entities.StudentEntity;
import com.thalia.cert_nlw.modules.students.repositories.CertificationStudentRepository;
import com.thalia.cert_nlw.modules.students.repositories.StudentRepository;

@Service
public class StudentCertificationAnswersUseCase {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CertificationStudentRepository certificationStudentRepository;

    @Autowired
    private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;

    public CertificationStudentEntity execute(StudentCertificationAnswerDTO dto) throws Exception {

        var hasCertification = this.verifyIfHasCertificationUseCase
                .execute(new VerifyIfHasCertificationDTO(dto.getEmail(), dto.getTechnology()));

        if (hasCertification) {
            throw new Exception("Você já tirou sua certificação!");
        }

        // Buscar as alternativas das perguntas
        // - Correct ou Incorreta
        List<QuestionsEntity> questionsEntity = questionRepository.findByTechnology(dto.getTechnology());
        List<AnswersCertificationsEntity> answersCertifications = new ArrayList<>();

        AtomicInteger correctAnswers = new AtomicInteger(0);

        dto.getQuestionAnswers()
                .stream().forEach(questionAnswer -> {
                    var question = questionsEntity.stream()
                            .filter(q -> q.getId().equals(questionAnswer.getQuestionId()))
                            .findFirst()
                            .orElseThrow(() -> new NoSuchElementException("Questão não encontrada com ID: " + questionAnswer.getQuestionId()));

                    var findCorrectAlternative = question.getAlternatives().stream()
                            .filter(alternative -> alternative.isCorrect()).findFirst().get();

                    if (findCorrectAlternative.getId().equals(questionAnswer.getAlternativeId())) {
                        questionAnswer.setCorrect(true);
                        correctAnswers.incrementAndGet();
                    } else {
                        questionAnswer.setCorrect(false);
                    }

                    var answerrsCertificationsEntity = AnswersCertificationsEntity.builder()
                            .answerID(questionAnswer.getAlternativeId())
                            .questionID(questionAnswer.getQuestionId())
                            .isCorrect(questionAnswer.isCorrect()).build();

                    answersCertifications.add(answerrsCertificationsEntity);
                });

        // Verificar se existe student pelo email
        var student = studentRepository.findByEmail(dto.getEmail());
        UUID studentID;
        if (student.isEmpty()) {
            var studentCreated = StudentEntity.builder().email(dto.getEmail()).build();
            studentCreated = studentRepository.save(studentCreated);
            studentID = studentCreated.getId();
        } else {
            studentID = student.get().getId();
        }

        CertificationStudentEntity certificationStudentEntity = CertificationStudentEntity.builder()
                .technology(dto.getTechnology())
                .studentID(studentID)
                .grade(correctAnswers.get())
                .build();

        var certificationStudentCreated = certificationStudentRepository.save(certificationStudentEntity);

        answersCertifications.stream().forEach(answerCertification -> {
            answerCertification.setCertificationID(certificationStudentEntity.getId());
            answerCertification.setCertificationStudentEntity(certificationStudentEntity);
        });

        certificationStudentEntity.setAnswersCertificationsEntities(answersCertifications);

        certificationStudentRepository.save(certificationStudentEntity);

        return certificationStudentCreated;
        // Salvar as informações da certificação
    }
}
