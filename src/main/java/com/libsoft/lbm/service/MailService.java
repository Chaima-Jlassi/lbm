package com.libsoft.lbm.service;

import com.libsoft.lbm.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final String adminEmail;
    private final String from;

    public MailService(JavaMailSender mailSender,
                       @Value("${app.mail.admin}") String adminEmail,
                       @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.adminEmail = adminEmail;
        this.from = from;
    }

    /** Notifie l'admin qu'un nouvel utilisateur s'est inscrit. Un échec SMTP ne bloque jamais l'inscription. */
    @Async
    public void notifyAdminNewRegistration(User u) {
        try {
            SimpleMailMessage m = new SimpleMailMessage();
            m.setFrom(from);
            m.setTo(adminEmail);
            m.setSubject("[Library Manager] Nouvelle inscription");
            m.setText("Un nouvel utilisateur vient de s'inscrire :\n\n"
                    + "Nom : " + u.getNom() + " " + u.getPrenom() + "\n"
                    + "Email : " + u.getEmail() + "\n"
                    + "Tél : " + (u.getTel() == null ? "-" : u.getTel()) + "\n\n"
                    + "Rôle attribué par défaut : BIBLIOTHECAIRE. Vous pouvez le modifier depuis la page Utilisateurs.");
            mailSender.send(m);
        } catch (Exception e) {
            log.warn("Envoi de l'email à l'admin impossible (SMTP non configuré ?) : {}", e.getMessage());
        }
    }
}
