package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.donor.RequestDonorDTO;
import com.FedericoFunes.app_service.dtos.donor.ResponseDonorDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DonorService {
    //ABM
    List<ResponseDonorDTO> GetAllDonors();
    ResponseDonorDTO GetDonorById(Long id);
    ResponseDonorDTO CreateDonor(RequestDonorDTO donor);
    ResponseDonorDTO UpdateDonor(RequestDonorDTO donor, Long id);
    ResponseDonorDTO DeleteDonor(Long id);

    //OTROS

}
