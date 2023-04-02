# SFTP Mina Server backed by Google Cloud Storage Bucket

# Introduction

Mina is an implementation of SSH and one of its components is an SFTP server (and Client).

SFTP is a good choice for reasonably secure communications and the Apache Mina Server is used by banks for integrations.
There are a number of ways to authenticate (like passwords), but public/private key is a better choice.

This example is extracted from a server I used for testing and integration, so it does contain some unusual things like
multiple users with a single public key.
The normal approach is a separate key per user. This stack overflow entry purports to do this, but I have not validated
it.
https://stackoverflow.com/questions/39122927/apache-mina-sshd-authenticate-client-signatures

# Google Cloud Storage (limitations)

The Mina SFTP Server is backed by a (java.nio) FileSystem and so you can plug in any compatible filesystem rather than
the default local Filesystem.
You could use an in-memory FileSystem like (Google JimFs) but since Google also provides a FileSystem adapter for Google
Cloud Storage you can also use that.
GCS by its nature does have some limitations and quirks and the code works around some of these.
For example, directories don't really exist but the object path with "/" separators is used to simulate a directory
structure.
for example "gs://mybucket/root/home/user1/file.txt" is considered as: file.txt in the directory: /root/home/user1

See https://github.com/googleapis/java-storage-nio especially for the limitations.

Since an empty directory is not usable, so we put a dummy hidden file into each directory that we want the client to
see.
Also channel close is not supported and gives errors on upload. To avoid with this, the SftpFileSystemAccessor overrides
closeFile. (apparently the property "sftp-auto-fsync-on-close" can also be set but dd not check how).
The files size and attributes are not provided. If needed it would probably be possible to do this with the
SftpFileSystemAccessor.

##

Recommendations:

- Consider using GCS Notifications on OBJECT_FINALIZE https://cloud.google.com/storage/docs/pubsub-notifications to
  process uploaded files.
- Protect the server host key. If this is leaked someone can impersonate this host. (do not do it like I have here, it
  should be password protected, come from a secure location and should not be stored)
- Provide the public key of the server host key to your clients for their "known_hosts" file. Do not let them "get it on
  first use". This mitigates soem MITM scenarios.
- Check all uploaded files as much as possible
  see: https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html.
- Use a VirtualFileSystemFactory to keep clients in their own area.
- Sign and Encrypt sensitive files. This not only protects the privacy of the files but also prevents modification and
  assures the files came from the sender.
- Don't expose the server directly publicly, protect it from threats with suitable infrastructure. 
