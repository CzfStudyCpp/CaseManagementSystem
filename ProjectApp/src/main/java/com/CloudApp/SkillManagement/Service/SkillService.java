package com.CloudApp.SkillManagement.Service;

import com.CloudApp.SkillManagement.Entity.Skill;
import com.CloudApp.SkillManagement.Mapper.SkillMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SkillService {

    private final SkillMapper skillMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_SKILL_LIST_KEY = "skills:list"; // 技能标签列表缓存键
    private static final String REDIS_SKILL_KEY_PREFIX = "skills:";   // 单个技能缓存键前缀
    private static final long CACHE_EXPIRATION = 10;                 // 缓存过期时间（单位：分钟）


    @Autowired
    public SkillService(SkillMapper skillMapper, RedisTemplate<String, Object> redisTemplate) {
        this.skillMapper = skillMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取所有技能标签
     */
    public List<Skill> getAllSkills() {
        // 从 Redis 缓存中获取技能标签列表
        List<Skill> cachedSkills = getFromRedis(REDIS_SKILL_LIST_KEY, List.class);
        if (cachedSkills != null && !cachedSkills.isEmpty()) {
            System.out.println("从 Redis 缓存中获取的技能标签列表：" + cachedSkills);
            return cachedSkills; // 如果缓存存在且不为空，直接返回
        }

        System.out.println("Redis 缓存为空或不存在，从数据库中获取技能标签列表");

        // 如果缓存不存在或为空，从数据库获取技能标签
        List<Skill> skills = skillMapper.findAllSkills();
        System.out.println("从数据库中获取的技能标签列表：" + skills);

        // 如果数据库中有数据，将技能标签列表存入 Redis 缓存
        if (skills != null && !skills.isEmpty()) {
            redisTemplate.opsForValue().set(REDIS_SKILL_LIST_KEY, skills, CACHE_EXPIRATION, TimeUnit.MINUTES);
        } else {
            System.out.println("数据库中技能标签列表为空，不存入 Redis");
        }

        return skills;
    }

    @SuppressWarnings("unchecked")
    private <T> T getFromRedis(String key, Class<T> type) {
        Object data = redisTemplate.opsForValue().get(key);
        if (data != null && type.isInstance(data)) {
            return (T) data;
        }
        return null;
    }
    /**
     * 添加新技能标签
     */
    public Skill addSkill(Skill skill) {
        // 检查是否已存在
        if (skillMapper.existsByName(skill.getName())) {
            throw new IllegalArgumentException("技能标签已存在: " + skill.getName());
        }

        // 保存到数据库
        skillMapper.insert(skill);

        // 更新 Redis 缓存
        refreshAllSkillsCache();

        return skill;
    }

    /**
     * 删除技能标签
     */
    public void deleteSkill(Long id) {
        // 检查是否存在
        Skill skill = skillMapper.selectById(id);
        if (skill == null) {
            throw new IllegalArgumentException("技能标签不存在，ID: " + id);
        }

        // 从数据库删除
        skillMapper.deleteById(id);

        // 更新 Redis 缓存
        refreshAllSkillsCache();
    }

    /**
     * 更新技能标签
     */
    public Skill updateSkill(Long id, Skill skill) {
        Skill existingSkill = skillMapper.selectById(id);
        if (existingSkill == null) {
            throw new IllegalArgumentException("技能标签不存在，ID: " + id);
        }

        // 更新字段
        existingSkill.setName(skill.getName());
        existingSkill.setDescription(skill.getDescription());
        existingSkill.setCategory(skill.getCategory());
        skillMapper.updateById(existingSkill);

        // 更新 Redis 缓存
        refreshAllSkillsCache();

        return existingSkill;
    }

    /**
     * 刷新所有技能标签的缓存
     */
    private void refreshAllSkillsCache() {
        List<Skill> skills = skillMapper.findAllSkills();
        redisTemplate.opsForValue().set(REDIS_SKILL_LIST_KEY, skills, CACHE_EXPIRATION, TimeUnit.MINUTES);
    }

    public void refresh(){
        refreshAllSkillsCache();
    }
}
