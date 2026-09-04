package com.rrparedes.neurosilogic.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String remitente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía un correo de texto plano. Lanza la excepción del envío hacia arriba
     * (falla de SMTP, credenciales mal configuradas, etc.) para que el llamador decida
     * cómo informar el error — nunca se traga el fallo en silencio.
     */
    public void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        if (remitente != null && !remitente.isBlank()) {
            mensaje.setFrom(remitente);
        }
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        mailSender.send(mensaje);
    }
}
