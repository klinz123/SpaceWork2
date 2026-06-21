package com.spacework.reservations.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        logger.info("Preparando envío de correo simulado/real a: {} con asunto: '{}'", to, subject);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("pruebas.spacework@gmail.com");

            mailSender.send(message);
            logger.info("Correo enviado exitosamente a {}", to);
        } catch (Exception e) {
            logger.warn("No se pudo enviar el correo real a {} (Es posible que los datos SMTP sean ficticios o de prueba). Detalles: {}", to, e.getMessage());
        }
    }
}
