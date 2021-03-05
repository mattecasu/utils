package utils.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("utils"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(
                        new ApiInfo(
                                "Util API",
                                "",
                                "latest",
                                "",
                                new Contact("Matteo Casu", "", ""),
                                "",
                                "",
                                newArrayList())
                );
    }
}
