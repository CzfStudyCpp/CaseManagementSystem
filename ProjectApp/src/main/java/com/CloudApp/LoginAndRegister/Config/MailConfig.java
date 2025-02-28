/**

 *
 * @author czf
 * @version 1.0
 * @最后修改时间： 2024-12-8
 * @修改说明，用于修改邮箱配置
 * @description:邮件发送
 */

package com.CloudApp.LoginAndRegister.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;
//bzawwxfmfrzebcag,邮箱授权


@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.qq.com");
        mailSender.setPort(587);
        mailSender.setUsername("2018249814@qq.com");
        mailSender.setPassword("bzawwxfmfrzebcag");

        // Additional custom settings
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        mailSender.setJavaMailProperties(properties);

        return mailSender;
    }
}
