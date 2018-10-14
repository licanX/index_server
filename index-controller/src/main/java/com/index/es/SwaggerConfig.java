package com.index.es;

import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger-ui
 * @author lic
 * @date 2018年8月26日
 * @since v1.0.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("com.index")).paths(PathSelectors.any()).build();
	}
	
	
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("API").description("API 接口调试页面").version("1.0").build();
	}
}
