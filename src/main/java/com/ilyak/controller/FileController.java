package com.ilyak.controller;

import com.ilyak.entity.jpa.Files;
import com.ilyak.entity.jpa.User;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.server.types.files.SystemFile;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.reactivex.rxjava3.core.Flowable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Controller("/api/files")
@Tag(name = "Контроллер файлов приложения",
        description = "Данный котроллер отвечает за получение, добавление, и изменение файлов"
)
@Secured(SecuredAnnotationRule.IS_ANONYMOUS)
@Validated
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "jwt")
public class FileController extends BaseController{

    public static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Adding user avatars")
    @Post(uri = "/avatar/add", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<DefaultAppResponse> addAvatar(@Part Publisher<CompletedFileUpload> avatars){
        try{
            User user = getCurrentUser();
            Flowable.fromPublisher(avatars).subscribe(emitted -> {
                Files target = filesService.save(emitted, filesService.getDirPattern() + filesService.getAvatars());
                filesRepository.save(target);
                user.getAvatars().add(target);
            });
            userRepository.update(user);
            return HttpResponse.ok(responseService.success("avatars uploaded successfully"));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Adding user avatars")
    @Post(uri = "/post/add{?post_oid}", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<DefaultAppResponse> addPostPhotos(
            @Part Publisher<CompletedFileUpload> photos,
            @QueryValue Optional<String> post_oid
    ){
        try{
            com.ilyak.entity.jpa.Post post = postRepository.findById(post_oid.orElseThrow()).orElseThrow();
            Flowable.fromPublisher(photos).subscribe(emitted -> {
                Files target = filesService.save(emitted, filesService.getDirPattern() + filesService.getPostPhotos());
                filesRepository.save(target);
                post.getPostPhotos().add(target);
            });
            postRepository.update(post);
            return HttpResponse.ok(responseService.success("фотографии к посту загружены"));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }


    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Adding user avatars")
    @Patch(uri = "/post/update{?post_oid}", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<DefaultAppResponse> updPostPhotos(
            @Part Publisher<CompletedFileUpload> photos,
            @Part List<String> delete_oids,
            @QueryValue Optional<String> post_oid
    ){
        try{
            com.ilyak.entity.jpa.Post post = postRepository.findById(post_oid.orElseThrow()).orElseThrow();
            if(delete_oids!= null) delete_oids.forEach(it -> {
                post.getPostPhotos().stream();
            });
            Flowable.fromPublisher(photos).subscribe(emitted -> {
                Files target = filesService.save(emitted, filesService.getDirPattern() + filesService.getPostPhotos());
                filesRepository.save(target);
                post.getPostPhotos().add(target);
            });
            postRepository.update(post);
            return HttpResponse.ok(responseService.success("фотографии к посту загружены"));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Getting user avatars")
    @Get(uri = "/get/oid", produces = MediaType.MULTIPART_FORM_DATA)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public SystemFile getAvatarByOid(@QueryValue(value = "file_oid") @Parameter Optional<String> oid){
        try{
            Files metha = filesRepository.findById(oid.orElseThrow()).orElseThrow();
            File target = new File(metha.getFilePath());


            if(!target.exists())
                throw new RuntimeException("The file does not exist, but there is a record of it: " + oid.get());

            return new SystemFile(
                                target,
                                mediaTypeFileResolver(target.getName())
                    );

        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error("Failed get avatar: " +ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Getting user avatars")
    @Get(uri = "/get/path", produces = MediaType.MULTIPART_FORM_DATA)
    @SecurityRequirement(name = "BearerAuth")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public SystemFile getAvatarByPath(@QueryValue(value = "file_path") @Parameter Optional<String> path){
        try{
            Files metha = filesRepository.findByFilePath(path.orElseThrow()).orElseThrow();
            File target = new File(metha.getFilePath());


            if(!target.exists())
                throw new RuntimeException("The file does not exist, but there is a record of it: " + path.get());

            return new SystemFile(
                    target,
                    mediaTypeFileResolver(target.getName())
            );

        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }

    }


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
