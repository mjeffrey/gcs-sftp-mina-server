package com.unifiedpost.ibis.connectors.sftp.config.properties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;


@ConfigurationProperties(prefix = "application")
@AllArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Validated
@ToString
public
class ApplicationProperties {

    @NestedConfigurationProperty
    SftpServerProperties sftp;

}



