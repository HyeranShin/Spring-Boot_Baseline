# S3 연결하기

### 1. S3 버킷 생성
생성 후 권한
→ 퍼블릭 액세스 설정 → 편집 → 체크 해제
→ 버킷 정책 → 정책 생성기 → 
```
Select Type of Policy: S3 Bucket Policy
Effect: Allow
Principal: *
AWS Service: Amazon S3
Actions: GetObject
Amazon Resource Name(ARN): arn:aws:s3:::버킷 이름/*
```
Add Statement → Generate Policy → 내용 복사 → 버킷 정책 편집기 붙여넣기
→ 퍼블리 액세스 권한이 있음 뜨면 성공

### 2. IAM 계정 액세스 키 ID, 비밀 액세스 키 발급
사용자 추가 → 액세스 유형: 프로그래밍 방식 액세스 → 기존 정책 직접 연결: AmazonS3FullAccess

### 3. application.yml 파일 수정
```yml
cloud:
  aws:
    stack:
      auto: false
    s3:
      bucket: dodami-bucket
      bucket_url: https://s3.ap-northeast-2.amazonaws.com/dodami-bucket
    region:
      static: ap-northeast-2
```

### 4. aws.yml 파일 생성
```yml
cloud:
  aws:
    credentials:
      accessKey: 액세스 키 ID
      secretKey: 비밀 액세스 키
```

### 5. Application 파일 수정
```java
    public static final String APPLICATION_LOCATIONS = "spring.config.location="
            +"classpath:application.yml, "
            +"classpath:aws.yml";

    public static void main(String[] args) {
        new SpringApplicationBuilder(DodamiApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }
```

### 6. build.gradle 수정
```gradle
repositories {
    mavenCentral()
    maven { url 'https://repo.spring.io/libs-milestone'}
}

dependencies {
    implementation 'org.springframework.cloud:spring-cloud-starter-aws'
}

dependencyManagement {
    imports {
        mavenBom 'org.springframework.cloud:spring-cloud-aws:2.0.0.RC2'
    }
}
```

### 7. S3FileUploadService 파일 추가
```java
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class S3FileUploadService {

    //버킷 이름 동적 할당
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    //버킷 주소 동적 할당
    @Value("${cloud.aws.s3.bucket_url}")
    private String defaultUrl;

    private final AmazonS3Client amazonS3Client;

    public S3FileUploadService(final AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public String upload(MultipartFile uploadFile) throws IOException {
        String origName = uploadFile.getOriginalFilename();
        String url;
        try {
            //확장자
            final String ext = origName.substring(origName.lastIndexOf('.'));
            //파일이름 암호화
            final String saveFileName = getUuid() + ext;
            //파일 객체 생성
            File file = new File(System.getProperty("user.dir") + saveFileName);
            //파일 변환
            uploadFile.transferTo(file);
            //S3 파일 업로드
            uploadOnS3(saveFileName, file);
            //주소 할당
            url = defaultUrl + saveFileName;
            //파일 삭제
            file.delete();
        } catch (StringIndexOutOfBoundsException e) {
            //파일이 없을 경우 예외 처리
            url = null;
        }
        return url;
    }

    private static String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    //S3에 파일을 업로드한다.
    private void uploadOnS3(final String fileName, final File file) {
        //AWS S3 전송 객체 생성
        final TransferManager transferManager = new TransferManager(this.amazonS3Client);
        //요청 객체 생성
        final PutObjectRequest request = new PutObjectRequest(bucket, fileName, file);
        //업로드 시도
        final Upload upload = transferManager.upload(request);

        try {
            //완료 확인
            upload.waitForCompletion();
        } catch (AmazonClientException amazonClientException) {
            log.error(amazonClientException.getMessage());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }
}
```

### 8. 해당 컨트롤러에 메서드 추가
```java
    @PostMapping
    public ResponseEntity<ImageResDto> uploadImage(@RequestParam("image") MultipartFile multipartFile) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ImageResDto(s3FileUploadService.upload(multipartFile)));
    }
```
