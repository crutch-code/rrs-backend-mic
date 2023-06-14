package com.ilyak.controller;

import com.ilyak.entity.jpa.Files;
import com.ilyak.entity.jpa.User;
import com.ilyak.entity.jpa.UsersAvatarFile;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.repository.UserAvatarFileRepository;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.http.server.types.files.SystemFile;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Controller("/api/files")
@Tag(name = "Контроллер файлов приложения",
        description = "Данный котроллер отвечает за получение, добавление, и изменение файлов"
)
@Secured(SecuredAnnotationRule.IS_ANONYMOUS)
@Validated
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "jwt")
public class FileController extends BaseController{

    public static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Inject
    UserAvatarFileRepository userAvatarFileRepository;

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Добавить аватар для пользователя")
    @Patch(uri = "/avatar/add", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<DefaultAppResponse> addAvatar(@Part CompletedFileUpload avatars){
        try{
            User user = getCurrentUser();
            logger.info("Try change avatar");Files target = filesService.save(
                    avatars,
                    filesService.getDirPattern() + filesService.getAvatars()
            );
            filesRepository.save(target);
            user.getAvatars().forEach(it -> filesService.delete(it, "avatar"));
            userAvatarFileRepository.save(
                    new UsersAvatarFile(
                            transactionalRepository.genOid().orElseThrow(),
                            user,
                            target
                    )
            );

            return HttpResponse.ok(responseService.success("avatars uploaded successfully"));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }



    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Обновление фотографий поста, может использоваться и для удаления фотографий")
    @Patch(uri = "/post/patch{?post_oid}", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<DefaultAppResponse> updPostPhotos(
            @Part @Nullable Publisher<CompletedFileUpload> photos,
            @Part @Nullable List<String> delete_oids,
            @QueryValue Optional<String> post_oid
    ){
        try{
            if(delete_oids != null && !delete_oids.isEmpty()){
                delete_oids.forEach(it -> filesRepository.findById(it).ifPresent(files -> filesService.delete(files,"post_photos" )));
            }
            AtomicInteger cnt = new AtomicInteger();
            com.ilyak.entity.jpa.Post post = postRepository.findById(
                    post_oid.orElseThrow(() -> new RuntimeException("Пустое значение идентификатора поста"))
            ).orElseThrow(() -> new RuntimeException("Пост с таким идентификатором не найден"));
            Flowable.fromPublisher(photos).subscribe(
                    emitted -> {
                        Files target = filesService.save(emitted, filesService.getDirPattern() + filesService.getPostPhotos());
                        filesRepository.save(target);
                        post.getPostPhotos().add(target);
                        cnt.getAndIncrement();
                    }
            );
            postRepository.update(post);
            return HttpResponse.ok(
                    responseService.success(
                            "манипуляция с фотограффиями поста (добавления/удаления) осуществлена. "
                            + (delete_oids == null ? "" : "Удалено: " + delete_oids.size())
                            + " Добавлено: " + cnt.get()
                    )
            );
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Получение файлов")
    @Get(uri = "/get/oid", produces = MediaType.MULTIPART_FORM_DATA)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public StreamedFile getAvatarByOid(@QueryValue(value = "file_oid") @Parameter Optional<String> oid){
        try{
            Files metha = filesRepository.findById(oid.orElseThrow()).orElseThrow();
            File target = new File(metha.getFilePath());


            if(!target.exists())
                throw new RuntimeException("The file does not exist, but there is a record of it: " + oid.get());

            return new StreamedFile(
                    new FileInputStream(target),
                    mediaTypeFileResolver(target.getName())
            ).attach(target.getName());

        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error("Failed get avatar: " +ex.getMessage()));
        }
    }

//    @ExecuteOn(TaskExecutors.IO)
//    @Operation(summary = "Getting user avatars")
//    @Get(uri = "/get/path", produces = MediaType.MULTIPART_FORM_DATA)
//    @SecurityRequirement(name = "BearerAuth")
//    @Secured(SecurityRule.IS_AUTHENTICATED)
//    public SystemFile getAvatarByPath(@QueryValue(value = "file_path") @Parameter Optional<String> path){
//        try{
//            Files metha = filesRepository.findByFilePath(path.orElseThrow()).orElseThrow();
//            File target = new File(metha.getFilePath());
//
//
//            if(!target.exists())
//                throw new RuntimeException("The file does not exist, but there is a record of it: " + path.get());
//
//            return new SystemFile(
//                    target,
//                    mediaTypeFileResolver(target.getName())
//            );
//
//        }catch (Exception ex){
//            logger.error(ex.getMessage());
//            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
//        }
//
//    }


    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Удаление конкретного файла")
    @Delete(uri = "/delete/{type}", produces = MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<DefaultAppResponse> deleteFile(
            @PathVariable String type,
            @QueryValue Optional<String> oid,
            @QueryValue Optional<String> path
    ){
        try{
            Files metha = null;

            if (oid.isPresent())
                metha = filesRepository.findById(oid.get()).orElseThrow();

            if (path.isPresent())
                metha = filesRepository.findByFilePath(path.get()).orElseThrow();

            if(metha == null)
                throw new RuntimeException("Файла с таким OID, или PATH не существует ");
            filesService.delete(metha, type);
            return HttpResponse.ok(responseService.success("файл "+ metha.getFilePath() + " удалён"));

        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }

    }


}
