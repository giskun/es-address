package com.ruixin.esaddress.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Create rest api docket.
     *
     * @return the docket
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(metaData())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ruixin.esaddress.controller"))
                .paths(PathSelectors.any())
                .build();

    }

    private ApiInfo metaData() {

        return new ApiInfoBuilder()
                .title("地址匹配 API文档")
                .description("SpringBoot 集成Elasticsearch 所记录")
                .termsOfServiceUrl("")
                .contact(new Contact("情指项目组", "", ""))
                .version("1.0")
                .build();
    }
}
