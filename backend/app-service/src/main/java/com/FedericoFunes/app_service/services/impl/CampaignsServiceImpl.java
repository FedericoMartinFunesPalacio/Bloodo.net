package com.FedericoFunes.app_service.services.impl;

import com.FedericoFunes.app_service.dtos.campaigns.BloodEstimatedDTO;
import com.FedericoFunes.app_service.dtos.campaigns.BloodTypeRankingDTO;
import com.FedericoFunes.app_service.dtos.campaigns.GeographicDistributionDTO;
import com.FedericoFunes.app_service.dtos.campaigns.LivesSavedDTO;
import com.FedericoFunes.app_service.dtos.campaigns.RequestCampaignsDTO;
import com.FedericoFunes.app_service.dtos.campaigns.ResponseCampaignsDTO;
import com.FedericoFunes.app_service.dtos.campaigns.TotalBloodEstimatedDTO;
import com.FedericoFunes.app_service.dtos.campaigns.TotalLivesSavedDTO;
import com.FedericoFunes.app_service.dtos.donor.BloodTypePercentageDTO;
import com.FedericoFunes.app_service.entities.CampaignsEntity;
import com.FedericoFunes.app_service.entities.Organizer;
import com.FedericoFunes.app_service.handlers.BadRequestException;
import com.FedericoFunes.app_service.handlers.NotFoundException;
import com.FedericoFunes.app_service.repositories.CampaignsRepository;
import com.FedericoFunes.app_service.services.CampaignsService;
import com.FedericoFunes.app_service.services.external.EmailService;
import com.FedericoFunes.app_service.services.external.GoogleMapsService;
import com.FedericoFunes.app_service.services.DonorService;
import com.FedericoFunes.app_service.services.OrganizerEmpService;
import com.FedericoFunes.app_service.services.OrganizerPerService;
import com.FedericoFunes.app_service.dtos.donor.ResponseDonorDTO;
import com.FedericoFunes.app_service.dtos.organizeremp.ResponseOrganizerEmpDTO;
import com.FedericoFunes.app_service.dtos.organizerper.ResponseOrganizerPerDTO;
import com.FedericoFunes.app_service.entities.DonorEntity;
import com.FedericoFunes.app_service.entities.OrganizerEmpEntity;
import com.FedericoFunes.app_service.entities.OrganizerPerEntity;
import com.FedericoFunes.app_service.dtos.campaigns.SubscribedDonorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CampaignsServiceImpl implements CampaignsService {

    private final CampaignsRepository campaignsRepository;
    private final OrganizerEmpService organizerEmpService;
    private final OrganizerPerService organizerPerService;
    private final GoogleMapsService googleMapsService;
    private final DonorService donorService;
    private final EmailService emailService;

    private ResponseCampaignsDTO entityToDTO(CampaignsEntity entity) {
        try {
            ResponseCampaignsDTO dto = new ResponseCampaignsDTO();
            dto.setId(entity.getId());
            dto.setTitle(entity.getTitle());
            dto.setDescription(entity.getDescription());
            dto.setStartDate(entity.getStartDate());
            dto.setEndDate(entity.getEndDate());
            dto.setStartTime(entity.getStartTime());
            dto.setDirection(entity.getDirection());
            dto.setLatitude(entity.getLatitude());
            dto.setLongitude(entity.getLongitude());
            dto.setBloodFactorRequired(entity.getBloodFactorRequired());
            dto.setBloodGroupRequired(entity.getBloodGroupRequired());
            dto.setOrganizerId(entity.getCreator() != null ? entity.getCreator().getId() : null);
            return dto;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error mapping CampaignsDTO: " + e.getMessage());
        }
    }
    private CampaignsEntity dtoToEntity(RequestCampaignsDTO dto) {
        try {
            CampaignsEntity entity = new CampaignsEntity();
            entity.setTitle(dto.getTitle());
            entity.setDescription(dto.getDescription());
            entity.setStartDate(dto.getStartDate());
            entity.setEndDate(dto.getEndDate());
            entity.setWasNotify(false);
            entity.setStartTime(dto.getStartTime());
            entity.setDirection(dto.getDirection());
            try {
                double[] latLng = googleMapsService.getLatLngFromAddress(dto.getDirection());
                entity.setLatitude(latLng[0]);
                entity.setLongitude(latLng[1]);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error fetching coordinates: " + e.getMessage());
            }
            entity.setBloodFactorRequired(dto.getBloodFactorRequired());
            entity.setBloodGroupRequired(dto.getBloodGroupRequired());
            Organizer organizer = getOrganizerById(dto.getOrganizerId());
            entity.setCreator(organizer);
            entity.setSubscribedDonors(new ArrayList<>());
            entity.setIsActive(true);
            return entity;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error mapping CampaignsEntity: " + e.getMessage());
        }
    }
    private DonorEntity mapDonorDTOToEntity(ResponseDonorDTO dto, Long donorId) {
        DonorEntity entity = new DonorEntity();
        entity.setId(donorId);
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setBirthdate(dto.getBirthdate());
        entity.setDocument(dto.getDocument());
        entity.setBloodFactor(dto.getBloodFactor());
        entity.setBloodGroup(dto.getBloodGroup());
        entity.setGender(dto.getGender());
        entity.setHeight(dto.getHeight());
        entity.setWeight(dto.getWeight());
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }
    private void validateCampaign(RequestCampaignsDTO dto) {
        if (dto == null || dto.getTitle() == null || dto.getTitle().isBlank() || dto.getDescription() == null || dto.getDescription().isBlank() || dto.getStartDate() == null || dto.getStartTime() == null || dto.getDirection() == null || dto.getDirection().isBlank() || dto.getOrganizerId() == null) {
            throw new BadRequestException("Validation failed: fields empty or null");
        }
    }
    private Organizer getOrganizerById(Long organizerId) {
        try {
            ResponseOrganizerEmpDTO empDTO = organizerEmpService.GetOrganizerEmpById(organizerId);
            OrganizerEmpEntity empEntity = new OrganizerEmpEntity();
            empEntity.setId(organizerId);
            empEntity.setFullName(empDTO.getFullName());
            empEntity.setDocument(empDTO.getDocument());
            empEntity.setDirection(empDTO.getDirection());
            empEntity.setLatitude(empDTO.getLatitude());
            empEntity.setLongitude(empDTO.getLongitude());
            empEntity.setEmail(empDTO.getEmail());
            empEntity.setPhoneNumber(empDTO.getPhoneNumber());
            empEntity.setIsActive(empDTO.getIsActive());
            return empEntity;
        } catch (Exception e) {
            try {
                ResponseOrganizerPerDTO perDTO = organizerPerService.GetOrganizerPerById(organizerId);
                OrganizerPerEntity perEntity = new OrganizerPerEntity();
                perEntity.setId(organizerId);
                perEntity.setFirstName(perDTO.getFirstName());
                perEntity.setLastName(perDTO.getLastName());
                perEntity.setBirthdate(perDTO.getBirthdate());
                perEntity.setDocument(perDTO.getDocument());
                perEntity.setDirection(perDTO.getDirection());
                perEntity.setLatitude(perDTO.getLatitude());
                perEntity.setLongitude(perDTO.getLongitude());
                perEntity.setGender(perDTO.getGender());
                perEntity.setEmail(perDTO.getEmail());
                perEntity.setPhoneNumber(perDTO.getPhoneNumber());
                perEntity.setIsActive(perDTO.getIsActive());
                return perEntity;
            } catch (Exception ex) {
                throw new BadRequestException("Organizer not found");
            }
        }
    }
    private List<SubscribedDonorDTO> mapDonorsSuscribeEntityToDTO(CampaignsEntity campaign) {
        List<SubscribedDonorDTO> result = new ArrayList<>();
        for (DonorEntity donor : campaign.getSubscribedDonors()) {
            SubscribedDonorDTO dto = new SubscribedDonorDTO();
            dto.setId(donor.getId());
            dto.setFirstName(donor.getFirstName());
            dto.setLastName(donor.getLastName());
            dto.setEmail(donor.getEmail());
            dto.setDocument(donor.getDocument());
            dto.setPhoneNumber(donor.getPhoneNumber());
            dto.setBloodGroup(donor.getBloodGroup());
            dto.setBloodFactor(donor.getBloodFactor());
            dto.setIsActive(donor.getIsActive());
            result.add(dto);
        }
        return result;
    }

    //ABM
    @Override
    public List<ResponseCampaignsDTO> getAllCampaigns() {
        List<ResponseCampaignsDTO> result = new ArrayList<>();
        for (CampaignsEntity entity : campaignsRepository.findAll()) {
            if (entity.getIsActive() != null && entity.getIsActive()
                    && entity.getEndDate() == null) {
                result.add(entityToDTO(entity));
            }
        }
        return result;
    }

    @Override
    public ResponseCampaignsDTO getCampaignById(Long id) {
        Optional<CampaignsEntity> entityOpt = campaignsRepository.findById(id);
        if (entityOpt.isEmpty() || entityOpt.get().getIsActive() == null || !entityOpt.get().getIsActive() || entityOpt.get().getEndDate() != null) {
            throw new NotFoundException("Campaign not found, inactive or finished");
        }
        return entityToDTO(entityOpt.get());
    }

    @Override
    public ResponseCampaignsDTO createCampaign(RequestCampaignsDTO campaign) {
        validateCampaign(campaign);
        CampaignsEntity entity = dtoToEntity(campaign);
        ResponseCampaignsDTO savedCampaing = entityToDTO(campaignsRepository.save(entity));

        emailService.notifyCreateCampaign(savedCampaing, donorService.GetAllDonors());

        return savedCampaing;
    }

    @Override
    public ResponseCampaignsDTO updateCampaign(RequestCampaignsDTO campaign, Long id) {
        validateCampaign(campaign);
        CampaignsEntity entity = campaignsRepository.findById(id).orElseThrow(() -> new NotFoundException("Campaign not found"));
        if (entity.getIsActive() == null || !entity.getIsActive()) {
            throw new BadRequestException("Cannot update inactive campaign");
        }
        if (entity.getEndDate() != null) {
            throw new BadRequestException("Cannot update finished campaign");
        }
        entity.setTitle(campaign.getTitle());
        entity.setDescription(campaign.getDescription());
        entity.setStartDate(campaign.getStartDate());
        entity.setEndDate(campaign.getEndDate());
        entity.setStartTime(campaign.getStartTime());
        entity.setDirection(campaign.getDirection());
        try {
            double[] latLng = googleMapsService.getLatLngFromAddress(campaign.getDirection());
            entity.setLatitude(latLng[0]);
            entity.setLongitude(latLng[1]);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error fetching coordinates: " + e.getMessage());
        }
        entity.setBloodFactorRequired(campaign.getBloodFactorRequired());
        entity.setBloodGroupRequired(campaign.getBloodGroupRequired());
        Organizer organizer = getOrganizerById(campaign.getOrganizerId());
        entity.setCreator(organizer);

        ResponseCampaignsDTO savedCampaing = entityToDTO(campaignsRepository.save(entity));

        emailService.notifyUpdateCampaign(savedCampaing, entity.getSubscribedDonors());

        return savedCampaing;
    }

    @Override
    public ResponseCampaignsDTO deleteCampaign(Long id) {
        CampaignsEntity entity = campaignsRepository.findById(id).orElseThrow(() -> new NotFoundException("Campaign not found"));
        if (entity.getEndDate() != null) {
            throw new BadRequestException("Cannot delete finished campaign");
        }
        entity.setIsActive(false);
        return entityToDTO(campaignsRepository.save(entity));
    }


    //DONOR
    @Override
    public ResponseCampaignsDTO subscribeDonor(Long campaignId, Long donorId) {
        CampaignsEntity campaign = campaignsRepository.findById(campaignId)
                .orElseThrow(() -> new NotFoundException("Campaign not found"));
        if (campaign.getIsActive() == null || !campaign.getIsActive()) {
            throw new BadRequestException("Cannot subscribe to inactive campaign");
        }
        if (campaign.getEndDate() != null) {
            throw new BadRequestException("Cannot subscribe to finished campaign");
        }
        ResponseDonorDTO donorDTO = donorService.GetDonorById(donorId);
        DonorEntity donor = mapDonorDTOToEntity(donorDTO, donorId);
        if (campaign.getSubscribedDonors().stream().anyMatch(d -> d.getDocument().equals(donor.getDocument()))) {
            throw new BadRequestException("Donor already subscribed to campaign");
        }
        campaign.getSubscribedDonors().add(donor);
        return entityToDTO(campaignsRepository.save(campaign));
    }

    @Override
    public List<SubscribedDonorDTO> getSubscribedDonors(Long campaignId) {
        CampaignsEntity campaign = campaignsRepository.findById(campaignId)
                .orElseThrow(() -> new NotFoundException("Campaign not found"));
        if (campaign.getIsActive() == null || !campaign.getIsActive() || campaign.getEndDate() != null) {
            throw new BadRequestException("Cannot get donors from inactive or finished campaign");
        }
        return mapDonorsSuscribeEntityToDTO(campaign);
    }

    @Override
    public List<SubscribedDonorDTO> unsubscribeDonor(Long campaignId, Long donorId) {
        CampaignsEntity campaign = campaignsRepository.findById(campaignId)
                .orElseThrow(() -> new NotFoundException("Campaign not found"));
        if (campaign.getIsActive() == null || !campaign.getIsActive()) {
            throw new BadRequestException("Cannot unsubscribe from inactive campaign");
        }
        if (campaign.getEndDate() != null) {
            throw new BadRequestException("Cannot unsubscribe from finished campaign");
        }
        boolean removed = campaign.getSubscribedDonors().removeIf(d -> d.getId() != null && d.getId().equals(donorId));
        if (!removed) {
            throw new BadRequestException("Donor is not subscribed to this campaign");
        }
        campaignsRepository.save(campaign);
        return mapDonorsSuscribeEntityToDTO(campaign);
    }

    @Override
    public List<ResponseCampaignsDTO> getActiveSubscribedCampaigns(Long donorId) {
        List<ResponseCampaignsDTO> result = new ArrayList<>();
        for (CampaignsEntity entity : campaignsRepository.findActiveSubscribedCampaignsByDonor(donorId)) {
            result.add(entityToDTO(entity));
        }
        return result;
    }


    //CAMP
    @Override
    public ResponseCampaignsDTO finishCampaign(Long campaignId, java.time.LocalDate endDate) {
        CampaignsEntity campaign = campaignsRepository.findById(campaignId)
                .orElseThrow(() -> new NotFoundException("Campaign not found"));
        if (campaign.getIsActive() == null || !campaign.getIsActive()) {
            throw new BadRequestException("Cannot finish inactive campaign");
        }
        if (campaign.getEndDate() != null) {
            throw new BadRequestException("Campaign already finished");
        }
        campaign.setEndDate(endDate);
        return entityToDTO(campaignsRepository.save(campaign));
    }

    @Override
    public List<ResponseCampaignsDTO> getAllFinishedCampaigns() {
        List<ResponseCampaignsDTO> result = new ArrayList<>();
        for (CampaignsEntity entity : campaignsRepository.findAll()) {
            if (entity.getEndDate() != null) {
                result.add(entityToDTO(entity));
            }
        }
        return result;
    }

    @Override
    public void notifyUpcomingCampaign(Long campaignId) {
        try {
            Optional<CampaignsEntity> entityOpt = campaignsRepository.findById(campaignId);
            if (entityOpt.isEmpty() || entityOpt.get().getIsActive() == null || !entityOpt.get().getIsActive() || entityOpt.get().getEndDate() != null) {
                throw new NotFoundException("Campaign not found, inactive or finished");
            }
            CampaignsEntity campaign = entityOpt.get();
            if (!campaign.getWasNotify() && campaign.getWasNotify() != null) {
                campaign.setWasNotify(true);
                campaignsRepository.save(campaign);
                long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), campaign.getStartDate());

                if (daysBetween <= 3 && daysBetween >= 0) {
                    List<String> donorEmails = campaign.getSubscribedDonors()
                            .stream()
                            .map(DonorEntity::getEmail)
                            .toList();

                    String subject = "Recordatorio: campaña próxima";
                    String bodyHtml = "<!DOCTYPE html>\n" +
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
                            "    <h2>Recordatorio de próxima campaña</h2>\n" +
                            "    <p>La campaña "+ campaign.getTitle() +"</p>\n" +
                            "    <p>comienza el "+ campaign.getStartDate() +"</p>\n" +
                            "    <p>¡Te esperamos!</p>\n" +
                            "    <div class=\"footer\">\n" +
                            "      Bloodo.net - Plataforma de donación de sangre\n" +
                            "    </div>\n" +
                            "  </div>\n" +
                            "</body>\n" +
                            "</html>\n";
                    emailService.sendBulkHtmlEmail(donorEmails, subject, bodyHtml);
                }
            }


        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error notify upcoming campaign: " + e.getMessage());
        }
    }


    //METRICAS
    @Override
    public List<BloodEstimatedDTO> getBloodEstimatedPerCampaign() {
        List<BloodEstimatedDTO> dtos = new ArrayList<>();

        List<Object[]> activeResults = campaignsRepository.countSubscribersPerActiveCampaign();
        for (Object[] row : activeResults) {
            Long campaignId = (Long) row[0];
            String title = (String) row[1];
            Long count = (Long) row[2];
            double estimatedMl = count * 450.0;
            double estimatedLiters = estimatedMl / 1000.0;
            dtos.add(new BloodEstimatedDTO(campaignId, title, "ACTIVA", count, estimatedMl, estimatedLiters));
        }

        List<Object[]> finishedResults = campaignsRepository.countSubscribersPerFinishedCampaign();
        for (Object[] row : finishedResults) {
            Long campaignId = (Long) row[0];
            String title = (String) row[1];
            Long count = (Long) row[2];
            double estimatedMl = count * 450.0;
            double estimatedLiters = estimatedMl / 1000.0;
            dtos.add(new BloodEstimatedDTO(campaignId, title, "FINALIZADA", count, estimatedMl, estimatedLiters));
        }

        return dtos;
    }

    @Override
    public TotalBloodEstimatedDTO getTotalBloodEstimated() {
        Long totalSubscribers = campaignsRepository.countTotalSubscribers();
        Long totalCampaigns = campaignsRepository.countTotalCampaigns();
        double estimatedMl = totalSubscribers * 450.0;
        double estimatedLiters = estimatedMl / 1000.0;
        return new TotalBloodEstimatedDTO(totalSubscribers, totalCampaigns, estimatedMl, estimatedLiters);
    }

    @Override
    public List<BloodTypeRankingDTO> getBloodTypeRanking(Long campaignId) {
        CampaignsEntity campaign = campaignsRepository.findById(campaignId)
                .orElseThrow(() -> new NotFoundException("Campaign not found"));

        if (campaign.getBloodGroupRequired() != null) {
            return new ArrayList<>();
        }

        List<Object[]> results = campaignsRepository.countBloodTypesByCampaign(campaignId);
        List<BloodTypeRankingDTO> dtos = new ArrayList<>();
        for (Object[] row : results) {
            String bloodGroup = row[0] != null ? row[0].toString() : "Sin grupo";
            String bloodFactor = row[1] != null ? row[1].toString() : "Sin factor";
            String bloodType = bloodGroup + "_" + bloodFactor;
            Long count = (Long) row[2];
            dtos.add(new BloodTypeRankingDTO(bloodType, count));
        }
        return dtos;
    }

    @Override
    public List<LivesSavedDTO> getLivesSavedPerCampaign() {
        List<LivesSavedDTO> dtos = new ArrayList<>();

        List<Object[]> activeResults = campaignsRepository.countSubscribersPerActiveCampaign();
        for (Object[] row : activeResults) {
            Long campaignId = (Long) row[0];
            String title = (String) row[1];
            Long count = (Long) row[2];
            dtos.add(new LivesSavedDTO(campaignId, title, "ACTIVA", count, count * 3));
        }

        List<Object[]> finishedResults = campaignsRepository.countSubscribersPerFinishedCampaign();
        for (Object[] row : finishedResults) {
            Long campaignId = (Long) row[0];
            String title = (String) row[1];
            Long count = (Long) row[2];
            dtos.add(new LivesSavedDTO(campaignId, title, "FINALIZADA", count, count * 3));
        }

        return dtos;
    }

    @Override
    public TotalLivesSavedDTO getTotalLivesSaved() {
        Long totalSubscribers = campaignsRepository.countTotalSubscribersInFinishedCampaigns();
        Long totalFinished = campaignsRepository.countFinishedCampaigns();
        return new TotalLivesSavedDTO(totalSubscribers, totalFinished, totalSubscribers * 3);
    }


    //ORG
    @Override
    public List<ResponseCampaignsDTO> getCampaignsByOrganizer(Long organizerId) {
        List<ResponseCampaignsDTO> result = new ArrayList<>();
        for (CampaignsEntity entity : campaignsRepository.findByCreatorId(organizerId)) {
            result.add(entityToDTO(entity));
        }
        return result;
    }

    @Override
    public TotalBloodEstimatedDTO getTotalBloodByOrganizer(Long organizerId) {
        Long totalSubscribers = campaignsRepository.countTotalSubscribersInFinishedCampaignsByOrganizer(organizerId);
        Long totalFinished = campaignsRepository.countFinishedCampaignsByOrganizer(organizerId);
        double estimatedMl = totalSubscribers * 450.0;
        double estimatedLiters = estimatedMl / 1000.0;
        return new TotalBloodEstimatedDTO(totalSubscribers, totalFinished, estimatedMl, estimatedLiters);
    }

    @Override
    public List<BloodTypePercentageDTO> getBloodTypePercentageByOrganizer(Long organizerId) {
        Long totalSubscribers = campaignsRepository.countTotalSubscribersInFinishedCampaignsByOrganizer(organizerId);
        List<Object[]> results = campaignsRepository.countBloodTypesInFinishedCampaignsByOrganizer(organizerId);
        List<BloodTypePercentageDTO> dtos = new ArrayList<>();
        for (Object[] row : results) {
            String bloodGroup = row[0] != null ? row[0].toString() : "Sin grupo";
            String bloodFactor = row[1] != null ? row[1].toString() : "Sin factor";
            String bloodType = bloodGroup + "_" + bloodFactor;
            Long count = (Long) row[2];
            double percentage = totalSubscribers > 0 ? (count * 100.0 / totalSubscribers) : 0.0;
            dtos.add(new BloodTypePercentageDTO(bloodType, count, Math.round(percentage * 100.0) / 100.0));
        }
        return dtos;
    }

    @Override
    public Long getCampaignCountByOrganizer(Long organizerId) {
        return campaignsRepository.countTotalCampaignsByOrganizer(organizerId);
    }

    @Override
    public Long getFinishedCampaignCountByOrganizer(Long organizerId) {
        return campaignsRepository.countFinishedCampaignsByOrganizer(organizerId);
    }

    @Override
    public Long getTotalDonorsByOrganizer(Long organizerId) {
        return campaignsRepository.countTotalSubscribersInFinishedCampaignsByOrganizer(organizerId);
    }

    @Override
    public Double getAverageDonorsPerCampaign(Long organizerId) {
        Long totalSubscribers = campaignsRepository.countTotalSubscribersInFinishedCampaignsByOrganizer(organizerId);
        Long totalFinished = campaignsRepository.countFinishedCampaignsByOrganizer(organizerId);
        return totalFinished > 0 ? (double) totalSubscribers / totalFinished : 0.0;
    }

    @Override
    public TotalLivesSavedDTO getLivesSavedByOrganizer(Long organizerId) {
        Long totalSubscribers = campaignsRepository.countTotalSubscribersInFinishedCampaignsByOrganizer(organizerId);
        Long totalFinished = campaignsRepository.countFinishedCampaignsByOrganizer(organizerId);
        return new TotalLivesSavedDTO(totalSubscribers, totalFinished, totalSubscribers * 3);
    }

    @Override
    public List<GeographicDistributionDTO> getGeographicDistributionByOrganizer(Long organizerId) {
        List<Object[]> results = campaignsRepository.findCampaignLocationsByOrganizer(organizerId);
        List<GeographicDistributionDTO> dtos = new ArrayList<>();
        for (Object[] row : results) {
            dtos.add(new GeographicDistributionDTO(
                    (Long) row[4],
                    (String) row[3],
                    (String) row[0],
                    (Double) row[1],
                    (Double) row[2],
                    (Boolean) row[5],
                    row[6] != null
            ));
        }
        return dtos;
    }
}
