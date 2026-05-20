package com.FedericoFunes.app_service.services.impl;

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
}
