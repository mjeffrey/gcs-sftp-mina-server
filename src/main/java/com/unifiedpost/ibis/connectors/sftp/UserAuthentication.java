package com.unifiedpost.ibis.connectors.sftp;

import org.apache.sshd.server.config.keys.AuthorizedKeysAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.nio.file.Path;
import java.util.Set;

class UserAuthentication extends AuthorizedKeysAuthenticator {
    private Set<String> users;

    public UserAuthentication(Path file, Set<String> users) {
        super(file);
        this.users = users;
    }

    @Override
    protected boolean isValidUsername(String username, ServerSession session) {
        return users.contains(username) && super.isValidUsername(username, session);
    }
}
