package com.example.mailreceive.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class FileInfo {

    private String name;  // dosya adı
    private String type; // dosya tipi
    private Long size; // dosya boyutu
    private byte[] data;

}
