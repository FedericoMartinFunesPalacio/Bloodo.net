package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.campaigns.BloodTypeRankingDTO;
import com.FedericoFunes.app_service.dtos.donor.BloodTypePercentageDTO;
import com.FedericoFunes.app_service.dtos.donor.DonorHealthDTO;
import com.FedericoFunes.app_service.dtos.donor.DonorStatsDTO;
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
    List<BloodTypeRankingDTO> GetBloodTypeRanking();
    List<BloodTypePercentageDTO> GetBloodTypePercentage();

    //METRICS
    DonorStatsDTO GetDonorStats(Long donorId);
    DonorHealthDTO GetDonorHealth(Long donorId);
}
