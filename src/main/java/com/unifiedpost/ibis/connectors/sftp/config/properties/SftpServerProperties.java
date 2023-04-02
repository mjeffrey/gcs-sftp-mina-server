package com.unifiedpost.ibis.connectors.sftp.config.properties;

import lombok.ToString;
import lombok.Value;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Value
@ToString
public class SftpServerProperties {


    @NonNull
    int port;

    @NonNull
    URI rootGsUri;

    @NonNull
    Resource hostKey;

    @NonNull
    Set<String> directories;

    @NonNull
    Resource authorizedKeys;

    List<String> users;

}
