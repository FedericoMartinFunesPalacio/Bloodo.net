package com.FedericoFunes.app_service.services.external;

import com.FedericoFunes.app_service.dtos.campaigns.ResponseCampaignsDTO;
import com.FedericoFunes.app_service.dtos.donor.ResponseDonorDTO;
import com.FedericoFunes.app_service.entities.CampaignsEntity;
import com.FedericoFunes.app_service.entities.DonorEntity;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private String createHtml (String title, String description, String startDate, String direction, Boolean isNew) {
        String titleHtml = isNew ? "Nueva campaña de donación" : "Actualización de la campaña de donación";
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <style>\n" +
                "    body { font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px; }\n" +
                "    .card { background-color: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }\n" +
                "    h2 { color: #d32f2f; }\n" +
                "    p { color: #333; }\n" +
                "    .footer { margin-top: 20px; font-size: 12px; color: #777; }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"card\">\n" +
                "    <h2>"+titleHtml+"</h2>\n" +
                "    <p><strong>Título:</strong> "+ title +"</p>\n" +
                "    <p><strong>Descripción:</strong> "+ description +"</p>\n" +
                "    <p><strong>Fecha:</strong> "+ startDate +"</p>\n" +
                "    <p><strong>Ubicación:</strong> "+ direction +"</p>\n" +
                "    <p>¡Gracias por tu compromiso con salvar vidas!</p>\n" +
                "    <div class=\"footer\">\n" +
                "      Bloodo.net - Plataforma de donación de sangre\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>\n";
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error sending email HTML: " + e.getMessage());
        }
    }

    public void sendBulkHtmlEmail(List<String> recipients, String subject, String htmlBody) {
        for (String to : recipients) {
            sendHtmlEmail(to, subject, htmlBody);
        }
    }

    public void sendBulkEmail(List<String> recipients, String subject, String body) {
        for (String to : recipients) {
            sendEmail(to, subject, body);
        }
    }

    @Async
    public void notifyCreateCampaign(ResponseCampaignsDTO campaing, List<ResponseDonorDTO> allDonors) {
        try {
            List<String> donorEmailsComplete;

            if (campaing.getBloodFactorRequired() != null && campaing.getBloodGroupRequired() == null) {
                //BloodFactor
                donorEmailsComplete = allDonors
                        .stream()
                        .filter(donor -> donor.getBloodFactor().equals(campaing.getBloodFactorRequired()))
                        .map(ResponseDonorDTO::getEmail)
                        .toList();

            } else if (campaing.getBloodGroupRequired() != null && campaing.getBloodFactorRequired() == null) {
                //BloodGroup
                donorEmailsComplete = allDonors
                        .stream()
                        .filter(donor -> donor.getBloodGroup().equals(campaing.getBloodGroupRequired()))
                        .map(ResponseDonorDTO::getEmail)
                        .toList();

            } else if (campaing.getBloodGroupRequired() != null && campaing.getBloodFactorRequired() != null) {
                //BloodFactor y BloodGroup
                donorEmailsComplete = allDonors
                        .stream()
                        .filter(donor -> donor.getBloodFactor().equals(campaing.getBloodFactorRequired())
                                && donor.getBloodGroup().equals(campaing.getBloodGroupRequired()))
                        .map(ResponseDonorDTO::getEmail)
                        .toList();

            } else {
                donorEmailsComplete = allDonors
                        .stream()
                        .map(ResponseDonorDTO::getEmail)
                        .toList();
            }
            sendBulkHtmlEmail(donorEmailsComplete, "Nueva campaña de donación: " + campaing.getTitle(), createHtml(campaing.getTitle(), campaing.getDescription(), campaing.getStartDate().toString(), campaing.getDirection(), true));
        } catch (Exception e) {
            try {
                List<String> donorEmails = allDonors
                        .stream()
                        .map(ResponseDonorDTO::getEmail)
                        .toList();
                String subject = "Nueva campaña de donación: " + campaing.getTitle();
                String body = "Se ha creado una nueva campaña en " + campaing.getDirection() +
                        " el día " + campaing.getStartDate() +
                        ". ¡Te esperamos para donar sangre!";
                sendBulkEmail(donorEmails, subject, body);
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error sending emails: " + e.getMessage());
            }
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error EmailService sending: " + e.getMessage());
        }
    }

    @Async
    public void notifyUpdateCampaign(ResponseCampaignsDTO campaing, List<DonorEntity> donorsSuscribe) {
        try {
            List<String> donorEmails = donorsSuscribe
                    .stream()
                    .map(DonorEntity::getEmail)
                    .toList();

            sendBulkHtmlEmail(donorEmails, "Actualización de campaña de donación: " + campaing.getTitle(), createHtml(campaing.getTitle(), campaing.getDescription(), campaing.getStartDate().toString(), campaing.getDirection(), false));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error EmailService sending: " + e.getMessage());
        }
    }
}
