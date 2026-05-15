package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.RequestDummyDTO;
import com.FedericoFunes.app_service.dtos.ResponseDummyDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DummyService {
    ResponseDummyDTO createDummy(RequestDummyDTO dummy);
    ResponseDummyDTO updateDummy(RequestDummyDTO dummy, Long id);
    ResponseDummyDTO getDummyById(Long id);
    List<ResponseDummyDTO> getAllDummys();
}
