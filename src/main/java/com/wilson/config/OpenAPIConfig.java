package com.wilson.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author weixun.gao
 * @date 2021/06/28
 */
@Configuration
public class OpenAPIConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        String[] packagesToScan = {"com.cathaypacific.teams.cpp.adyen.controller"};
        String[] pathsToMatch = {"/cpp-payadyen/v1/notifications"};
        return GroupedOpenApi.builder()
                .group("Default")
                .packagesToScan(packagesToScan)
                .pathsToMatch(pathsToMatch)
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        Contact contact = new Contact();
        contact.setName("Centrailized Payment System ");
        contact.setUrl("https://adyen-cpp-d0.osc1.ct1.cathaypacific.com/terms");
        contact.setUrl("zack_lee@cathaypacific.com");
        return new OpenAPI()
                .info(new Info().title("Centralizied Payment System")
                        .description("Adyen Payment Service Modules")
                        .termsOfService("https://adyen-cpp-d0.osc1.ct1.cathaypacific.com/terms")
                        .contact(contact)
                        .version("1.0")
                        .license(new License().name("Copyright ©  Cathay Pacific Airways Limited")));
    }

}
