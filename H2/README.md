# DB 연동 없이 경량 테스트용 DB H2 사용하기

### 의존성 추가
```gradle
runtimeOnly 'com.h2database:h2'
```

### application.properties → application.yml 이름 변경
```yml
spring:
  h2:
    console:
      enabled: true
```

http://localhost:8080/h2-console 접속 후 확인
