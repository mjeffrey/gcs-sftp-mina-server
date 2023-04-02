package com.unifiedpost.ibis.connectors.sftp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GcsSftpServer {

    public static void main(String[] args) {
        SpringApplication.run(GcsSftpServer.class, args);
    }
}
