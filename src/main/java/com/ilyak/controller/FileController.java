package com.ilyak.controller;

import com.ilyak.entity.jpa.Files;
import com.ilyak.entity.jpa.User;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
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
    @Operation(summary = "Getting user avatars")
    @Get(uri = "/avatar/get/oid", produces = MediaType.MULTIPART_FORM_DATA)
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
                                fileTypeResolver(target.getName())
                    );

        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error("Failed get avatar: " +ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Getting user avatars")
    @Get(uri = "/avatar/get/path", produces = MediaType.MULTIPART_FORM_DATA)
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
                    fileTypeResolver(target.getName())
            );

        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }
}
