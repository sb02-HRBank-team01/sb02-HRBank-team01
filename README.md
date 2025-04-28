# HR Bank - SB2기_1팀 협업 백엔드 프로젝트

📎 [팀 협업 Wiki 문서 바로가기](https://github.com/sb02-HRBank-team01/sb02-HRBank-team01/wiki)

---

## 🌐 배포 주소
📎 [sb02-hrbank-team01-production.up.railway.app](sb02-hrbank-team01-production.up.railway.app)  

---

## 👥 팀원 구성

| 이름     | GitHub 링크 |
|----------|--|
| 안재관   | [https://github.com/kkwan99](https://github.com/kkwan99) |
| 양찬혁   | [https://github.com/20184415](https://github.com/20184415) |
| 윤영로   | [https://github.com/yun0ro](https://github.com/yun0ro) |
| 한성태   | [https://github.com/Seong-taeHan](https://github.com/Seong-taeHan) |

---

## 📝 프로젝트 소개

기업의 인사 데이터를 효율적으로 관리하고, 부서 및 직원 정보의 등록, 수정, 삭제, 조회는 물론 직원 정보 수정 이력 기록, 다운로드 그리고 Spring Batch 기반의 자동화된 데이터 백업 기능을 제공하는 Spring 기반 백엔드 HR 관리 시스템입니다.
또한 실시간 통계 데이터를 제공하는 대시보드 API를 통해 관리자가 전체 인사 현황을 직관적으로 파악할 수 있도록 지원합니다.

- **진행 기간**: 2025.04.21 ~ 2025.04.29

--- 

## 🛠️ 기술 스택
### Backend
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white">

### Database 
<img src="https://img.shields.io/badge/postgresql-4169E1?style=for-the-badge&logo=postgresql&logoColor=white">

### Collaboration Tools
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white"> <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">

---

## 🖥️ 화면 구성

| 대시보드_1 | 대시보드_2 |
|----------|------------|
| <img width="1471" alt="스크린샷 2025-04-27 오후 10 50 43" src="https://github.com/user-attachments/assets/9ef55eca-f14b-4e70-982c-25eee5afcf2d" /> | <img width="1471" alt="스크린샷 2025-04-27 오후 10 50 43" src="https://github.com/user-attachments/assets/9ef55eca-f14b-4e70-982c-25eee5afcf2d" /> |


| 부서 관리 | 직원 관리 |
|------------|------------|
| <img width="1471" alt="스크린샷 2025-04-27 오후 10 50 43" src="https://github.com/user-attachments/assets/9ef55eca-f14b-4e70-982c-25eee5afcf2d" /> | <img width="1471" alt="스크린샷 2025-04-27 오후 10 50 43" src="https://github.com/user-attachments/assets/9ef55eca-f14b-4e70-982c-25eee5afcf2d" /> |

| 수정 이력 | 데이터 백업 |
|------------|------------|
| <img width="1471" alt="스크린샷 2025-04-27 오후 10 50 43" src="https://github.com/user-attachments/assets/9ef55eca-f14b-4e70-982c-25eee5afcf2d" /> | <img width="1471" alt="스크린샷 2025-04-27 오후 10 50 43" src="https://github.com/user-attachments/assets/9ef55eca-f14b-4e70-982c-25eee5afcf2d" /> |

---

## 💻 팀원별 구현 기능

> 주요 기능 현황입니다. 보다 자세한 구현 내용은 위에 첨부한 Wiki 링크를 누르시면 확인 하실 수 있습니다.

### ✅ 안재관  
- **부서 관리 기능 전반 구현**  
    - 부서 등록, 수정, 삭제, 상세 조회, 목록 조회 기능 구현  
    - 부서 목록 조회 시 **QueryDSL** 기반 **동적 검색**, **정렬**, **커서 기반 페이지네이션** 처리  
    - 부서별 **소속 직원 수** 함께 제공하는 로직 구현  
    - 소속 직원이 있는 경우 부서 삭제 제한 로직 포함  
    - 전체적인 예외 처리 및 유효성 검증 설계  
- **대시보드 통계 연계**  
    - 부서별 직원 분포, 직원 수 집계 등 통계 API 설계 협업 및 연계 처리


---

### ✅ 한성태  
- **직원 관리 기능 전반 구현**
  - 직원 등록, 수정, 삭제, 상세 조회, 목록 조회 기능 구현
  - 직원 목록 조회 시 **QueryDSL** 기반 **동적 검색**, **정렬**, **커서 기반 페이지네이션** 처리
  - 직원 상태(예: ACTIVE, ON_LEAVE, RESIGNED) 및 부서, 직책 등의 필터링 조건을 반영한 조회 조건 구현
  - 직원 삭제 시 연관된 binary_contents 데이터 삭제
  - 전체적인 예외 처리 및 유효성 검증 설계
- **대시보드 기능 구현**
  - 직원 상태별 직원 수 조회 구현
  - 직원 수 변동 추이 조회 (기간 및 기준 단위별로 변동 추이 확인 가능) 구현
  - 직책별, 부서별 직원 분포 및 상태별 분포 조회 구현


---

### ✅ 윤영로  
- **직원 정보 수정 및 이력 관리**
    - 이력 등록 기능 구현
    - 이력 목록 조회 시 **QueryDSL** 기반 **동적 검색**, **정렬**, **커서 기반 페이지네이션** 처리
    - 이력 상세 변경 내용 조회 구현
    - 이력 상태(직원 추가, 정보 수정, 직원 삭제)를 반영 
      
---

### ✅ 양찬혁  
- **파일 관리 기능 구현**
    - 파일 메타데이터와 실제 파일 분리 저장 시스템 설계 및 구현
    -  파일 다운로드 API 구현
    - 파일 메타데이터(파일명, 콘텐츠 타입, 크기) 관리 로직 개발
  
- **백업 기능 구현**
    -  백업 시스템의 자동/수동 실행 메커니즘 구현 및 상태 관리
    - 직원 데이터의 주기적 백업 및 CSV 포맷 저장 로직 구현
    - 백업 과정의 예외 처리 및 실패 상태 관리 시스템 설계
    - 백업 이력 조회 및 커서 기반 페이지네이션 처리


---

## 📁 디렉토리 구조
<pre> 
  src 
  ┣ main 
  ┃ ┗ generated 
  ┃ ┃ ┗ com.team.hrbank.entity
  ┃ ┃ ┣ QBackup 
  ┃ ┃ ┣ QBaseEntity
  ┃ ┃ ┣ QBaseUpdatableEntity
  ┃ ┃ ┣ QBinaryContent
  ┃ ┃ ┣ QChangeLog
  ┃ ┃ ┣ QChangeLogDetail
  ┃ ┃ ┣ QDepartment
  ┃ ┃ ┣ QEmployee
  ┃ ┗ java 
  ┃ ┃ ┗ com.team.hrbank
  ┃ ┃ ┣ config
  ┃ ┃ ┣ controller 
  ┃ ┃ ┣ converter
  ┃ ┃ ┗ dto
  ┃ ┃ ┃ ┣ backup 
  ┃ ┃ ┃ ┣ binarycontent
  ┃ ┃ ┃ ┣ changelog 
  ┃ ┃ ┃ ┣ department
  ┃ ┃ ┃ ┣ employee
  ┃ ┃ ┃ ┣ error
  ┃ ┃ ┣ service 
  ┃ ┃ ┣ entity
  ┃ ┃ ┗ repository
  ┃ ┃ ┃ ┣ custom
  ┃ ┃ ┣ exception 
  ┃ ┃ ┣ utils 
  ┃ ┃ ┣ enums
  ┃ ┃ ┣ mapper
  ┃ ┃ ┣ strorage
  ┃ ┣ resources 
  ┃ ┃ ┣ HrBankApplication.yaml 
  ┃ ┃ ┗ static 
  ┃ ┃ ┣ css 
  ┃ ┃ ┃ ┗ style.css 
  ┃ ┃ ┗ js 
  ┃ ┃ ┗ script.js 
  ┣ test 
  ┃ ┗ java/com/example 
  ┃ ┗ HrBankApplicationTests.java  
  ┣ .gitignore 
  ┣ build.gradle
  ┗ README.md 
</pre>

--- 

## 📝 프로젝트 회고록

- 프로젝트를 진행하며 느낀 점과 배운 점을 정리한 팀원별 회고 자료입니다.  
- 아래 링크를 통해 회고 내용을 확인할 수 있습니다.

📎 **[회고록 링크](회고록_링크_URL)**  
