//package com.CloudApp.LoginAndRegister.Controller;
//
//import com.CloudApp.LoginAndRegister.Request.UserRegistrationRequest;
//import com.CloudApp.LoginAndRegister.Service.UserService;
//import com.CloudApp.LoginAndRegister.Service.RegistrationService;
//import com.CloudApp.LoginAndRegister.Service.VerificationService;
//
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@RestController
//@RequestMapping("/register")
//public class UserRegistrationController {
//
//    private final RegistrationService registrationService;
//    private final VerificationService verificationService;
//    private final UserService userService;
//    //private static final Logger logger = LoggerFactory.getLogger(UserRegistrationController.class);
//
//    @Autowired
//    public UserRegistrationController(RegistrationService registrationService, VerificationService verificationService, UserService userService) {
//        this.registrationService = registrationService;
//        this.verificationService = verificationService;
//        this.userService = userService;
//    }
//
//    @GetMapping("/check-email")
//    public ResponseEntity<?>checkEmail(@RequestParam String email) {
//        try {
//            boolean isRegistered = userService.isEmailRegistered(email);
//            if (isRegistered) {
//                return ResponseEntity.badRequest().body("该邮箱已被注册，请更换邮箱");
//            }
//            return ResponseEntity.ok("邮箱可用");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("服务器内部错误: " + e.getMessage());
//        }
//    }
//    // 用户注册时，发送验证码
//    @PostMapping("/sendVerificationCode")
//    public ResponseEntity<?> sendVerificationCode(@RequestParam String email) {
//        // 删除try-catch，异常直接抛出，交给全局异常处理类处理
//        registrationService.sendVerificationCode(email);
//        return ResponseEntity.ok("验证码已发送至邮箱");
//    }
//
//    // 用户注册时，验证验证码
//    @PostMapping("/verifyCode")
//    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code) {
//        // 删除try-catch，异常直接抛出，交给全局异常处理类处理
//        boolean isValid = verificationService.verifyCode(email, code);
//        if (isValid) {
//            return ResponseEntity.ok("验证码验证成功");
//        } else {
//            throw new IllegalArgumentException("验证码无效或已过期");
//        }
//    }
//
//    @PostMapping("/submit")
//    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegistrationRequest request) {
//        // 删除try-catch，异常直接抛出，交给全局异常处理类处理
//        if (!verificationService.isVerified(request.getEmail())) {
//            throw new IllegalArgumentException("请先验证邮箱验证码");
//        }
//
//        // 调用业务类的注册方法，将所有逻辑集中到服务层处理
//        userService.registerPendingUser(request);
//
//        return ResponseEntity.ok("注册信息已提交，请等待管理员审核");
//    }
//}
//
package com.CloudApp.LoginAndRegister.Controller;

import com.CloudApp.GlobalException.BusinessException;
import com.CloudApp.GlobalException.ErrorCode;
import com.CloudApp.LoginAndRegister.Request.UserRegistrationRequest;
import com.CloudApp.LoginAndRegister.Request.VerificationRequest;
import com.CloudApp.LoginAndRegister.Service.RegistrationService;
import com.CloudApp.LoginAndRegister.Service.UserService;
import com.CloudApp.LoginAndRegister.Service.FileStorageService;
import com.CloudApp.LoginAndRegister.Service.VerificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/register")
public class UserRegistrationController {

    private final VerificationService verificationService;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final RegistrationService registrationService;

    @Autowired
    public UserRegistrationController(VerificationService verificationService, UserService userService, FileStorageService fileStorageService, RegistrationService registrationService) {
        this.verificationService = verificationService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
        this.registrationService = registrationService;
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        System.out.println("检查的邮箱为"+email);
        if (userService.isEmailRegistered(email)) {
            // 抛出 BusinessException，附带错误码
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }
        return ResponseEntity.ok("邮箱可用");
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        System.out.println("检查的用户名为：" + username);
        if (userService.isUsernameRegistered(username)) {
            // 抛出 BusinessException，附带错误码
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_REGISTERED);
        }
        return ResponseEntity.ok("用户名可用");
    }


    @PostMapping("/sendVerificationCode")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        registrationService.sendVerificationCode(email);
        return ResponseEntity.ok("验证码已发送至邮箱");
    }


    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody @Valid VerificationRequest request) {
        boolean isValid = verificationService.verifyCode(request.getEmail(), request.getCode());
        if (!isValid) {
            // 抛出验证码无效的异常
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_INVALID);
        }
        verificationService.setVerified(request.getEmail());
        return ResponseEntity.ok("验证码验证成功");
    }

    @PostMapping("/submit")
    public ResponseEntity<?> registerUser(
            @ModelAttribute @Valid UserRegistrationRequest request,
            @RequestParam(value = "businessLicense", required = false) MultipartFile businessLicense,
            @RequestParam("verificationCode") String verificationCode) throws Exception {

        // 验证验证码是否通过
        if (!verificationService.isVerified(request.getEmail())) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_MISSING);
        }

        if ("developer".equals(request.getUserType())) {
            if (request.getDeveloperInfo() == null || request.getDeveloperInfo().getRealName() == null) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "开发者信息缺失");
            }
            System.out.println("开发者真实姓名: " + request.getDeveloperInfo().getRealName());
        }

//        System.out.println("接收到的用户名: " + request.getUsername());
//        System.out.println("接收到的邮箱: " + request.getEmail());
//        System.out.println("接收到的电话: " + request.getPhone());
//        System.out.println("接收到的账户类型: " + request.getUserType());
//        System.out.println("接收到的真实姓名: " + request.getDeveloperInfo().getRealName());
//        System.out.println("接收到的GitHub链接: " + request.getDeveloperInfo().getGithub());
//        System.out.println("接收到的作品集链接: " + request.getDeveloperInfo().getPortfolio());
//        System.out.println("接收到的项目经验: " + request.getDeveloperInfo().getExperience());
//        System.out.println("接收到的验证码: " + verificationCode);
//        System.out.println("准备执行注册业务>>>>>eof<<<<<<<<");

        // 保存营业执照文件（仅企业用户上传）
        if ("company".equals(request.getUserType())) {
            if (businessLicense == null || businessLicense.isEmpty()) {
                throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
            }
            String filePath = fileStorageService.saveFile(businessLicense);
            if (request.getCompanyInfo() == null) {
                throw new BusinessException(ErrorCode.COMPANY_INFO_MISSING);
            }
            System.out.println("文件保存路径: " + filePath);
            request.getCompanyInfo().setBusinessLicense(filePath);
        }

        // 调用业务类处理注册逻辑
        userService.registerPendingUser(request);

        return ResponseEntity.ok("注册信息已提交，请等待管理员审核");
    }
}

