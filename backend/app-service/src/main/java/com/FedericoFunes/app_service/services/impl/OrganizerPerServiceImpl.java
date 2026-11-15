package com.FedericoFunes.app_service.services.impl;

import com.FedericoFunes.app_service.dtos.organizerper.RequestOrganizerPerDTO;
import com.FedericoFunes.app_service.dtos.organizerper.ResponseOrganizerPerDTO;
import com.FedericoFunes.app_service.entities.OrganizerPerEntity;
import com.FedericoFunes.app_service.handlers.BadRequestException;
import com.FedericoFunes.app_service.handlers.NotFoundException;
import com.FedericoFunes.app_service.repositories.OrganizerPerRepository;
import com.FedericoFunes.app_service.services.OrganizerPerService;
import com.FedericoFunes.app_service.services.external.GoogleMapsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizerPerServiceImpl implements OrganizerPerService {

    private final OrganizerPerRepository organizerPerRepository;
    private final GoogleMapsService googleMapsService;

    private ResponseOrganizerPerDTO EntityToDTO(OrganizerPerEntity entity) {
        try {
            ResponseOrganizerPerDTO dto = new ResponseOrganizerPerDTO();
            dto.setId(entity.getId());
            dto.setFirstName(entity.getFirstName());
            dto.setLastName(entity.getLastName());
            dto.setBirthdate(entity.getBirthdate());
            dto.setDocument(entity.getDocument());
            dto.setDirection(entity.getDirection());
            dto.setLatitude(entity.getLatitude());
            dto.setLongitude(entity.getLongitude());
            dto.setGender(entity.getGender());
            dto.setEmail(entity.getEmail());
            dto.setPhoneNumber(entity.getPhoneNumber());
            dto.setIsActive(entity.getIsActive());
            return dto;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error mapping OrganizerPerDTO: " + e.getMessage());
        }
    }
    private OrganizerPerEntity DTOToEntity(RequestOrganizerPerDTO dto) {
        try {
            OrganizerPerEntity entity = new OrganizerPerEntity();
            entity.setFirstName(dto.getFirstName());
            entity.setLastName(dto.getLastName());
            entity.setBirthdate(dto.getBirthdate());
            entity.setDocument(dto.getDocument());
            entity.setDirection(dto.getDirection());
            try {
                double[] latLng = googleMapsService.getLatLngFromAddress(entity.getDirection());
                entity.setLatitude(latLng[0]);
                entity.setLongitude(latLng[1]);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error fetching coordinates: " + e.getMessage());
            }
            entity.setGender(dto.getGender());
            entity.setEmail(dto.getEmail());
            entity.setPhoneNumber(dto.getPhoneNumber());
            entity.setIsActive(true);
            return entity;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error mapping OrganizerPerEntity: " + e.getMessage());
        }
    }
    private Boolean validateOrganizerPer(RequestOrganizerPerDTO dto) {
        if (dto == null) return false;
        if (dto.getFirstName() == null || dto.getFirstName().isBlank()) return false;
        if (dto.getLastName() == null || dto.getLastName().isBlank()) return false;
        if (dto.getBirthdate() == null) return false;
        if (dto.getDocument() == null || dto.getDocument().isBlank()) return false;
        if (dto.getDirection() == null || dto.getDirection().isBlank()) return false;
        if (dto.getGender() == null) return false;
        if (dto.getEmail() == null || dto.getEmail().isBlank()) return false;
        if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isBlank()) return false;
        return true;
    }

    @Override
    public List<ResponseOrganizerPerDTO> GetAllOrganizerPers() {
        List<ResponseOrganizerPerDTO> result = new ArrayList<>();
        for (OrganizerPerEntity entity : organizerPerRepository.findAll()) {
            if (entity.getIsActive() != null && entity.getIsActive()) {
                result.add(EntityToDTO(entity));
            }
        }
        return result;
    }

    @Override
    public ResponseOrganizerPerDTO GetOrganizerPerById(Long id) {
        OrganizerPerEntity entity = organizerPerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organizer not found"));
        if (entity.getIsActive() == null || !entity.getIsActive()) {
            throw new NotFoundException("Organizer not found");
        }
        return EntityToDTO(entity);
    }

    @Override
    public ResponseOrganizerPerDTO CreateOrganizerPer(RequestOrganizerPerDTO dto) {
        if (!validateOrganizerPer(dto)) {
            throw new BadRequestException("Invalid or incomplete organizer data");
        }
        return EntityToDTO(organizerPerRepository.save(DTOToEntity(dto)));
    }

    @Override
    public ResponseOrganizerPerDTO UpdateOrganizerPer(RequestOrganizerPerDTO dto, Long id) {
        if (!validateOrganizerPer(dto)) {
            throw new BadRequestException("Invalid or incomplete organizer data");
        }
        OrganizerPerEntity entity = organizerPerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organizer not found"));
        if (entity.getIsActive() == null || !entity.getIsActive()) {
            throw new NotFoundException("Organizer not found");
        }
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setBirthdate(dto.getBirthdate());
        entity.setDocument(dto.getDocument());
        entity.setDirection(dto.getDirection());
        try {
            double[] latLng = googleMapsService.getLatLngFromAddress(entity.getDirection());
            entity.setLatitude(latLng[0]);
            entity.setLongitude(latLng[1]);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error fetching coordinates: " + e.getMessage());
        }
        entity.setGender(dto.getGender());
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        return EntityToDTO(organizerPerRepository.save(entity));
    }

    @Override
    public ResponseOrganizerPerDTO DeleteOrganizerPer(Long id) {
        OrganizerPerEntity entity = organizerPerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organizer not found"));
        entity.setIsActive(false);
        return EntityToDTO(organizerPerRepository.save(entity));
    }
}

