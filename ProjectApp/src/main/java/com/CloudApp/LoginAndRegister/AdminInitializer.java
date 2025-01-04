package com.CloudApp.LoginAndRegister;

import com.CloudApp.LoginAndRegister.Service.AdminInitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final AdminInitializationService adminInitializationService;

    @Autowired
    public AdminInitializer(AdminInitializationService adminInitializationService) {
        this.adminInitializationService = adminInitializationService;
    }

    @Override
    public void run(String... args) throws Exception {
        adminInitializationService.initializeSuperAdmin();
    }
}
