package com.fortaleza.svc.firmadigital.config;

import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.spi.DocumentationType;

//@Configuration
//@EnableSwagger2
public class SwaggerConfig {
    /*@Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.fortaleza.svc.firmadigital.controller"))
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/svc-digital-signature/**");
    }*/
}
