package com.FedericoFunes.app_service.services.impl;

import com.FedericoFunes.app_service.dtos.campaigns.BloodTypeRankingDTO;
import com.FedericoFunes.app_service.dtos.donor.BloodTypePercentageDTO;
import com.FedericoFunes.app_service.dtos.donor.DonorHealthDTO;
import com.FedericoFunes.app_service.dtos.donor.DonorStatsDTO;
import com.FedericoFunes.app_service.dtos.donor.RequestDonorDTO;
import com.FedericoFunes.app_service.dtos.donor.ResponseDonorDTO;
import com.FedericoFunes.app_service.entities.DonorEntity;
import com.FedericoFunes.app_service.handlers.BadRequestException;
import com.FedericoFunes.app_service.handlers.NotFoundException;
import com.FedericoFunes.app_service.repositories.DonorRepository;
import com.FedericoFunes.app_service.services.DonorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonorServiceImpl implements DonorService {

    private final DonorRepository donorRepository;

    private ResponseDonorDTO EntityToDTO (DonorEntity dE) {
        try {
            ResponseDonorDTO dto = new ResponseDonorDTO();

            dto.setId(dE.getId());
            dto.setFirstName(dE.getFirstName());
            dto.setLastName(dE.getLastName());
            dto.setBirthdate(dE.getBirthdate());
            dto.setDocument(dE.getDocument());
            dto.setBloodFactor(dE.getBloodFactor());
            dto.setBloodGroup(dE.getBloodGroup());
            dto.setGender(dE.getGender());
            dto.setHeight(dE.getHeight());
            dto.setWeight(dE.getWeight());
            dto.setEmail(dE.getEmail());
            dto.setPhoneNumber(dE.getPhoneNumber());
            dto.setIsActive(dE.getIsActive());
            return dto;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500),"Error mapping donorDTO: " + e.getMessage());
        }
    }
    private DonorEntity DTOToEntity (RequestDonorDTO dto) {
        try {
            DonorEntity dE = new DonorEntity();

            dE.setFirstName(dto.getFirstName());
            dE.setLastName(dto.getLastName());
            dE.setBirthdate(dto.getBirthdate());
            dE.setDocument(dto.getDocument());
            dE.setBloodFactor(dto.getBloodFactor());
            dE.setBloodGroup(dto.getBloodGroup());
            dE.setGender(dto.getGender());
            dE.setHeight(dto.getHeight());
            dE.setWeight(dto.getWeight());
            dE.setEmail(dto.getEmail());
            dE.setPhoneNumber(dto.getPhoneNumber());
            dE.setIsActive(true);

            return dE;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500),"Error mapping donorEntity: " + e.getMessage());
        }
    }
    private Boolean validateDonor (RequestDonorDTO dR) {
        if (dR.getFirstName() == null ||
                dR.getLastName() == null ||
                dR.getBirthdate() == null ||
                dR.getDocument() == null ||
                dR.getBloodFactor() == null ||
                dR.getBloodGroup() == null ||
                dR.getGender() == null ||
                dR.getHeight() == null ||
                dR.getWeight() == null ||
                dR.getEmail() == null ||
                dR.getPhoneNumber() == null) {
            throw new BadRequestException("Validation failed: fields empty or null");
        }
        return true;
    }

    @Override
    public List<ResponseDonorDTO> GetAllDonors() {
        try {
            List<DonorEntity> donorEntities = donorRepository.findAll();
            List<ResponseDonorDTO> donorDTOS = new ArrayList<>();
            for (DonorEntity dE : donorEntities) {
                if (dE.getIsActive()) {
                    ResponseDonorDTO dto = EntityToDTO(dE);
                    donorDTOS.add(dto);
                }
            }
            return donorDTOS;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500),"Error finding all donors: " + e.getMessage());
        }
    }

    @Override
    public ResponseDonorDTO GetDonorById(Long id) {
        DonorEntity dE;
        try {
            dE = donorRepository.findById(id).get();
        } catch (Exception e) {
            throw new NotFoundException("Donor whit id: " + id + " not found");
        }
        if (dE.getIsActive()) {
            return EntityToDTO(dE);
        } else {
            throw new NotFoundException("Donor whit id: " + id + " is inactive");
        }

    }

    @Override
    public ResponseDonorDTO CreateDonor(RequestDonorDTO donor) {
        try {
            if (validateDonor(donor)) {
                DonorEntity dE = DTOToEntity(donor);
                DonorEntity savedDE = donorRepository.save(dE);
                return EntityToDTO(savedDE);
            }
            return null;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500),"Error creating donor: " + e.getMessage());
        }
    }

    @Override
    public ResponseDonorDTO UpdateDonor(RequestDonorDTO donor, Long id) {
        try {
            if (validateDonor(donor)) {
                DonorEntity dE;
                try {
                    dE = donorRepository.findById(id).get();
                } catch (Exception e) {
                    throw new NotFoundException("Donor whit id: " + id + " not found");
                }
                if (dE.getIsActive()) {
                    dE.setFirstName(donor.getFirstName());
                    dE.setLastName(donor.getLastName());
                    dE.setBirthdate(donor.getBirthdate());
                    dE.setDocument(donor.getDocument());
                    dE.setBloodFactor(donor.getBloodFactor());
                    dE.setBloodGroup(donor.getBloodGroup());
                    dE.setGender(donor.getGender());
                    dE.setHeight(donor.getHeight());
                    dE.setWeight(donor.getWeight());
                    dE.setEmail(donor.getEmail());
                    dE.setPhoneNumber(donor.getPhoneNumber());
                    dE.setIsActive(true);

                    DonorEntity rDE = donorRepository.save(dE);

                    return EntityToDTO(rDE);
                } else {
                    throw new BadRequestException("Donor whit id: " + id + " is inactive, update not allowed");
                }
            }
            return null;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500),"Error updating donor: " + e.getMessage());
        }
    }

    @Override
    public ResponseDonorDTO DeleteDonor(Long id) {
        try {
            DonorEntity dE;
            try {
                dE = donorRepository.findById(id).get();
            } catch (Exception e) {
                throw new NotFoundException("Donor whit id: " + id + " not found");
            }
            if (dE.getIsActive()) {
                dE.setIsActive(false);
                DonorEntity rDE = donorRepository.save(dE);
                return EntityToDTO(rDE);
            } else {
                throw new BadRequestException("Donor whit id: " + id + " is inactive, deleted not allowed");
            }

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500),"Error deleting donor: " + e.getMessage());
        }
     }

    @Override
    public List<BloodTypeRankingDTO> GetBloodTypeRanking() {
        List<Object[]> results = donorRepository.countBloodTypesGlobally();
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
    public List<BloodTypePercentageDTO> GetBloodTypePercentage() {
        Long totalDonors = donorRepository.countActiveDonors();
        List<Object[]> results = donorRepository.countBloodTypesGlobally();
        List<BloodTypePercentageDTO> dtos = new ArrayList<>();
        for (Object[] row : results) {
            String bloodGroup = row[0] != null ? row[0].toString() : "Sin grupo";
            String bloodFactor = row[1] != null ? row[1].toString() : "Sin factor";
            String bloodType = bloodGroup + "_" + bloodFactor;
            Long count = (Long) row[2];
            double percentage = totalDonors > 0 ? (count * 100.0 / totalDonors) : 0.0;
            dtos.add(new BloodTypePercentageDTO(bloodType, count, Math.round(percentage * 100.0) / 100.0));
        }
        return dtos;
    }

    @Override
    public DonorStatsDTO GetDonorStats(Long donorId) {
        DonorEntity donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new NotFoundException("Donor not found"));
        if (!donor.getIsActive()) {
            throw new NotFoundException("Donor is inactive");
        }

        Long campaignsAttended = donorRepository.countFinishedCampaignsByDonor(donorId);
        double estimatedMl = campaignsAttended * 450.0;
        double estimatedLiters = campaignsAttended * 0.45;

        return new DonorStatsDTO(campaignsAttended, estimatedMl, estimatedLiters);
    }

    @Override
    public DonorHealthDTO GetDonorHealth(Long donorId) {
        DonorEntity donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new NotFoundException("Donor not found"));
        if (!donor.getIsActive()) {
            throw new NotFoundException("Donor is inactive");
        }

        String bloodType = donor.getBloodGroup() + "_" + donor.getBloodFactor();

        int age = java.time.Period.between(donor.getBirthdate(), java.time.LocalDate.now()).getYears();

        double bmi = donor.getWeight() / (donor.getHeight() * donor.getHeight());

        String lastDonationDate = null;
        String nextEligibleDate = null;

        List<java.time.LocalDate> endDates = donorRepository.findFinishedCampaignEndDatesByDonor(donorId);
        if (!endDates.isEmpty()) {
            java.time.LocalDate lastDate = endDates.get(0);
            lastDonationDate = lastDate.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            java.time.LocalDate nextEligible = lastDate.plusMonths(3);
            nextEligibleDate = nextEligible.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        }

        return new DonorHealthDTO(bloodType, lastDonationDate, nextEligibleDate, bmi, age);
    }
}
