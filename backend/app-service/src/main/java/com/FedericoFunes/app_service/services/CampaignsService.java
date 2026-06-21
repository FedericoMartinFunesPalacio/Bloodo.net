package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.campaigns.BloodEstimatedDTO;
import com.FedericoFunes.app_service.dtos.campaigns.BloodTypeRankingDTO;
import com.FedericoFunes.app_service.dtos.campaigns.LivesSavedDTO;
import com.FedericoFunes.app_service.dtos.campaigns.RequestCampaignsDTO;
import com.FedericoFunes.app_service.dtos.campaigns.ResponseCampaignsDTO;
import com.FedericoFunes.app_service.dtos.campaigns.SubscribedDonorDTO;
import com.FedericoFunes.app_service.dtos.campaigns.TotalBloodEstimatedDTO;
import com.FedericoFunes.app_service.dtos.campaigns.TotalLivesSavedDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CampaignsService {
    List<ResponseCampaignsDTO> getAllCampaigns();
    ResponseCampaignsDTO getCampaignById(Long id);
    ResponseCampaignsDTO createCampaign(RequestCampaignsDTO campaign);
    ResponseCampaignsDTO updateCampaign(RequestCampaignsDTO campaign, Long id);
    ResponseCampaignsDTO deleteCampaign(Long id);
    ResponseCampaignsDTO subscribeDonor(Long campaignId, Long donorId);
    List<SubscribedDonorDTO> unsubscribeDonor(Long campaignId, Long donorId);
    ResponseCampaignsDTO finishCampaign(Long campaignId, java.time.LocalDate endDate);
    List<ResponseCampaignsDTO> getAllFinishedCampaigns();
    List<SubscribedDonorDTO> getSubscribedDonors(Long campaignId);
    void notifyUpcomingCampaign(Long campaignId);
    List<BloodEstimatedDTO> getBloodEstimatedPerCampaign();
    TotalBloodEstimatedDTO getTotalBloodEstimated();
    List<BloodTypeRankingDTO> getBloodTypeRanking(Long campaignId);
    List<LivesSavedDTO> getLivesSavedPerCampaign();
    TotalLivesSavedDTO getTotalLivesSaved();
    List<ResponseCampaignsDTO> getCampaignsByOrganizer(Long organizerId);
}
