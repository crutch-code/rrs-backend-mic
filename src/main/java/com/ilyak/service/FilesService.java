package com.ilyak.service;

import com.ilyak.entity.Files;
import com.ilyak.utills.security.MD5Util;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Singleton;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Singleton
public class FilesService {


    @Value("${micronaut.router.folder.dir-pattern}")
    protected String dirPattern;
    @Value("${micronaut.router.folder.files.post-photos}")
    protected String postPhotos;
    @Value("${micronaut.router.folder.files.avatars}")
    protected String avatars;
    @Value("${micronaut.router.folder.files.documents}")
    protected String documents;
    @Value("${micronaut.router.folder.files.secure-pictures}")
    protected String securePhotos;

    public Files save(CompletedFileUpload file, String destination) throws IOException, NoSuchAlgorithmException {
        Path path = Path.of(destination + uniqueName(file.getFilename()));
        java.nio.file.Files.copy(file.getInputStream(), path);
        return new Files(null, path.toUri().getPath(), Double.parseDouble(String.valueOf(file.getSize())), new Timestamp(System.currentTimeMillis()));
    }

    public String uniqueName(String old) throws NoSuchAlgorithmException {
        return MD5Util.getMD5(old + System.currentTimeMillis()) + old.substring(old.lastIndexOf("."));
    }

    @Async
    @EventListener
    public void initDirs(ApplicationStartupEvent e){
        Arrays.asList(
                new File(dirPattern),
                new File(dirPattern + avatars),
                new File(dirPattern + postPhotos),
                new File(dirPattern + documents),
                new File(dirPattern + securePhotos)
        ).forEach(File::mkdirs);
    }

}
