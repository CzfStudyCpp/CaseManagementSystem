package com.CloudApp.LoginAndRegister.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 生成6位数字验证码
     * @return 随机验证码
     */
    public String generateVerificationCode() {
        SecureRandom secureRandom = new SecureRandom();
        int code = secureRandom.nextInt(900000) + 100000; // 生成6位随机验证码
        return String.valueOf(code);
    }

    /**
     * 发送验证码邮件
     * @param to 目标邮箱
     * @param code 验证码
     */
    public void sendVerificationEmail(String to, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("2018249814@qq.com"); // 发件人地址
            message.setTo(to);
            message.setSubject("验证码 - 注册验证");
            message.setText(generateEmailContent(code));
            mailSender.send(message);
        } catch (Exception e) {
            // 捕获邮件发送异常并抛出
            throw new RuntimeException("邮件发送失败:网络异常或SMTP服务不可达 " + e.getMessage());
        }
    }


    /**
     * 生成验证码邮件内容
     * @param code 验证码
     * @return 邮件内容
     */
    private String generateEmailContent(String code) {
        return "尊敬的用户，您好：\n\n" +
                "您的验证码是: " + code + "\n" +
                "请在 3 分钟内完成验证。\n\n" +
                "如果您未申请此操作，请忽略此邮件。\n\n" +
                "感谢您对云协作案例平台的支持！";
    }

    public void sendPasswordVerificationEmail(String to, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("2018249814@qq.com"); // 发件人地址
            message.setTo(to);
            message.setSubject("验证码 - 修改密码");
            message.setText(generatePasswordEmailContent(code));
            mailSender.send(message);
        } catch (Exception e) {
            // 捕获邮件发送异常并抛出
            throw new RuntimeException("邮件发送失败:网络异常或SMTP服务不可达 " + e.getMessage());
        }
    }

    private String generatePasswordEmailContent(String code) {
        return "尊敬的用户，您好：\n\n" +
                "您正在申请修改密码\n\n"+
                "您的验证码是: " + code + "\n" +
                "请在 3 分钟内完成验证。\n\n" +
                "如果您未申请此操作，请忽略此邮件。\n\n" +
                "感谢您对云协作案例平台的支持！";
    }
    /**
     * 发送审核结果邮件
     * @param to 收件人邮箱
     * @param result 审核结果（如：审核通过、审核未通过）
     */
    public void sendApprovalResultEmail(String to, String result) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("2018249814@qq.com"); // 发件人地址
            message.setTo(to);
            message.setSubject("审核结果通知");
            message.setText(generateApprovalEmailContent(result));
            mailSender.send(message);
        } catch (Exception e) {
            // 捕获邮件发送异常并抛出
            throw new RuntimeException("邮件发送失败: 网络异常或SMTP服务不可达" + e.getMessage());
        }
    }

    /**
     * 生成审核结果邮件内容
     * @param result 审核结果
     * @return 邮件内容
     */
    private String generateApprovalEmailContent(String result) {
        return "尊敬的用户，您好：\n\n" +
                "您的账号审核结果为: " + result + "\n" +
                "如果您有任何疑问，请联系我们的支持团队。\n\n" +
                "感谢您对云协作案例平台的支持！";
    }

    private String generateApprovalChangeEmailEmailContent(String result) {
        return "尊敬的用户，您好：\n\n" +
                "您申请邮箱更换的审核结果为: " + result + "\n" +
                "如果您有任何疑问，请联系我们的支持团队。\n\n" +
                "感谢您对云协作案例平台的支持！";
    }

    public void sendApprovalEmailResult(String email, String result) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("2018249814@qq.com"); // 发件人地址
            message.setTo(email);
            message.setSubject("邮箱变更审核结果通知");
            message.setText(generateApprovalChangeEmailEmailContent(result));
            mailSender.send(message);
        } catch (Exception e) {
            // 捕获邮件发送异常并抛出
            throw new RuntimeException("邮件发送失败: 网络异常或SMTP服务不可达" + e.getMessage());
        }
    }

    public void sendInviterEmail(String to, String CompanyEmail,String username,String demand ) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("2018249814@qq.com"); // 发件人地址
            message.setTo(to);
            message.setSubject("邀请邮件");
            message.setText(generateInviteEmail(CompanyEmail,username,demand));
            mailSender.send(message);
        } catch (Exception e) {
            // 捕获邮件发送异常并抛出
            throw new RuntimeException("邮件发送失败:网络异常或SMTP服务不可达 " + e.getMessage());
        }
    }
    private String generateInviteEmail(String email,String userName, String demand) {
        return "尊敬的用户，您好：\n\n" +
                "用户" + userName + "\n" +
                "邀请您参与需求"+demand+"的协作"+"\n"+
                "邀请用户邮箱为"+email+"\n\n"+
                "如果您有任何疑问，请联系我们的支持团队。\n\n" +
                "感谢您对云协作案例平台的支持！";
    }
}
