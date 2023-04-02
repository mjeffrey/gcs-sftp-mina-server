package com.unifiedpost.ibis.connectors.sftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.sftp.server.FileHandle;
import org.apache.sshd.sftp.server.SftpEventListener;

/*
Simple example of adding a Listener, see SftpEventListener for docs.
 */
@Slf4j
public class LoggingSftpEventListener implements SftpEventListener {
    @Override
    public void read(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen, int readLen, Throwable thrown) {
        log.info("Read File '{}' offset {}, data.length {}, dataOffset {}, dataLen {}, readLen {}",
                localHandle.getFile(), offset, data.length, dataOffset, dataLen, readLen, thrown);
    }

    @Override
    public void writing(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen) {
        log.info("Write File '{}' offset {}, data.length {}, dataOffset {}, dataLen {}",
                localHandle.getFile(), offset, data.length, dataOffset, dataLen);
    }

}
