package com.CloudApp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
@MapperScan({
        "com.CloudApp.LoginAndRegister.Mapper", // 现有路径
        "com.CloudApp.SkillManagement.Mapper",
        "com.CloudApp.userServices.Mapper",
        "com.CloudApp.AnnouncementManagementService.Mapper",
        "com.CloudApp.RequirementService.Mapper"// 新增路径
})
//@ComponentScan(basePackages = {
//        "com.CloudApp.LoginAndRegister",
//        "com.CloudApp.LoginAndRegister.Request",
//        "com.CloudApp.LoginAndRegister.Config",
//        "com.CloudApp.LoginAndRegister.Service",
//        "com.CloudApp.LoginAndRegister.Utils",
//        "com.CloudApp.LoginAndRegister.Controller",
//        "com.CloudApp.RedisCommon.Config",
//        "com.CloudApp.RedisCommon.Service"})
//run application
public class CloudAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudAppApplication.class, args);
    }
}
