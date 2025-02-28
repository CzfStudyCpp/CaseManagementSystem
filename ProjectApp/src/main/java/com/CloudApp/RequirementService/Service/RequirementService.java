package com.CloudApp.RequirementService.Service;

import com.CloudApp.RequirementService.DTO.RequirementCommentDTO;
import com.CloudApp.RequirementService.DTO.RequirementDTO;
import com.CloudApp.RequirementService.DTO.SkillStatisticsDTO;

import java.util.List;
import java.util.Map;

public interface RequirementService {
    List<RequirementDTO> getRecommendedRequirements();
    List<RequirementDTO> getRequirementsBySkills(List<Long> skillIds);

    RequirementDTO getRequirementDetail(Long id);

    boolean checkIfFollowed(Long requirementId, String username);

    boolean followRequirement(Long requirementId, String username);

    boolean unfollowRequirement(Long requirementId, String username);

    void addComment(Long requirementId, String content, String username);

    List<RequirementCommentDTO> getCommentsByRequirementId(Long requirementId, int page, int pageSize);

    int countCommentsByRequirementId(Long requirementId);

    List<RequirementDTO> getFavoriteRequirements(String username);

    List<RequirementDTO> getMyDemands(String username);

    void addDemand(RequirementDTO requirementDTO, String username);

    void updateDemand(Long id, RequirementDTO requirementDTO, String username);

    void deleteDemand(Long id, String username);

    boolean inviteCollaborate(Long demandId, String username, String inviteeUsername);
    List<Map<String, Object>> getSkillStatistics();
}

