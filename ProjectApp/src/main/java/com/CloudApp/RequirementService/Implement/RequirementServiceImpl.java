package com.CloudApp.RequirementService.Implement;

import com.CloudApp.LoginAndRegister.Entities.User;

import com.CloudApp.LoginAndRegister.Mapper.UserMapper;
import com.CloudApp.LoginAndRegister.Service.EmailService;
import com.CloudApp.RequirementService.DTO.RequirementCommentDTO;
import com.CloudApp.RequirementService.DTO.RequirementDTO;
import com.CloudApp.RequirementService.DTO.SkillStatisticsDTO;
import com.CloudApp.RequirementService.Entity.Requirement;
import com.CloudApp.RequirementService.Entity.RequirementComment;
import com.CloudApp.RequirementService.Mapper.RequirementCommentMapper;
import com.CloudApp.RequirementService.Mapper.RequirementMapper;
import com.CloudApp.RequirementService.Mapper.RequirementSkillMapper;
import com.CloudApp.RequirementService.Mapper.UserRequirementFavoriteMapper;
import com.CloudApp.RequirementService.Service.RequirementService;
import com.CloudApp.SkillManagement.Entity.Skill;
import com.CloudApp.SkillManagement.Entity.UserSkill;
import com.CloudApp.SkillManagement.Mapper.SkillMapper;
import com.CloudApp.SkillManagement.Mapper.UserSkillMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RequirementServiceImpl implements RequirementService {


    private final RequirementCommentMapper requirementCommentMapper;

    private final RequirementMapper requirementMapper;

    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private final UserSkillMapper userSkillMapper;

    private final UserRequirementFavoriteMapper userRequirementFavoritesMapper;

    private final RequirementSkillMapper requirementSkillMapper;
    private final SkillMapper skillMapper;

    private final EmailService emailService;

    private final JdbcTemplate jdbcTemplate;
    private static final String RECOMMENDED_DEMANDS_LIST_KEY = "recommended_demands_list_key";
    private static final String RECOMMENDED_DEMANDS_LIST_SKILL = "recommended_demands_list_skill";
    private static final String REQUIREMENT_DETAIL="requirement_detail_";
    private static final String SKILL_STATISTICS="skill_statistics";
    private static final int REDIS_EXPIRED_TIME=20;//20分钟存储时间
    @Autowired
    public RequirementServiceImpl(RequirementCommentMapper requirementCommentMapper, RequirementMapper requirementMapper, UserMapper userMapper, RedisTemplate<String, Object> redisTemplate, UserSkillMapper userSkillMapper, UserRequirementFavoriteMapper userRequirementFavoritesMapper1, RequirementSkillMapper requirementSkillMapper, SkillMapper skillMapper, EmailService emailService, JdbcTemplate jdbcTemplate){
        this.requirementCommentMapper = requirementCommentMapper;

        this.requirementMapper = requirementMapper;
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
        this.userSkillMapper = userSkillMapper;
        this.userRequirementFavoritesMapper = userRequirementFavoritesMapper1;

        this.requirementSkillMapper = requirementSkillMapper;
        this.skillMapper = skillMapper;
        this.emailService = emailService;
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<RequirementDTO> getRecommendedRequirements() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前的推荐用户为" + username);
        String redisKey = RECOMMENDED_DEMANDS_LIST_KEY + username;

        // 查询 Redis 缓存
        List<RequirementDTO> cachedDemands = (List<RequirementDTO>) redisTemplate.opsForValue().get(redisKey);
        if (cachedDemands != null) {
            System.out.println("Redis推荐列表存在..........");
            return cachedDemands; // 如果缓存存在，直接返回
        }

        // 匿名用户逻辑
        if ("anonymousUser".equals(username) || username == null) {
            List<Requirement> requirements = requirementMapper.selectAllPublishedRequirements();

            // 匿名用户按关注人数推荐
            List<RequirementDTO> anonymousRecommendedDemands = requirements.stream()
                    .map(requirement -> {
                        int favoriteCount = userRequirementFavoritesMapper.countFavoritesByRequirementId(requirement.getId());

                        RequirementDTO dto = new RequirementDTO();
                        BeanUtils.copyProperties(requirement, dto);
                        dto.setScore(favoriteCount); // 使用关注人数作为评分

                        // 填充技能字段
                        List<String> skillNames = requirementSkillMapper.selectSkillNamesByRequirementId(requirement.getId());
                        dto.setSkills(skillNames);

                        return dto;
                    })
                    .sorted(Comparator.comparingInt(RequirementDTO::getScore).reversed()) // 按关注人数降序排序
                    .collect(Collectors.toList());

            redisTemplate.opsForValue().set(redisKey, anonymousRecommendedDemands, REDIS_EXPIRED_TIME, TimeUnit.MINUTES);
            System.out.println("用户为空");
            return anonymousRecommendedDemands;
        }

        User currentUser = userMapper.getUserByUsername(username);
        if (currentUser == null) {
            throw new IllegalArgumentException("无法找到当前用户");
        }
        Long userId = currentUser.getId();

        List<UserSkill> userSkills = userSkillMapper.getUserSkillsByUserId(userId);
        Map<Long, String> userSkillMap = userSkills.stream()
                .collect(Collectors.toMap(UserSkill::getSkillId, UserSkill::getProficiency));

        List<Long> favoriteRequirementSkillIds = getFavoriteRequirementSkillIds(userId);

        List<Requirement> requirements = requirementMapper.selectAllPublishedRequirements();

        List<RequirementDTO> recommendedDemands = requirements.stream()
                .map(requirement -> {
                    List<Long> requirementSkillIds = requirementSkillMapper.selectSkillIdsByRequirementId(requirement.getId());

                    // 计算评分
                    int score = 0;
                    long matchedSkills = requirementSkillIds.stream().filter(userSkillMap::containsKey).count();
                    score += (int) (matchedSkills * 100 * 0.4);

                    int proficiencyScore = requirementSkillIds.stream()
                            .filter(userSkillMap::containsKey)
                            .mapToInt(skillId -> {
                                String proficiency = userSkillMap.get(skillId);
                                switch (proficiency) {
                                    case "初级":
                                        return 60;
                                    case "中级":
                                        return 80;
                                    case "专家":
                                        return 100;
                                    default:
                                        return 0;
                                }
                            })
                            .sum();
                    score += (int) (proficiencyScore * 0.4);

                    long favoriteMatchedSkills = requirementSkillIds.stream().filter(favoriteRequirementSkillIds::contains).count();
                    score += (int) (favoriteMatchedSkills * 80 * 0.2);

                    RequirementDTO dto = new RequirementDTO();
                    BeanUtils.copyProperties(requirement, dto);
                    dto.setScore(score);

                    // 填充技能字段
                    List<String> skillNames = requirementSkillMapper.selectSkillNamesByRequirementId(requirement.getId());
                    dto.setSkills(skillNames);

                    return dto;
                })
                .sorted(Comparator.comparingInt(RequirementDTO::getScore).reversed())
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(redisKey, recommendedDemands, REDIS_EXPIRED_TIME, TimeUnit.MINUTES);
        System.out.println("已完成推荐列表生成");
        return recommendedDemands;
    }



    @Override
    public List<RequirementDTO> getRequirementsBySkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            throw new IllegalArgumentException("技能 ID 列表不能为空");
        }

        String cacheKey = RECOMMENDED_DEMANDS_LIST_SKILL + skillIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        List<RequirementDTO> cachedDemands = (List<RequirementDTO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedDemands != null) {
            return cachedDemands;
        }

        // 使用 JDBC 查询
        String sql = "SELECT DISTINCT r.* FROM requirements r " +
                "JOIN requirement_skills rs ON r.id = rs.requirement_id " +
                "WHERE rs.skill_id IN (" + skillIds.stream().map(id -> "?").collect(Collectors.joining(",")) + ")";
        Object[] params = skillIds.toArray();

        List<Requirement> requirements = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            Requirement requirement = new Requirement();
            requirement.setId(rs.getLong("id"));
            requirement.setTitle(rs.getString("title"));
            requirement.setBudget(rs.getBigDecimal("budget"));
            requirement.setStatus(rs.getString("status"));
            requirement.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            requirement.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return requirement;
        });

        // 转换为 DTO
        List<RequirementDTO> requirementDTOs = requirements.stream()
                .map(requirement -> {
                    RequirementDTO dto = new RequirementDTO();
                    BeanUtils.copyProperties(requirement, dto);

                    // 查询关注人数
                    int favoritesCount = userRequirementFavoritesMapper.countFavoritesByRequirementId(requirement.getId());
                    dto.setFavoritesCount(favoritesCount);

                    // 填充技能字段
                    List<String> skillNames = requirementSkillMapper.selectSkillNamesByRequirementId(requirement.getId());
                    dto.setSkills(skillNames);

                    return dto;
                })
                .sorted(Comparator.comparingInt(RequirementDTO::getFavoritesCount).reversed())
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(cacheKey, requirementDTOs, REDIS_EXPIRED_TIME, TimeUnit.MINUTES);
        return requirementDTOs;
    }



    public List<Long> getFavoriteRequirementSkillIds(Long userId) {
        // 查询用户关注的需求 ID 和关联的技能 ID
        String sql = "SELECT DISTINCT rs.skill_id " +
                "FROM user_requirement_favorites urf " +
                "JOIN requirement_skills rs ON urf.requirement_id = rs.requirement_id " +
                "WHERE urf.user_id = ?";

        // 使用 JDBC 查询
        return jdbcTemplate.queryForList(sql, new Object[]{userId}, Long.class);
    }

    @Override
    public RequirementDTO getRequirementDetail(Long id){
        String cacheKey = REQUIREMENT_DETAIL+ id;
        RequirementDTO cachedRequirement = (RequirementDTO) redisTemplate.opsForValue().get(cacheKey);

        if (cachedRequirement != null) {
            System.out.println("需求详情命中缓存");
            return cachedRequirement;
        }

        Requirement requirement = requirementMapper.selectById(id);
        if (requirement == null) {
            return null;
        }

        RequirementDTO dto = new RequirementDTO();
        BeanUtils.copyProperties(requirement, dto);

        List<String> skillNames = requirementSkillMapper.selectSkillNamesByRequirementId(requirement.getId());
        dto.setSkills(skillNames);

        // 写入缓存
        redisTemplate.opsForValue().set(cacheKey, dto, REDIS_EXPIRED_TIME, TimeUnit.MINUTES);
        return dto;
    }

    @Override
    public boolean checkIfFollowed(Long requirementId, String username){
        // 检查参数
        if (requirementId == null || username == null || username.isEmpty()) {
            throw new IllegalArgumentException("需求 ID 和用户名不能为空");
        }

        // 从 UserMapper 获取当前用户信息
        User currentUser = userMapper.getUserByUsername(username);
        if (currentUser == null) {
            throw new IllegalArgumentException("无法找到当前用户");
        }

        Long userId = currentUser.getId();

        // 查询 UserRequirementFavorite 表，检查是否存在关注记录
        int count = userRequirementFavoritesMapper.countByUserIdAndRequirementId(userId, requirementId);

        // 如果有记录，表示已关注
        return count > 0;
    }


    @Override
    public boolean followRequirement(Long requirementId, String username) {
        // 关注逻辑（保持不变）
        User user = userMapper.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 检查是否已关注
        int count = userRequirementFavoritesMapper.countByUserIdAndRequirementId(user.getId(), requirementId);
        if (count > 0) {
            throw new IllegalStateException("已经关注过该需求");
        }

        redisTemplate.delete(RECOMMENDED_DEMANDS_LIST_KEY + username);
        // 插入关注记录
        return userRequirementFavoritesMapper.insertFavorite(user.getId(), requirementId) > 0;
    }

    @Override
    public boolean unfollowRequirement(Long requirementId, String username) {
        // 取消关注逻辑（保持不变）
        User user = userMapper.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 清除缓存
        redisTemplate.delete(RECOMMENDED_DEMANDS_LIST_KEY + username);

        // 删除关注记录
        return userRequirementFavoritesMapper.deleteFavorite(user.getId(), requirementId) > 0;
    }

    @Override
    public void addComment(Long requirementId, String content, String username) {
        User user = userMapper.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        RequirementComment comment = new RequirementComment();
        comment.setRequirementId(requirementId);
        comment.setUserId(user.getId());
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());

        requirementCommentMapper.insert(comment);
    }

    @Override
    public List<RequirementCommentDTO> getCommentsByRequirementId(Long requirementId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;

        // 从 Mapper 获取实体类
        List<RequirementComment> comments = requirementCommentMapper.getCommentsByRequirementIdWithPagination(requirementId, offset, pageSize);

        // 转换为 DTO
        return comments.stream().map(comment -> {
            RequirementCommentDTO dto = new RequirementCommentDTO();
            BeanUtils.copyProperties(comment, dto);

            // 从 UserMapper 获取用户名
            User user = userMapper.selectById(comment.getUserId());
            dto.setUsername(user != null ? user.getUsername() : "未知用户");

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public int countCommentsByRequirementId(Long requirementId) {
        return requirementCommentMapper.countCommentsByRequirementId(requirementId);
    }

    @Override
    public List<RequirementDTO> getFavoriteRequirements(String username) {
        // 获取当前用户信息
        User user = userMapper.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("无法找到用户: " + username);
        }

        // 获取用户关注的需求 ID 列表
        List<Long> favoriteRequirementIds = userRequirementFavoritesMapper.selectFavoriteRequirementIds(user.getId());
        if (favoriteRequirementIds.isEmpty()) {
            return new ArrayList<>(); // 如果没有关注的需求，直接返回空列表
        }

        // 使用 JDBC 查询需求详情
        List<Requirement> requirements = fetchRequirementsByIds(favoriteRequirementIds);

        // 转换为 DTO
        return requirements.stream()
                .map(requirement -> {
                    RequirementDTO dto = new RequirementDTO();
                    BeanUtils.copyProperties(requirement, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 使用 JDBC 查询需求详情
     */
    private List<Requirement> fetchRequirementsByIds(List<Long> ids) {
        // 动态构造 SQL
        String sql = "SELECT * FROM requirements WHERE id IN (" +
                ids.stream().map(id -> "?").collect(Collectors.joining(",")) +
                ")";

        // 将 ID 列表转为 Object 数组
        Object[] params = ids.toArray();

        // 执行 JDBC 查询
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            Requirement requirement = new Requirement();
            requirement.setId(rs.getLong("id"));
            requirement.setTitle(rs.getString("title"));
            requirement.setBudget(rs.getBigDecimal("budget"));
            requirement.setStatus(rs.getString("status"));
            requirement.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            requirement.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return requirement;
        });
    }

    @Override
    public List<RequirementDTO> getMyDemands(String username) {
        // 获取当前用户
        User user = userMapper.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 查询用户创建的需求列表
        List<Requirement> requirements = requirementMapper.selectByUserId(user.getId());
        if (requirements.isEmpty()) {
            return new ArrayList<>();
        }

        // 转换为 DTO
        return requirements.stream().map(requirement -> {
            RequirementDTO dto = new RequirementDTO();
            BeanUtils.copyProperties(requirement, dto);

            // 获取需求相关的技能
            List<String> skillNames = requirementSkillMapper.selectSkillNamesByRequirementId(requirement.getId());
            dto.setSkills(skillNames);

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void addDemand(RequirementDTO requirementDTO, String username) {
        // 获取当前用户
        User user = userMapper.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 构建需求实体
        Requirement requirement = new Requirement();
        requirement.setTitle(requirementDTO.getTitle());
        requirement.setDescription(requirementDTO.getDescription());
        requirement.setBudget(requirementDTO.getBudget());
        requirement.setStatus(requirementDTO.getStatus());
        requirement.setUserId(user.getId());
        requirement.setCreatedAt(LocalDateTime.now());
        requirement.setUpdatedAt(LocalDateTime.now());

        // 插入需求记录
        requirementMapper.insert(requirement);

        // 处理技能关联
        if (requirementDTO.getSkills() != null && !requirementDTO.getSkills().isEmpty()) {
            for (String skillName : requirementDTO.getSkills()) {
                // 从 SkillMapper 获取技能 ID
                Long skillId = skillMapper.getSkillIdByName(skillName);
                if (skillId != null) {
                    // 插入到 RequirementSkillMapper 进行需求和技能的关联
                    requirementSkillMapper.insertRequirementSkill(requirement.getId(), skillId);
                } else {
                    // 如果技能在数据库中不存在，可以选择抛出异常或记录日志
                    throw new IllegalArgumentException("技能不存在: " + skillName);
                }
            }
        }
        // 清除相关缓存
        redisTemplate.delete(RECOMMENDED_DEMANDS_LIST_KEY + username);
        redisTemplate.delete(SKILL_STATISTICS);
    }

    @Override
    public void updateDemand(Long id, RequirementDTO requirementDTO, String username) {
        // 获取当前用户
        User user = userMapper.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 获取需求记录
        Requirement requirement = requirementMapper.selectById(id);
        if (requirement == null || !requirement.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("需求不存在或无权修改该需求");
        }

        // 更新需求信息
        requirement.setTitle(requirementDTO.getTitle());
        requirement.setDescription(requirementDTO.getDescription());
        requirement.setBudget(requirementDTO.getBudget());
        requirement.setStatus(requirementDTO.getStatus());
        requirement.setUpdatedAt(LocalDateTime.now());
        requirement.setSolution(requirementDTO.getSolution());
        requirementMapper.updateRequirement(requirement);

        // 处理技能关联
        requirementSkillMapper.deleteSkillsByRequirementId(requirement.getId());
        // 插入新的技能关联（如果存在技能列表）
        if (requirementDTO.getSkills() != null && !requirementDTO.getSkills().isEmpty()) {
            for (String skillName : requirementDTO.getSkills()) {
                // 获取技能 ID
                System.out.println("技能名字为:"+skillName);
                Long skillId = skillMapper.getSkillIdByName(skillName);
                if (skillId != null) {
                    // 插入新的需求-技能关联
                    requirementSkillMapper.insertRequirementSkill(requirement.getId(), skillId);
                } else {
                    // 如果技能不存在，可以选择抛出异常或忽略
                    throw new IllegalArgumentException("技能不存在: " + skillName);
                }
            }
        }
        String cacheKey = REQUIREMENT_DETAIL + id;
        redisTemplate.delete(cacheKey);
        redisTemplate.delete(SKILL_STATISTICS);
        redisTemplate.delete(RECOMMENDED_DEMANDS_LIST_KEY + username);

        RequirementDTO updatedRequirement = getRequirementDetail(id);
        redisTemplate.opsForValue().set(cacheKey, updatedRequirement, REDIS_EXPIRED_TIME, TimeUnit.MINUTES);
    }

    @Override
    public void deleteDemand(Long id, String username) {
        // 获取当前用户
        User user = userMapper.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 获取需求
        Requirement requirement = requirementMapper.selectById(id);
        if (requirement == null) {
            throw new IllegalArgumentException("需求不存在");
        }

        // 验证需求是否属于当前用户
        if (!requirement.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("无权限删除该需求");
        }


        // 删除需求与技能的关联
        requirementSkillMapper.deleteSkillsByRequirementId(id);

        // 删除需求
        requirementMapper.deleteById(id);
        redisTemplate.delete(REQUIREMENT_DETAIL + id);
        redisTemplate.delete(SKILL_STATISTICS);
        redisTemplate.delete(RECOMMENDED_DEMANDS_LIST_KEY + username);

    }
    @Override
   public boolean inviteCollaborate(Long demandId, String username, String inviteeUsername){
        try {
            // 获取需求名称
            Requirement requirement = requirementMapper.selectById(demandId);
            if (requirement == null) {
                throw new IllegalArgumentException("需求不存在，无法发送邀请");
            }
            String demandName = requirement.getTitle();

            User user = userMapper.getUserById(requirement.getUserId());
            System.out.println("当前需求的用户名为"+user.getUsername());

            if (!user.getUsername().equals(username)) {
                throw new IllegalArgumentException("当前需求不归属于你，不可以邀请该用户");
            }
            // 获取企业用户邮箱
            User companyUser = userMapper.getUserByUsername(username);
            if (companyUser == null || companyUser.getEmail() == null) {
                throw new IllegalArgumentException("企业用户信息无效，无法发送邀请");
            }
            String companyEmail = companyUser.getEmail();

            // 获取目标用户邮箱
            User targetUser = userMapper.getUserByUsername(inviteeUsername);
            if (targetUser == null || targetUser.getEmail() == null) {
                throw new IllegalArgumentException("被邀请用户信息无效，无法发送邀请");
            }
            String targetUserEmail = targetUser.getEmail();

            // 调用邮件服务发送邮件
            emailService.sendInviterEmail(targetUserEmail, companyEmail, username, demandName);

            return true;
        } catch (IllegalArgumentException e) {
            // 捕获参数无效的异常并打印日志
            System.err.println("参数异常：" + e.getMessage());
            throw e;
        } catch (Exception e) {
            // 捕获所有其他异常并打印日志
            System.err.println("发送协作邀请时发生错误：" + e.getMessage());
            throw new RuntimeException("发送协作邀请失败，请稍后重试", e); // 包装异常并抛出
        }
    }
    @Override
    public List<Map<String, Object>> getSkillStatistics() {
        String cacheKey = SKILL_STATISTICS;
        List<Map<String, Object>> cachedStatistics = (List<Map<String, Object>>) redisTemplate.opsForValue().get(cacheKey);

        if (cachedStatistics != null) {
            System.out.println("技能统计数据命中缓存");
            return cachedStatistics;
        }

        // 获取所有技能
        List<Skill> allSkills = skillMapper.getAllSkills();

        // 统计结果列表
        List<Map<String, Object>> statistics = new ArrayList<>();
        for (Skill skill : allSkills) {
            List<Long> requirementIds = requirementSkillMapper.getRequirementIdsBySkillId(skill.getId());
            Long completedCount = countRequirementsByIdsAndStatus(requirementIds, "COMPLETED");
            Long pendingCount = countRequirementsByIdsAndStatus(requirementIds, "PUBLISHED");

            Map<String, Object> skillStats = new HashMap<>();
            skillStats.put("skillName", skill.getName());
            skillStats.put("completedCount", completedCount);
            skillStats.put("pendingCount", pendingCount);
            statistics.add(skillStats);
        }

        // 写入缓存
        redisTemplate.opsForValue().set(cacheKey, statistics, REDIS_EXPIRED_TIME, TimeUnit.MINUTES);
        return statistics;
    }


    public Long countRequirementsByIdsAndStatus(List<Long> requirementIds, String status) {
        if (requirementIds == null || requirementIds.isEmpty()) {
            return 0L; // 如果需求列表为空，直接返回 0
        }

        // 构造 SQL 查询语句
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM requirements WHERE status = ? AND id IN (");
        for (int i = 0; i < requirementIds.size(); i++) {
            sql.append("?");
            if (i < requirementIds.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        // 将参数组装到列表中
        List<Object> params = new ArrayList<>();
        params.add(status); // 添加状态参数
        params.addAll(requirementIds); // 添加需求 ID 列表

        // 执行查询
        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Long.class);
    }
}



