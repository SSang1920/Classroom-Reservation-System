# BookingClassroom

> 대학 강의실 예약을 **실시간** 관리하는 웹 애플리케이션

![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)  
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)  
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)  
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)  
![Nginx](https://img.shields.io/badge/Nginx-009639?logo=nginx&logoColor=white)  

---

## 목차
1. [프로젝트 소개](#프로젝트-소개)  
2. [목표](#목표)  
3. [주요 기능](#주요-기능)  
4. [구현 화면](#구현-화면)  
5. [아키텍처](#아키텍처)  
6. [기술 스택 상세](#기술-스택-상세)  
7. [프로젝트 실행 방법](#프로젝트-실행-방법)  
8. [데이터베이스 설계 (ERD)](#데이터베이스-설계-erd)

---

##  프로젝트 소개
**BookingClassroom** 은 대학 내 강의실 예약을 효율적으로 관리하기 위한 웹 애플리케이션입니다.  

- 학생들이 간편하게 강의실을 예약/취소/변경 가능  
- 관리자는 예약 승인, 취소, 회원 관리 등을 통합적으로 처리  
- **실시간 동시성 제어**를 통해 중복 예약을 방지

---

## 목표
- 강의실 예약 프로세스의 편의성 제공
- 예약 현황 실시간 확인 가능
- 관리자 권한 기반 통합 관리
- **동시성 제어**로 다중 사용자 예약 충돌 방지

---

## 주요 기능
- 학번 기반 **JWT 인증/인가**(Access 1h / Refresh 14d), BCrypt 비밀번호
- **강의실 예약/취소/변경 요청** + **동시성 제어로 중복 방지**
- **SSE 실시간 알림**(승인/거절/변경), 읽음 상태 관리
- **히스토리 엔티티**로 변경·취소 흐름 추적
- **관리자 대시보드**: 회원/시설/예약/요청/공지 통합 관리

### JWT 기반 인증/인가
<details>
  <summary>자세히 보기</summary>

  - **2-토큰**: Access(1h), Refresh(14d)  
  - 만료 시 Refresh로 재발급, 로그아웃 시 서버 저장 Refresh 무효화  
  - 토큰 클레임: 학번(ID), 이름, 역할(`ROLE_STUDENT`, `ROLE_ADMIN`)  
  - 비밀번호 **BCrypt 해시**, `JWT_SECRET_KEY`는 환경 변수로 관리  
</details>

### 강의실 예약
<details>
  <summary>자세히 보기</summary>

  - 날짜/교시 선택 **예약 신청**, **취소**, **변경 요청**  
  - **실시간 동시성 제어**로 중복 예약 방지  
  - 변경 요청은 **관리자 승인 후 반영**  
  - 취소/변경 요청은 **History 엔티티**로 투명하게 관리 
</details>

### 알림
<details>
  <summary>자세히 보기</summary>

  - 승인/거절/변경 등 **이벤트 발생 시 푸시**  
  - 읽음/안읽음(`isRead`, `readAt`) 상태 관리, 전체 읽음 처리  
  - **SSE 구독** 기반 실시간 알림 구독 지원 → 예약 상태 변경 시 즉시 알림 수신
</details>

### 예약 히스토리
<details>
  <summary>자세히 보기</summary>

  - **요청 자체가 히스토리**로 생성되어 상태 흐름 추적  
  - 관리자는 히스토리를 보고 승인/거절, 사용자는 자신의 요청 내역 조회
</details>

### 관리자 기능
<details>
  <summary>자세히 보기</summary>

  - **대시보드**: 회원 수, 예약 현황, 시설 수, 대기 중 요청 등  
  - **시설 관리**: 강의실 추가/수정/삭제, 사용 가능/불가 전환  
  - **회원 관리**: 조건 검색, 삭제(관리자 계정 제외)  
  - **예약 관리**: 학생의 변경 요청 **승인/거절**  
  - **공지사항 관리**: 등록/수정/삭제  
</details>

---

## 구현 화면

### 요약(표)
| 기능 | 화면 |
| ------- | ------- |
| 홈 화면 | ![홈 화면](https://github.com/user-attachments/assets/b18cc16c-fa17-4230-b764-cf2cca08eedf)|


### 강의실 예약
![강의실 예약](https://github.com/user-attachments/assets/88937b32-29a8-4c62-9a80-dc83da741d26)

### 예약 변경 요청
![예약 변경 요청](https://github.com/user-attachments/assets/030bc9b4-adb9-4a54-8235-b110c27be4cc)

### 알림 기능 구현
![알림 기능](https://github.com/user-attachments/assets/8571baa0-b79a-4007-b51c-d34967e32c64)

### 관리자 페이지

#### 대시보드
![관리자 대시보드](https://github.com/user-attachments/assets/eeaa7040-d476-4d59-9e52-27db312298c2)

#### 회원 관리
![회원 관리](https://github.com/user-attachments/assets/114c18bb-008b-4c75-918e-380799924b74)

#### 변경 요청 관리
![변경 요청 관리](https://github.com/user-attachments/assets/a2c86e54-97de-4f96-a73b-60c9240f9d44)

#### 공지사항 관리
![공지사항 관리](https://github.com/user-attachments/assets/8f5c92df-9f6b-4ff3-a680-e1cc4c698d20)

## 5. 설치 및 실행 방법

## 기술 스택

### Backend
- Java 17, Spring Boot 3.x  
- Spring Data JPA, QueryDSL  
- Spring Security, JWT  
- MySQL, Lombok  

### Frontend
- Thymeleaf, JavaScript, Bootstrap  

### Infra & Tools
- IntelliJ IDEA, Git/GitHub

##  배포 & 운영
- **Oracle Cloud (OCI)** – 서버 및 데이터베이스 배포 환경  
- **Docker, Docker Compose** – 컨테이너 기반 서비스 관리  
- **Nginx** – Reverse Proxy 및 정적 리소스 제공

 

## 데이터베이스 설계도 (ERD)
![데이터베이스 설계도](https://github.com/user-attachments/assets/e41b2d48-5024-4496-b2ae-348b9d126452)




