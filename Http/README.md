## 포스트맨이나 스웨거 없이 콘솔창에서 api 호출하기
test 패키지 아래 New HTTP Request 파일 생성

~~~http
GET http://localhost:8080/api/helloWorld

### 직접 JSON 사용
POST https://localhost:8080/group
Content-Type: application/json

{
  "groupName": "그룹명",
  "members": [
    "회원1",
    "회원2",
    "회원3"
  ],
  "date": {
    "year": 2018,
    "month": 1,
    "day": 24
  }
}

### 로컬 파일 사용

POST http://localhost:8080/group
Content-Type: application/json

< ./post.json
~~~

➤ 브라우저를 열지 않고도 IntelliJ 콘솔창에서 api 결과 바로 확인 가능

### 자세한 내용은 https://jojoldu.tistory.com/266 참고 
