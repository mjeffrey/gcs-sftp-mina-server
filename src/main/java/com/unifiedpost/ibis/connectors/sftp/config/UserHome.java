package com.unifiedpost.ibis.connectors.sftp.config;

import lombok.Builder;
import lombok.Value;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class UserHome {

    String username;
    Path homePath;

    public static List<UserHome> initialize(Path rootPath, List<String> users) {
        List<UserHome> userHomes = new ArrayList<>();
        users.forEach(user -> userHomes.add(asUserHome(rootPath.resolve("home"), user)));
        return userHomes;
    }

    private static UserHome asUserHome(Path homePath, String user) {
        Path userHomePath = homePath.resolve(user);
        return UserHome.builder().homePath(userHomePath).username(user).build();
    }
}
