package com.you.and.me.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    // Dev Mail Sender
    @Bean(name = "devMailSender")
    public JavaMailSender devMailSender(
            @Value("${spring.mail.dev.host}") String host,
            @Value("${spring.mail.dev.port}") int port,
            @Value("${spring.mail.dev.username}") String username,
            @Value("${spring.mail.dev.password}") String password) {

        return createMailSender(host, port, username, password,
                "smtp.gmail.com", true, true);
    }

    // Main Mail Sender
    @Bean(name = "mainMailSender")
    public JavaMailSender mainMailSender(
            @Value("${spring.mail.main.host}") String host,
            @Value("${spring.mail.main.port}") int port,
            @Value("${spring.mail.main.username}") String username,
            @Value("${spring.mail.main.password}") String password) {

        return createMailSender(host, port, username, password,
                "smtp.gmail.com", true, true);
    }

    // Info Mail Sender
    @Bean(name = "infoMailSender")
    public JavaMailSender infoMailSender(
            @Value("${spring.mail.info.host}") String host,
            @Value("${spring.mail.info.port}") int port,
            @Value("${spring.mail.info.username}") String username,
            @Value("${spring.mail.info.password}") String password) {

        return createMailSender(host, port, username, password,
                "mail.privateemail.com", false, true);
    }

    private JavaMailSender createMailSender(String host, int port, String username,
                                            String password, String trustHost,
                                            boolean starttls, boolean ssl) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", starttls ? "true" : "false");
        props.put("mail.smtp.ssl.enable", ssl ? "true" : "false");
        props.put("mail.smtp.ssl.trust", trustHost);
        props.put("mail.debug", "false");

        return mailSender;
    }
}