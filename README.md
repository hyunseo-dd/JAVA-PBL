# ⏳ JUST 10min To-do List (자바 데스크톱 일정 관리 애플리케이션)

[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fmii-ii%2FJAVA-PBL&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)

> **Java 기반 문제해결 프로젝트 (PBL)**

---

## 👩‍💻 포트폴리오 소유자: 신령미 (Shin Ryeong-mi)

본 저장소는 `JUST 10min To-do List` 프로젝트의 구현 결과를 담고 있으며, 특히 **핵심 비즈니스/데이터 로직(백엔드 레이어) 구현, 기술 문서 집필, 발표용 시연 영상 제작 등 프로젝트 전반을 성공적으로 리드**한 **신령미**의 포트폴리오(회사 제출용) 목적으로 재구성된 저장소입니다.

*   **포지션:** Lead Developer (Data & Services) & Project Manager
*   **주요 성과 및 역할:** 
    *   `TaskService`, `TodoFileManager` 등 핵심 비즈니스 로직 및 데이터 영속성(Persistence) 층 설계 및 구현
    *   프로젝트 아키텍처 수립 및 팀 간 업무 통합 리드
    *   결과보고서 메인 집필 및 산출물(기획/설계 문서) 총괄 작성 (팀 문서화 기여도 90% 이상)
    *   핵심 기능 시연 영상(프레젠테이션 및 데모용) 전담 기획 및 영상 편집 완료

---

## 💡 프로젝트 개요 (Project Overview)
**'JUST 10min To-do List'**는 단순한 일정 관리를 넘어 사용자의 몰입도와 생산성을 극대화하기 위해 기획된 **자바 데스크톱 애플리케이션**입니다. Java AWT/Swing을 활용하여 직관적인 GUI를 제공하며, 일정(Task)의 CRUD, 달력 연동, 뽀모도로(Pomodoro) 타이머를 활용한 집중 모드 및 주간 통계 시스템을 하나의 워크스페이스에 통합하여 사용자 경험을 최적화했습니다.

## 🎯 핵심 기여 상세 (Key Contributions of Shin Ryeong-mi)

### 1. 비즈니스 로직 및 데이터 관리(Backend Layer) 구현
*   **스케줄 데이터 설계 및 `TaskService` 구현:** 애플리케이션의 뼈대가 되는 일정 관리의 비즈니스 로직(조회, 추가, 수정, 완료 처리 로직 등)을 전담하였습니다. 반복 일정(Daily, Weekly, Monthly)에 따른 `DueDate` 자동 갱신 로직을 설계해 예외 처리를 강화했습니다.
*   **데이터 영속성 보장 (`TodoFileManager`):** 데이터베이스 환경 없이도 영속성을 보장하기 위해, JSON 형태의 로컬 데이터 직렬화/역직렬화(I/O 통신) 로직을 적용하여 사용자의 일정 데이터가 앱 종료 후에도 안전하게 유지되도록 구현했습니다.

### 2. 프로젝트 퀄리티 컨트롤 및 산출물 총괄(Project Management)
*   **기술 문서 설계 및 결과보고서 주도:** 초기 요구사항 정의서부터 클래스 다이어그램 설계, 최종 결과보고서의 트러블슈팅 사례 작성까지, 개발 주기의 모든 문서화를 사실상 단독으로 완성하여 프로젝트 커뮤니케이션의 기준점을 확립했습니다.
*   **홍보 / 발표용 시연 영상 전담 제작:** 애플리케이션의 핵심 로직(캘린더 연동, 타이머 통계 등)이 잘 드러나도록 데모 영상의 시나리오 기획 및 영상 편집을 도맡아 수행하였고, 결과적으로 발표 심사에서 높은 이해도와 퀄리티를 인정받았습니다.

---

## 🏗️ 세부 시스템 구성 및 주요 기능 (Features)

1. **캘린더 & 일정 관리 (Calendar & Task Management)**
   *   `CalendarView`, `CalendarMain`: 직관적인 달력 기반 UI로, 날짜별 일정 조회 및 스케줄 등록 기능 구현 (싱글/더블 클릭 이벤트 분리 관리).
   *   우선순위(Priority)별 정렬 및 상태 기준 검색, 반복 일정 자동 계산 지원.

2. **뽀모도로 및 스마트 타이머 (Pomodoro & Timer)**
   *   `TimerAndStatsDialog`, `PomodoroPopup`: 단순히 일정을 적는데 그치지 않고, 작업의 실행과 몰입을 돕는 자체 타이머 시스템 구축.
   *   `TenMinuteTimer`: "딱 10분만 집중하자"는 모토에 맞춘 초단기 밀도 타이머 기능.

3. **작업 통계 모니터링 관리 (Statistics & Monitoring)**
   *   `WeeklyStatsPopup`, `StatisticsService`: 사용자의 일정 완수율과 타이머 사용 시간을 추적하여 주간 성과를 시각적으로 모니터링할 수 있는 피드백 루프 제공.

---

## 🛠️ 기술 스택 (Tech Stack)
*   **Language / Framework:** Java (JDK 8+), Java AWT / Swing (GUI Framework)
*   **Architecture & Data:** MVC Pattern-inspired, Local File I/O (JSON 직렬화)
*   **Documentation & Media:** Premiere Pro(시연 영상 기획 및 편집), Microsoft Word(최종보고서), Markdown

---

## 👥 팀원 역할 분담
*   **신령미 (Lead):** 애플리케이션 데이터 모델 연동(`TaskService`), 파일 I/O 직렬화 구현, 프로젝트 최종 결과보고서 및 기획 문서 집필 총괄, 데모 시연 영상 기획/편집
*   팀원 A: 프론트엔드 GUI 구축 (Java Swing) 및 캘린더 컴포넌트 렌더링 구현
*   팀원 B: 타이머/통계 팝업 UI 설계 및 화면 전환 이벤트 처리
*   팀원 C: 테스트 코드 작성 보조 및 버그(UI 레이아웃) QA

---

## 📁 주요 프로젝트 산출물 (Repository Structure)
*   `*/.java`: 주요 시스템 소스코드 (Calendar, Task, Timer Services 등)
*   `*.dat` / `*.json`: 모의/테스트 실행용 로컬 DB (데이터 영속성 저장소)
*   `결과보고서 및 문서/`: **[신령미 총괄 작성]** 프로젝트 기획안, 산출물 내역 및 트러블슈팅 정리
*   `미디어 자료/`: **[신령미 단독 제작]** PBL 결과 발표용 및 포트폴리오용 데모 시연 영상