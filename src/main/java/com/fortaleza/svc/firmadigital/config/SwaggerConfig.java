package com.fortaleza.svc.firmadigital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableWebMvc
public class SwaggerConfig {
    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.fortaleza.svc.firmadigital.controller"))
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/svc-digital-signature/")
                .apiInfo(getApiInfo());
    }
    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder().title("SVC-DIGITAL-SIGNATURE Service API")
                .description("SVC-DIGITAL-SIGNATURE Service API Description")
                .contact(new Contact("Área de desarrollo - Gerencia Nacional de Tecnología", null, "desarrollo_tecnologia@grupofortaleza.com.bo"))
                .version("1.0.0")
                .build();
    }
}
