package com.FedericoFunes.app_service.services.impl;

import com.FedericoFunes.app_service.dtos.organizeremp.RequestOrganizerEmpDTO;
import com.FedericoFunes.app_service.dtos.organizeremp.ResponseOrganizerEmpDTO;
import com.FedericoFunes.app_service.entities.OrganizerEmpEntity;
import com.FedericoFunes.app_service.handlers.BadRequestException;
import com.FedericoFunes.app_service.handlers.NotFoundException;
import com.FedericoFunes.app_service.repositories.OrganizerEmpRepository;
import com.FedericoFunes.app_service.services.OrganizerEmpService;
import com.FedericoFunes.app_service.services.external.GoogleMapsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizerEmpServiceImpl implements OrganizerEmpService {

    private final OrganizerEmpRepository organizerEmpRepository;
    private final GoogleMapsService googleMapsService;

    private ResponseOrganizerEmpDTO EntityToDTO(OrganizerEmpEntity entity) {
        try {
            ResponseOrganizerEmpDTO dto = new ResponseOrganizerEmpDTO();

            dto.setId(entity.getId());
            dto.setFullName(entity.getFullName());
            dto.setDocument(entity.getDocument());
            dto.setDirection(entity.getDirection());
            dto.setLatitude(entity.getLatitude());
            dto.setLongitude(entity.getLongitude());
            dto.setEmail(entity.getEmail());
            dto.setPhoneNumber(entity.getPhoneNumber());
            dto.setIsActive(entity.getIsActive());
            return dto;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error mapping OrganizerEmpDTO: " + e.getMessage());
        }
    }
    private OrganizerEmpEntity DTOToEntity(RequestOrganizerEmpDTO dto) {
        try {
            OrganizerEmpEntity entity = new OrganizerEmpEntity();

            entity.setFullName(dto.getFullName());
            entity.setDocument(dto.getDocument());
            entity.setDirection(dto.getDirection());

            try {
                double[] latLng = googleMapsService.getLatLngFromAddress(entity.getDirection());
                entity.setLatitude(latLng[0]);
                entity.setLongitude(latLng[1]);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error fetching coordinates: " + e.getMessage());
            }

            entity.setEmail(dto.getEmail());
            entity.setPhoneNumber(dto.getPhoneNumber());
            entity.setIsActive(true);
            return entity;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error mapping OrganizerEmpEntity: " + e.getMessage());
        }
    }
    private Boolean validateOrganizerEmp(RequestOrganizerEmpDTO dto) {
        if (dto == null) return false;
        if (dto.getFullName() == null || dto.getFullName().isBlank()) return false;
        if (dto.getDocument() == null || dto.getDocument().isBlank()) return false;
        if (dto.getDirection() == null || dto.getDirection().isBlank()) return false;
        if (dto.getEmail() == null || dto.getEmail().isBlank()) return false;
        if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isBlank()) return false;
        return true;
    }

    @Override
    public List<ResponseOrganizerEmpDTO> GetAllOrganizerEmps() {
        List<ResponseOrganizerEmpDTO> result = new ArrayList<>();
        for (OrganizerEmpEntity entity : organizerEmpRepository.findAll()) {
            if (entity.getIsActive() != null && entity.getIsActive()) {
                result.add(EntityToDTO(entity));
            }
        }
        return result;
    }

    @Override
    public ResponseOrganizerEmpDTO GetOrganizerEmpById(Long id) {
        OrganizerEmpEntity entity = organizerEmpRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organizer not found"));
        if (entity.getIsActive() == null || !entity.getIsActive()) {
            throw new NotFoundException("Organizer not found");
        }
        return EntityToDTO(entity);
    }

    @Override
    public ResponseOrganizerEmpDTO CreateOrganizerEmp(RequestOrganizerEmpDTO dto) {
        if (!validateOrganizerEmp(dto)) {
            throw new BadRequestException("Invalid or incomplete organizer data");
        }
        OrganizerEmpEntity entity = DTOToEntity(dto);
        OrganizerEmpEntity saved = organizerEmpRepository.save(entity);
        return EntityToDTO(saved);
    }

    @Override
    public ResponseOrganizerEmpDTO UpdateOrganizerEmp(RequestOrganizerEmpDTO dto, Long id) {
        if (!validateOrganizerEmp(dto)) {
            throw new BadRequestException("Invalid or incomplete organizer data");
        }
        OrganizerEmpEntity entity = organizerEmpRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organizer not found"));
        if (entity.getIsActive() == null || !entity.getIsActive()) {
            throw new NotFoundException("Organizer not found");
        }
        entity.setFullName(dto.getFullName());
        entity.setDocument(dto.getDocument());
        entity.setDirection(dto.getDirection());

        try {
            double[] latLng = googleMapsService.getLatLngFromAddress(entity.getDirection());
            entity.setLatitude(latLng[0]);
            entity.setLongitude(latLng[1]);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Error fetching coordinates: " + e.getMessage());
        }
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        OrganizerEmpEntity updated = organizerEmpRepository.save(entity);
        return EntityToDTO(updated);
    }

    @Override
    public ResponseOrganizerEmpDTO DeleteOrganizerEmp(Long id) {
        OrganizerEmpEntity entity = organizerEmpRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organizer not found"));
        entity.setIsActive(false);
        OrganizerEmpEntity updated = organizerEmpRepository.save(entity);
        return EntityToDTO(updated);
    }
}
