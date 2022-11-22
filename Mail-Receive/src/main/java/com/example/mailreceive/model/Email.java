package com.example.mailreceive.model;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Email {

    private String mailSubject;  // mail konusu.
    private String mailFrom;     // mail şu kişiden geldi.
    private Date mailReceivedDate; // mail alma tarihi.
    private List<FileInfo> attachments; // mail ekleri.

}
