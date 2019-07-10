@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)     //기본 반환 메세지 설정
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())   //기본 패키지 설정
                .paths(PathSelectors.ant("/api/**"))   //노출할 API 경로 패턴 설정
                .build();
    }

    //Swagger UI 페이지에 노출할 정보 커스텀
    @SuppressWarnings("deprecation")
    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "Title",
                "Description",
                "Version",
                "Terms of Service URL",
                "contact Name",
                "License",
                "License URL"
        );
        return apiInfo;
    }
}
