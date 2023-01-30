package com.ilyak;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;

import java.util.TimeZone;

@OpenAPIDefinition(
        info = @Info(
                title = "rrs-backend-mic",
                version = "0.1",
                description = "My API"
        )
)
public class  Application {
    public static void main(String[] args)
    {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
        Micronaut.run(Application.class, args);
    }
}
