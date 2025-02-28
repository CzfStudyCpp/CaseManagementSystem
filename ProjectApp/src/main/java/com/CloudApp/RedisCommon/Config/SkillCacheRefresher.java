package com.CloudApp.RedisCommon.Config;
import com.CloudApp.SkillManagement.Service.SkillService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SkillCacheRefresher {

    private final SkillService skillService;

    public SkillCacheRefresher(SkillService skillService) {
        this.skillService = skillService;
    }

    // 每隔10分钟刷新缓存
    @Scheduled(fixedRate =  10 * 60 * 1000)
    public void refreshSkillCache() {
        skillService.refresh(); // 调用缓存刷新逻辑
        System.out.println("技能标签缓存已刷新");
    }
}
