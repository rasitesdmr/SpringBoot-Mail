package com.example.mailreceive.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MailConfiguration {

    final Environment environment;


    public Properties getMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.imap.host", environment.getProperty("mail.imap.host"));
        properties.put("mail.imap.port", environment.getProperty("mail.imap.port"));
        properties.put("mail.imap.socketFactory.class", environment.getProperty("mail.imap.socketFactory.class"));
        properties.put("mail.imap.socketFactory.fallback", environment.getProperty("mail.imap.socketFactory.fallback"));
        properties.put("mail.imap.socketFactory.port", (environment.getProperty("mail.imap.port")));
        return properties;
    }


    //@SneakyThrows
    public Store setSessionStoreProperties(Properties properties) throws MessagingException {

        Session session = Session.getDefaultInstance(properties);

        Store store = session.getStore(environment.getProperty("mail.store.protocol"));
        store.connect(environment.getProperty("mail.imap.userName"), environment.getProperty("mail.imap.password"));

        return store;
    }

}
