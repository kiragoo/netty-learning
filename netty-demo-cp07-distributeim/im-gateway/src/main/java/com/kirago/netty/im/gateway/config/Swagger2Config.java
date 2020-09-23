package com.kirago.netty.im.gateway.config;

import com.google.common.base.Predicate;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
public class Swagger2Config implements WebMvcConfigurer {
    //swagger2的配置文件，这里可以配置swagger2的一些基本的内容，比如扫描的包等等

    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        
        
    }


    @Bean
    public Docket createRestApi() {
        //为当前包路径,这个包指的是在哪些类中使用swagger2来测试
        Predicate<RequestHandler> selector = RequestHandlerSelectors
                .basePackage("com.kirago.netty.im.gateway.controller");
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(selector)
                .paths(PathSelectors.any())
                .build();
    }

    //构建 api文档的详细信息函数,注意这里的注解引用的是哪个
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("分布式 IM  API 文档")
                //创建人
                //版本号
                .version("1.0")
                .termsOfServiceUrl("http://localhost:8080/swagger-ui.html")   //描述
                .description("API 描述")
                .build();
    }


}
