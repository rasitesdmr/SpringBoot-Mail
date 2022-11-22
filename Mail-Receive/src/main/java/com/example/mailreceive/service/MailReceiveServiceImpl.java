package com.example.mailreceive.service;

import com.example.mailreceive.config.JmsConfiguration;
import com.example.mailreceive.config.MailConfiguration;
import com.example.mailreceive.model.Email;
import com.example.mailreceive.model.FileInfo;
import com.example.mailreceive.model.event.MailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MailReceiveServiceImpl implements MailReceiveService {

    final MailConfiguration mailConfiguration;

    final Environment environment;

    @Autowired
    JmsTemplate jmsTemplate;

    // TODO --->  List<Email> getAllMails() : Bütün mailleri getirir.

    @Override
    public List<Email> getAllMails() throws MessagingException, IOException {
        List<Email> emails = new ArrayList<>();  // email listesi oluşturduk
        Properties properties = mailConfiguration.getMailProperties();
        Store store = mailConfiguration.setSessionStoreProperties(properties);
        Folder folder = store.getFolder(environment.getProperty("mail.imap.box"));
        folder.open(Folder.READ_ONLY); // Oku ve yaz
        Message[] messages = folder.getMessages();
        for (int i = 0; i < messages.length; i++) {
            Message message = messages[i];
            String from = message.getFrom()[0].toString();
            String subject = message.getSubject();
            Date sentDate = message.getSentDate();
            List<FileInfo> attachments = new ArrayList<>();

            if (message.getContentType().contains("multipart")) {
                attachments = getAttachments(message);
            }
            emails.add(Email.builder()
                    //.messageCount(String.valueOf(i))
                    .mailFrom(from)
                    .mailSubject(subject)
                    .mailReceivedDate(sentDate)
                    .attachments(attachments)
                    .build());

        }
        folder.close(false);
        store.close();

        return emails;
    }

    // TODO --->  List<Email> receiveUnreadMails() : Okunmamış mailleri getirir.
    @Override
    public List<Email> receiveUnreadMails() throws MessagingException, IOException {
        List<Email> emails = new ArrayList<>();
        Properties properties = mailConfiguration.getMailProperties();
        Store store = mailConfiguration.setSessionStoreProperties(properties);
        Folder folder = store.getFolder(environment.getProperty("mail.imap.box"));
        folder.open(Folder.READ_ONLY);

        Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        Arrays.sort(messages, (m1, m2) -> {


            try {
                return m2.getSentDate().compareTo(m1.getSentDate());
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }

        });
        for (Message message : messages) {

            String from = message.getFrom()[0].toString();
            String subject = message.getSubject();
            Date sentDate = message.getSentDate();
            List<FileInfo> attachments = new ArrayList<>();

            if (message.getContentType().contains("multipart")) {
                attachments = getAttachments(message);
            }
            emails.add(Email.builder()

                    .mailFrom(from)
                    .mailSubject(subject)
                    .mailReceivedDate(sentDate)
                    .attachments(attachments)
                    .build());


        }
        folder.close(false);
        store.close();
        return emails;
    }

    // TODO --->  void sendUnreadMailsToQueue(): Okunmamış mailleri kuyruğa gönderir.

    @Override
    public void sendUnreadMailsToQueue() throws MessagingException, IOException {
        List<Email> emails = new ArrayList<>();
        Properties properties = mailConfiguration.getMailProperties();
        Store store = mailConfiguration.setSessionStoreProperties(properties);
        Folder folder = store.getFolder(environment.getProperty("mail.imap.box"));
        folder.open(Folder.READ_ONLY);

        Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        Arrays.sort(messages, (m1, m2) -> {


            try {
                return m2.getSentDate().compareTo(m1.getSentDate());
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }

        });
        for (Message message : messages) {

            String from = message.getFrom()[0].toString();
            String subject = message.getSubject();
            Date sentDate = message.getSentDate();
            List<FileInfo> attachments = new ArrayList<>();

            if (message.getContentType().contains("multipart")) {
                attachments = getAttachments(message);
            }
            emails.add(Email.builder()

                    .mailFrom(from)
                    .mailSubject(subject)
                    .mailReceivedDate(sentDate)
                    .attachments(attachments)
                    .build());

            emails.stream().forEach(emailQueue -> jmsTemplate.convertAndSend(JmsConfiguration.MAIL_RECEIVE_QUEUE, MailRequest.builder().email(emailQueue).build()));


        }
        folder.close(false);
        store.close();
    }


    private List<FileInfo> getAttachments(Message message) throws MessagingException, IOException {

        List<FileInfo> result = new ArrayList<>();

        Multipart multipart = (Multipart) message.getContent(); // Mesaj içeriğine eriş

        int numberOfPart = multipart.getCount(); // çoklu parçacık sayısı

        for (int partCount = 0; partCount < numberOfPart; partCount++) {
            MimeBodyPart mimeBodyPart = (MimeBodyPart) multipart.getBodyPart(partCount);
            if (Part.ATTACHMENT.equalsIgnoreCase(mimeBodyPart.getDisposition())) {
                byte[] data = mimeBodyPart.getInputStream().readAllBytes();
                FileInfo attachment = FileInfo.builder()
                        .name(MimeUtility.decodeText(mimeBodyPart.getFileName()))
                        .type(mimeBodyPart.getContentType().toLowerCase())
                        .size((long) data.length)
                        .data(data)
                        .build();
                result.add(attachment);
            }
        }
        return result;
    }
}