package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.campaigns.ResponseCampaignsDTO;
import com.FedericoFunes.app_service.dtos.donor.ResponseDonorDTO;
import com.FedericoFunes.app_service.entities.DonorEntity;
import com.FedericoFunes.app_service.entities.enums.BloodFactor;
import com.FedericoFunes.app_service.entities.enums.BloodGroup;
import com.FedericoFunes.app_service.services.external.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private ResponseCampaignsDTO campaignDto;
    private ResponseDonorDTO donorDto;

    @BeforeEach
    void setUp() throws Exception {
        Field fromField = EmailService.class.getDeclaredField("fromEmail");
        fromField.setAccessible(true);
        fromField.set(emailService, "test@bloodo.net");

        campaignDto = new ResponseCampaignsDTO();
        campaignDto.setId(1L);
        campaignDto.setTitle("Campaña Test");
        campaignDto.setDescription("Descripción test");
        campaignDto.setStartDate(LocalDate.of(2026, 8, 1));
        campaignDto.setDirection("Av. Corrientes 1234");
        campaignDto.setBloodFactorRequired(BloodFactor.POSITIVE);
        campaignDto.setBloodGroupRequired(null);

        donorDto = new ResponseDonorDTO();
        donorDto.setId(10L);
        donorDto.setFirstName("Juan");
        donorDto.setLastName("Pérez");
        donorDto.setEmail("juan@test.com");
        donorDto.setBloodFactor(BloodFactor.POSITIVE);
        donorDto.setBloodGroup(BloodGroup.A);
    }

    // ── sendEmail ──

    @Test
    void sendEmail_shouldCallMailSender() {
        emailService.sendEmail("to@test.com", "Subject", "Body");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_shouldThrowRuntimeException_whenMailSenderFails() {
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(RuntimeException.class,
                () -> emailService.sendEmail("to@test.com", "Subject", "Body"));
    }

    // ── sendHtmlEmail ──

    @Test
    void sendHtmlEmail_shouldCallMailSender() {
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        emailService.sendHtmlEmail("to@test.com", "Subject", "<h1>Hello</h1>");

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendHtmlEmail_shouldThrowRuntimeException_whenCreateFails() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("SMTP error"));

        assertThrows(RuntimeException.class,
                () -> emailService.sendHtmlEmail("to@test.com", "Subject", "<h1>Hello</h1>"));
    }

    // ── sendBulkHtmlEmail ──

    @Test
    void sendBulkHtmlEmail_shouldSendToAllRecipients() {
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        List<String> recipients = Arrays.asList("a@test.com", "b@test.com", "c@test.com");

        emailService.sendBulkHtmlEmail(recipients, "Subject", "<h1>Hello</h1>");

        verify(mailSender, times(3)).send(any(MimeMessage.class));
    }

    @Test
    void sendBulkHtmlEmail_shouldSendNothing_whenListEmpty() {
        emailService.sendBulkHtmlEmail(Collections.emptyList(), "Subject", "<h1>Hello</h1>");

        verify(mailSender, never()).createMimeMessage();
    }

    // ── sendBulkEmail ──

    @Test
    void sendBulkEmail_shouldSendToAllRecipients() {
        List<String> recipients = Arrays.asList("a@test.com", "b@test.com");

        emailService.sendBulkEmail(recipients, "Subject", "Body");

        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendBulkEmail_shouldSendNothing_whenListEmpty() {
        emailService.sendBulkEmail(Collections.emptyList(), "Subject", "Body");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    // ── notifyCreateCampaign ──

    @Test
    void notifyCreateCampaign_shouldFilterByBloodFactor() {
        ResponseDonorDTO negDonor = new ResponseDonorDTO();
        negDonor.setEmail("neg@test.com");
        negDonor.setBloodFactor(BloodFactor.NEGATIVE);
        negDonor.setBloodGroup(BloodGroup.A);

        List<ResponseDonorDTO> donors = Arrays.asList(donorDto, negDonor);

        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        emailService.notifyCreateCampaign(campaignDto, donors);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void notifyCreateCampaign_shouldFilterByBloodGroup() {
        campaignDto.setBloodFactorRequired(null);
        campaignDto.setBloodGroupRequired(BloodGroup.O);

        ResponseDonorDTO oDonor = new ResponseDonorDTO();
        oDonor.setEmail("o@test.com");
        oDonor.setBloodFactor(BloodFactor.POSITIVE);
        oDonor.setBloodGroup(BloodGroup.O);

        List<ResponseDonorDTO> donors = Arrays.asList(donorDto, oDonor);

        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        emailService.notifyCreateCampaign(campaignDto, donors);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void notifyCreateCampaign_shouldFilterByBothBloodType() {
        campaignDto.setBloodGroupRequired(BloodGroup.A);

        ResponseDonorDTO matchDonor = new ResponseDonorDTO();
        matchDonor.setEmail("match@test.com");
        matchDonor.setBloodFactor(BloodFactor.POSITIVE);
        matchDonor.setBloodGroup(BloodGroup.A);

        ResponseDonorDTO noMatchDonor = new ResponseDonorDTO();
        noMatchDonor.setEmail("nomatch@test.com");
        noMatchDonor.setBloodFactor(BloodFactor.NEGATIVE);
        noMatchDonor.setBloodGroup(BloodGroup.B);

        List<ResponseDonorDTO> donors = Arrays.asList(matchDonor, noMatchDonor);

        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        emailService.notifyCreateCampaign(campaignDto, donors);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void notifyCreateCampaign_shouldSendToAll_whenNoBloodFilter() {
        campaignDto.setBloodFactorRequired(null);
        campaignDto.setBloodGroupRequired(null);

        List<ResponseDonorDTO> donors = Arrays.asList(donorDto, donorDto);

        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        emailService.notifyCreateCampaign(campaignDto, donors);

        verify(mailSender, times(2)).send(any(MimeMessage.class));
    }

    @Test
    void notifyCreateCampaign_shouldSendFallbackEmail_whenHtmlFails() {
        when(mailSender.createMimeMessage())
                .thenThrow(new RuntimeException("HTML error"));

        assertThrows(ResponseStatusException.class,
                () -> emailService.notifyCreateCampaign(campaignDto, Collections.singletonList(donorDto)));
    }

    @Test
    void notifyCreateCampaign_shouldThrowException_whenBothFail() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("HTML error"));
        doThrow(new RuntimeException("Plain error")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(ResponseStatusException.class,
                () -> emailService.notifyCreateCampaign(campaignDto, Collections.singletonList(donorDto)));
    }

    // ── notifyUpdateCampaign ──

    @Test
    void notifyUpdateCampaign_shouldSendToAllSubscribedDonors() {
        DonorEntity donor = DonorEntity.builder()
                .firstName("Juan").lastName("Pérez")
                .email("juan@test.com").build();

        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        emailService.notifyUpdateCampaign(campaignDto, Collections.singletonList(donor));

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void notifyUpdateCampaign_shouldSendNothing_whenNoDonors() {
        emailService.notifyUpdateCampaign(campaignDto, Collections.emptyList());

        verify(mailSender, never()).createMimeMessage();
    }

    @Test
    void notifyUpdateCampaign_shouldThrowException_whenSendFails() {
        DonorEntity donor = DonorEntity.builder()
                .firstName("Juan").lastName("Pérez")
                .email("juan@test.com").build();

        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("SMTP down"));

        assertThrows(ResponseStatusException.class,
                () -> emailService.notifyUpdateCampaign(campaignDto, Collections.singletonList(donor)));
    }

    @Test
    void notifyUpdateCampaign_shouldSendMultipleDonors() {
        DonorEntity d1 = DonorEntity.builder().email("a@test.com").build();
        DonorEntity d2 = DonorEntity.builder().email("b@test.com").build();
        DonorEntity d3 = DonorEntity.builder().email("c@test.com").build();

        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        emailService.notifyUpdateCampaign(campaignDto, Arrays.asList(d1, d2, d3));

        verify(mailSender, times(3)).send(any(MimeMessage.class));
    }
}
