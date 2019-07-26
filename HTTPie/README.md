# HTTP 클라이언트 HTTPie

브라우저에 api 주소를 입력할 경우 GET으로만 요청이 된다.<br/>
그래서 form을 만들어서 수행하거나 Postman 등의 여러가지 도구들을 사용해서 api 결과를 확인하는데<br/>
HTTPie는 api 결과를 커맨드 라인에서 이를 간단하게 확인할 수 있는 HTTP 클라이언트 도구이다.

#### 사용 예시
```
http GET localhost:8080/restaurants
http POST localhost:8080/restaurants name=BeRong address=Busan
```

https://httpie.org/#installation﻿
