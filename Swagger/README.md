# Spring Boot에 Swagger 적용하기

### 1. 라이브러리 추가

Gradle
~~~java
compile 'io.springfox:springfox-swagger2:2.9.2'
compile 'io.springfox:springfox-swagger-ui:2.9.2'
~~~

Maven
~~~xml
<!-- Swagger -->
<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-swagger2</artifactId>
	<version>2.9.2</version>
</dependency>
    
<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-swagger-ui</artifactId>
	<version>2.9.2</version>
</dependency>
~~~		

### 2. SwaggerConfig 생성

~~~java
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
~~~

### 3. 각각에 맞는 Annotation 작성

Controller
~~~java
@Api(description = "유저 REST API")	//컨트롤러 설명
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @ApiOperation(value = "회원 가입")  //메서드 설명
    @ApiImplicitParams({              //파라미터 설명
        @ApiImplicitParam(name = "name", value = "이름", required = true),
        @ApiImplicitParam(name = "part", value = "파트", required = true, defaultValue = "서버"),
        @ApiImplicitParam(name = "password", value = "비밀번호", required = true)
    })
    @ApiResponses({                   //응답 설명
        @ApiResponse(code = 200, message = "회원 가입 성공"),
        @ApiResponse(code = 500, message = "내부 서버 에러")
    })
    public ResponseEntity signUp(SignUpReq signUpReq) 
~~~    

RequestDto
~~~java
@ApiModel(value = "Login Request Dto : 로그인 내용", description = "로그인 내용")
@Data
public class LoginReq {
    @ApiModelProperty(readOnly = true) //표시 안함
    private LocalDateTime createTime;
    
    @ApiModelProperty(value = "이름", example = "신혜란", position = 1)
    private String name;
    @ApiModelProperty(value = "비밀번호", example = "hyeran", position = 2)
    private String password;
}
~~~
