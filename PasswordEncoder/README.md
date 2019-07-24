# PasswordEncoder 적용하기

### 1. build.gradle 추가
```gradle
    implementation 'org.springframework.security:spring-security-core:5.0.6.RELEASE'
```

### 2. WebMvcConfig 파일 추가
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 3. 해당 Service 파일 수정
~~~java
    private final PasswordEncoder passwordEncoder;

signUp 함수 내부
    userRepository.save(signUpReqDto.toUser(passwordEncoder.encode(signUpReqDto.getPassword())));
    //toUser 함수에서 signUpReqDto의 password 대신 파라미터로 넘겨 받은 password 사용
    
signIn 함수 내부
    matchPassword(user.getPassword(), signInReqDto.getPassword());
    
public boolean matchPassword(String password, String signInPassword) {
        if(!passwordEncoder.matches(signInPassword, password)) {
            throw new NotMatchException("password", "비밀번호가 일치하지 않습니다.");
        }
        return Boolean.TRUE;
    }
~~~
