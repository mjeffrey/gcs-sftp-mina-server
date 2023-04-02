package com.unifiedpost.ibis.connectors.sftp;

import org.apache.sshd.sftp.server.FileHandle;
import org.apache.sshd.sftp.server.SftpFileSystemAccessor;
import org.apache.sshd.sftp.server.SftpSubsystemProxy;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Set;

public class GcsSftpFileSystemAccessor implements SftpFileSystemAccessor {
    @Override
    public void closeFile(SftpSubsystemProxy subsystem, FileHandle fileHandle, Path file, String handle, Channel channel, Set<? extends OpenOption> options) throws IOException {
        // do nothing, not needed and an error for GCS.
    }
}
