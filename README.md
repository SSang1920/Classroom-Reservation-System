##  프로젝트 소개

**BookingClassroom** 은 대학 내 강의실 예약을 효율적으로 관리하기 위한 웹 애플리케이션입니다.  

###  목표
- 학생들이 간편하게 강의실을 예약/취소/변경할 수 있도록 지원  
- 예약 현황을 실시간으로 확인 가능  
- 관리자는 승인, 취소, 회원 관리 등을 통합적으로 처리  
- **실시간 동시성 제어**를 통해 여러 사용자가 동시에 예약을 시도해도 **중복 예약을 방지**  

###  주요 기능 요약
- **회원 관리**: 학번 기반 회원가입, 로그인, 권한(Role)별 접근 제어  
- **강의실 예약**: 원하는 날짜/교시를 선택해 예약 신청 및 취소  
- **예약 변경 요청**: 예약된 내역 변경 시 관리자의 승인을 통한 처리  
- **알림(Notification)**: 예약 승인/거절, 변경 처리 등의 상태를 실시간 전달  
- **관리자 페이지**: 회원 목록 조회, 예약 내역 관리, 검색/필터링 기능  
- **중복 방지 예약 시스템**: 동시에 여러 사용자가 신청해도 한 강의실에는 한 명만 예약 가능  

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

