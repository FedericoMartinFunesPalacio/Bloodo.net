package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.campaigns.RequestCampaignsDTO;
import com.FedericoFunes.app_service.dtos.campaigns.ResponseCampaignsDTO;
import com.FedericoFunes.app_service.dtos.campaigns.SubscribedDonorDTO;
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
}
