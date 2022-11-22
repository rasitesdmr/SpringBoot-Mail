package com.example.mailreceive.service;

import com.example.mailreceive.model.Email;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface MailReceiveService {

    List<Email> getAllMails() throws MessagingException, IOException;

    List<Email> receiveUnreadMails() throws MessagingException, IOException;

    void sendUnreadMailsToQueue() throws MessagingException, IOException;

}
