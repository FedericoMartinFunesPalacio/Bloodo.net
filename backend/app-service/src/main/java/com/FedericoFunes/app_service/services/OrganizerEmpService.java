package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.organizeremp.RequestOrganizerEmpDTO;
import com.FedericoFunes.app_service.dtos.organizeremp.ResponseOrganizerEmpDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrganizerEmpService {
    List<ResponseOrganizerEmpDTO> GetAllOrganizerEmps();
    ResponseOrganizerEmpDTO GetOrganizerEmpById(Long id);
    ResponseOrganizerEmpDTO CreateOrganizerEmp(RequestOrganizerEmpDTO organizerEmp);
    ResponseOrganizerEmpDTO UpdateOrganizerEmp(RequestOrganizerEmpDTO organizerEmp, Long id);
    ResponseOrganizerEmpDTO DeleteOrganizerEmp(Long id);
}

