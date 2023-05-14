# 스프링 부트 3.0 스프링 시큐리티 스터디 

스프링 시큐리티의 동작 흐름 파악

## DEPENDENCY
- Lombok
- Spring Web
- Spring Data JPA
- H2
- **Spring Security**

## 프론트
React.js 로 개발후 build 하여 Spring Project에 담을 예정 


## 페이지 구성
- 인덱스 페이지 ("/") -> 일반 로그인, OAuth2 로그인 버튼, 회원가입 버튼
- 로그인 페이지 ("/login") -> 일반 로그인 폼(ID, PW) 
- 회원가입 페이지 ("/sign-up") -> 회원가입 폼(ID, PW, 이름, 나이, 성별(남, 여, 모름)) 
- 메인 페이지 ("/main") -> 로그인 성공시 이동하는 페이지 , 이름 출력, 로그아웃 버튼

## 기능
- 로그인
- 회원가입
- 로그아웃

## DB
- H2 사용
- Member 테이블
    - id
    - password
    - memberName
    - age
    - sex
    - role