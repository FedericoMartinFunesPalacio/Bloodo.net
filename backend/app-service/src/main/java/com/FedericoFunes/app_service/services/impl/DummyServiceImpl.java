package com.FedericoFunes.app_service.services.impl;

import com.FedericoFunes.app_service.dtos.RequestDummyDTO;
import com.FedericoFunes.app_service.dtos.ResponseDummyDTO;
import com.FedericoFunes.app_service.entities.DummyEntity;
import com.FedericoFunes.app_service.repositories.DummyRepository;
import com.FedericoFunes.app_service.services.DummyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DummyServiceImpl implements DummyService {

    private final DummyRepository dummyRepository;

    private ResponseDummyDTO EntityToDTO(DummyEntity dummy) {
        ResponseDummyDTO dto = new ResponseDummyDTO();
        dto.setId(dummy.getId());
        dto.setName(dummy.getName());
        dto.setDescription(dummy.getDescription());
        dto.setSerialNumber(dummy.getSerialNumber());
        dto.setCreatedAt(dummy.getCreatedAt());
        return dto;
    }

    private DummyEntity DTOtoEntity(RequestDummyDTO dto) {
        DummyEntity dummy = new DummyEntity();
        dummy.setName(dto.getName());
        dummy.setDescription(dto.getDescription());
        dummy.setCreatedAt(dto.getCreatedAt());
        dummy.setSerialNumber(dto.getSerialNumber());
        return dummy;
    }

    private DummyEntity updateEntity(DummyEntity dummy, RequestDummyDTO dto) {
        dummy.setName(dto.getName());
        dummy.setDescription(dto.getDescription());
        dummy.setCreatedAt(dto.getCreatedAt());
        dummy.setSerialNumber(dto.getSerialNumber());
        return dummy;
    }

    @Override
    public ResponseDummyDTO createDummy(RequestDummyDTO dto) {
        DummyEntity dummy = DTOtoEntity(dto);
        return EntityToDTO(dummyRepository.save(dummy));
    }

    @Override
    public ResponseDummyDTO updateDummy(RequestDummyDTO dummy, Long id) {
        return EntityToDTO(dummyRepository.save(updateEntity(dummyRepository.findById(id).get(), dummy)));
    }

    @Override
    public ResponseDummyDTO getDummyById(Long id) {
       return EntityToDTO(dummyRepository.findById(id).orElseThrow(() -> new RuntimeException("Dummy not found")));
    }

    @Override
    public List<ResponseDummyDTO> getAllDummys() {
        List<ResponseDummyDTO> dummys = new ArrayList<>();
        List<DummyEntity> entities = dummyRepository.findAll();
        for (DummyEntity entity : entities) {
            dummys.add(EntityToDTO(entity));
        }
        return dummys;
    }
}
