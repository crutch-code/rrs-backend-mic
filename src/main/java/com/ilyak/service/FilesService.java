package com.ilyak.service;

import com.ilyak.entity.jpa.Files;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.repository.FilesRepository;
import com.ilyak.repository.PostRepository;
import com.ilyak.repository.TransactionalRepository;
import com.ilyak.repository.UserRepository;
import com.ilyak.utills.security.MD5Util;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

@Singleton
public class FilesService {

    @Inject
    TransactionalRepository transactionalRepository;
    @Inject
    ResponseService responseService;

    @Inject
    FilesRepository filesRepository;

    @Inject
    PostRepository postRepository;

    @Inject
    UserRepository userRepository;


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
        return new Files(
                transactionalRepository.genOid().orElseThrow(),
                path.toString().replace("\\", "/"),
                Long.parseLong(String.valueOf(file.getSize())),
                LocalDateTime.now(ZoneId.systemDefault())
        );
    }

    public String uniqueName(String old) throws NoSuchAlgorithmException {
        return MD5Util.getMD5(old + System.currentTimeMillis()) + old.substring(old.lastIndexOf("."));
    }

    public String uniqueName(String old, String format) throws NoSuchAlgorithmException {
        return MD5Util.getMD5(old + System.currentTimeMillis()) + format;
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

    @SneakyThrows
    public void delete(Files target, String type){
        String targetTable;
        switch (type){
            case "post_photos" -> targetTable = "post_photos";
            case "avatar"-> targetTable = "users_avatar_file";
            default -> throw new InternalExceptionResponse("Неправильный тип запроса на удаление", responseService.error("Неправильный тип запроса на удаление") );
        }
        transactionalRepository.deleteFile(target.getOid(), null, targetTable);
        java.nio.file.Files.deleteIfExists(Path.of(target.getFilePath()));
    }

    public String getDirPattern() {
        return dirPattern;
    }

    public String getPostPhotos() {
        return postPhotos;
    }

    public String getAvatars() {
        return avatars;
    }

    public String getDocuments() {
        return documents;
    }

    public String getSecurePhotos() {
        return securePhotos;
    }
}
