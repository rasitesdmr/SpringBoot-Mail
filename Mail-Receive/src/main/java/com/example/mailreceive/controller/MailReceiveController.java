package com.example.mailreceive.controller;

import com.example.mailreceive.model.Email;
import com.example.mailreceive.service.MailReceiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/mailReceive")
@RequiredArgsConstructor
public class MailReceiveController {

    final MailReceiveService mailReceiveService;

    @GetMapping("/allUnreadMail")
    public List<Email> getAllReceiveUnreadMails() throws MessagingException, IOException {
        return mailReceiveService.receiveUnreadMails();
    }

    @GetMapping("/sendUnreadMailsToQueue")
    public void getSendUnreadMailsToQueue() throws MessagingException, IOException {
        mailReceiveService.sendUnreadMailsToQueue();
    }

    @GetMapping("/allEmails")
    public List<Email> getAllEmails()throws MessagingException, IOException{
        return mailReceiveService.getAllMails();
    }

}
