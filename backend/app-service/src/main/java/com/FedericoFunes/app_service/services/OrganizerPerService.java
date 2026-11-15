package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.organizerper.RequestOrganizerPerDTO;
import com.FedericoFunes.app_service.dtos.organizerper.ResponseOrganizerPerDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrganizerPerService {
    List<ResponseOrganizerPerDTO> GetAllOrganizerPers();
    ResponseOrganizerPerDTO GetOrganizerPerById(Long id);
    ResponseOrganizerPerDTO CreateOrganizerPer(RequestOrganizerPerDTO organizerPer);
    ResponseOrganizerPerDTO UpdateOrganizerPer(RequestOrganizerPerDTO organizerPer, Long id);
    ResponseOrganizerPerDTO DeleteOrganizerPer(Long id);
}

