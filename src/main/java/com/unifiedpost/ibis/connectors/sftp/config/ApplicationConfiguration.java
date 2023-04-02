package com.unifiedpost.ibis.connectors.sftp.config;

import com.unifiedpost.ibis.connectors.sftp.config.properties.ApplicationProperties;
import com.unifiedpost.ibis.connectors.sftp.config.properties.SftpServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Slf4j
@Configuration
@EnableConfigurationProperties({ApplicationProperties.class})
public class ApplicationConfiguration {

    @Bean
    public SftpServerProperties sftpProperties(ApplicationProperties applicationProperties) {
        log.info("Application Properties {}", applicationProperties);
        return applicationProperties.getSftp();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

}
