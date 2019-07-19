# 생성시간/수정시간 자동화 - JPA Auditing
보통 Entity에는 해당 데이터의 생성시간과 수정시간을 포함시킵니다. <br/>
언제 만들어졌는지, 언제 수정되었는지 등은 차후 유지보수에 있어 굉장히 중요한 정보이기 때문입니다. <br/>
그렇다보니 매번 DB에 insert하기전, update 하기전에 날짜 데이터를 등록/수정 하는 코드가 여기저기 들어가게 됩니다. <br/>

생성일 추가 코드 예제
```java
public void savePosts(){
    ...
    posts.setCreateDate(new LocalDate());
    postsRepository.save(posts);
    ...
}
```
이런 단순하고 반복적인 코드가 모든 테이블과 서비스 메소드에 포함되어야 한다고 생각하면 어마어마하게 귀찮고 코드가 더러워지겠죠? <br/>
그래서 이 문제를 해결하기 위해 JPA Auditing를 사용하겠습니다.

### BaseTimeEntity 생성

src/main/java/com/jojoldu/webservice/domain에 아래와 같이 BaseTimeEntity 클래스를 생성하겠습니다.
```java
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

}
```
BaseTimeEntity클래스는 모든 Entity들의 상위 클래스가 되어 Entity들의 createdDate, modifiedDate를 자동으로 관리하는 역할입니다.

<b>@MappedSuperclass</b><br/>
JPA Entity 클래스들이 BaseTimeEntity을 상속할 경우 필드들(createdDate, modifiedDate)도 컬럼으로 인식하도록 합니다.<br/>
<b>@EntityListeners(AuditingEntityListener.class)</b><br/>
BaseTimeEntity클래스에 Auditing 기능을 포함시킵니다.<br/>
<b>@CreatedDate</b><br/>
Entity가 생성되어 저장될 때 시간이 자동 저장됩니다.<br/>
<b>@LastModifiedDate</b><br/>
조회한 Entity의 값을 변경할 때 시간이 자동 저장됩니다.<br/>

### Domain 클래스가 BaseTimeEntity를 상속받도록 변경
```java
...
public class Posts extends BaseTimeEntity {
    ...
}
```

### Application 클래스에 JPA Auditing 어노테이션 활성화 어노테이션 추가
```java
@EnableJpaAuditing // JPA Auditing 활성화
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

--------

### JPA Auditing 테스트 코드 작성하기
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    @After
    public void cleanup() {
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기() {
        ...
    }

    @Test
    public void BaseTimeEntity_등록 () {
        //given
        LocalDateTime now = LocalDateTime.now();
        postsRepository.save(Posts.builder()
                .title("테스트 게시글")
                .content("테스트 본문")
                .author("jojoldu@gmail.com")
                .build());
        //when
        List<Posts> postsList = postsRepository.findAll();

        //then
        Posts posts = postsList.get(0);
        assertTrue(posts.getCreatedDate().isAfter(now));
        assertTrue(posts.getModifiedDate().isAfter(now));
    }
}
```
