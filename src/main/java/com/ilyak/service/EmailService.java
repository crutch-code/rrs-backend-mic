package com.ilyak.service;


import com.google.api.services.gmail.model.Message;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;


import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.util.Properties;


@Singleton
public class EmailService {


    @Inject
    GmailService service;




    @SneakyThrows
    public void send(){


        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress("remote.rent.system@gmail.com", "Remote Rent System"));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress("boris. stupin01@gmail.com"));
        email.setSubject("title");
        email.setText("try txt");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message message = new Message() ;
        message.setRaw(encodedEmail);
        service.instance().users().messages().send("me", message).execute();
    }



    public static class MessageBuilder{


        public MessageBuilder() {

        }
    }

}
