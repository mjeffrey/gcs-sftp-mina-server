package com.unifiedpost.ibis.connectors.sftp;

import com.unifiedpost.ibis.connectors.sftp.config.UserHome;
import com.unifiedpost.ibis.connectors.sftp.config.properties.SftpServerProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.config.keys.AuthorizedKeysAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmbeddedSftpServer implements InitializingBean {

    private final SshServer server = SshServer.setUpDefaultServer();
    private Path rootPath;
    private SftpServerProperties sftpServerProperties;

    public EmbeddedSftpServer(SftpServerProperties sftpServerProperties) {
        this.sftpServerProperties = sftpServerProperties;
    }

    private static List<SftpSubsystemFactory> createSubsystemFactories() {
        SftpSubsystemFactory subsystemFactory = new SftpSubsystemFactory.Builder()
                .withFileSystemAccessor(new GcsSftpFileSystemAccessor()) // probably not needed see "sftp-auto-fsync-on-close"
                .build();
        subsystemFactory.addSftpEventListener(new LoggingSftpEventListener());
        return Collections.singletonList(subsystemFactory);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rootPath = Paths.get(sftpServerProperties.getRootGsUri());
        log.info("Root directory {}", rootPath.toUri());

        server.setPort(sftpServerProperties.getPort());
        server.setKeyPairProvider(createServerHostKeyPairProvider(sftpServerProperties));
        server.setSubsystemFactories(createSubsystemFactories());

        List<UserHome> userHomes = UserHome.initialize(rootPath, sftpServerProperties.getUsers());

        server.setPublickeyAuthenticator(configurePublicKeyAuthentication(sftpServerProperties, userHomes));
        server.setFileSystemFactory(creatVirtualFileSystemFactory(userHomes));
        start();
    }

    private VirtualFileSystemFactory creatVirtualFileSystemFactory(List<UserHome> userHomes) {
        VirtualFileSystemFactory virtualFileSystemFactory = new VirtualFileSystemFactory(rootPath);
        userHomes.forEach(userHome -> addUser(virtualFileSystemFactory, userHome));
        return virtualFileSystemFactory;
    }

    private SimpleGeneratorHostKeyProvider createServerHostKeyPairProvider(SftpServerProperties sftpServerProperties) {
        // Don't do this!
        // Wrap a password protected key from a secure mechanism (like secrets manager) and use your own KeyProvider
        Path hostKeyPath = writeResource("hostkey.ser", sftpServerProperties.getHostKey());
        SimpleGeneratorHostKeyProvider keyPairProvider = new SimpleGeneratorHostKeyProvider(hostKeyPath);
        log.info("Host key file {}", hostKeyPath);
        return keyPairProvider;
    }

    @SneakyThrows
    private AuthorizedKeysAuthenticator configurePublicKeyAuthentication(SftpServerProperties sftpServerProperties, List<UserHome> userHomes) {
        Path authorizedKeys = writeResource("authorized_keys.local", sftpServerProperties.getAuthorizedKeys());
        Set<String> users = userHomes.stream().map(UserHome::getUsername).collect(Collectors.toSet());
        return new UserAuthentication(authorizedKeys, users);
    }

    @SneakyThrows
    private Path writeResource(String filename, Resource resource) {
        Path resourcePath = rootPath.resolve(filename);
        Files.write(resourcePath, resource.getContentAsByteArray());
        return resourcePath;
    }

    private void addUser(VirtualFileSystemFactory fileSystemFactory, UserHome userHome) {
        Path homePath = userHome.getHomePath();
        fileSystemFactory.setUserHomeDir(userHome.getUsername(), homePath);
        createSubDirectory(homePath, "upload");
        createSubDirectory(homePath, "download");
        log.info("Home directory for user: {}  {}", userHome.getUsername(), homePath);
    }

    @SneakyThrows
    private void createSubDirectory(Path homePath, String subdirectory) {
        Path placeHolderFile = homePath.resolve(subdirectory).resolve(".ignored");
        Files.writeString(placeHolderFile, "placeholder so directory exists");
    }


    public void start() {
        try {
            this.server.start();
            log.info("Starting on port {}", this.server.getPort());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
