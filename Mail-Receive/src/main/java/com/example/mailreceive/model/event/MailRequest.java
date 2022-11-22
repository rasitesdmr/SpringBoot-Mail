package com.example.mailreceive.model.event;

import com.example.mailreceive.model.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MailRequest {

    private Email email;

}
