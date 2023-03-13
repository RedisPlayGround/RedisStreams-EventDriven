# Redis Streams를 이용한 Event-Driven 아키텍쳐 개발 및 학습

## MSA와 Event-Driven 아키텍쳐

### MSA(Microservice Architecture)는 무엇인가

- 시스템을 독립적인 단위의 작은 서비스들로 분리(크기보다는 독립성이 중요)

=> 독립적인 단위 : 다른 서비스와 다른 이유로 변경되고, 다른 속도로 변경되는 단위

- 각 서비스들이 사용하는 DB도 분리
- 각 서비스들은 API(인터페이스)를 통해서만 통신(다른 서비스의 DB 접근 불가)

### 기존의 Monolithic 아키텍쳐

- 모든 기능들이 한 서버 안에 들어가 있고, 공유 데이터베이스를 사용

![image](https://user-images.githubusercontent.com/40031858/224592717-ff65c907-dd1e-443f-bb20-9cc90aef11a2.png)

### MSA 아키텍처

- 기능 별로 (도메인 별로) 서버가 나뉘어 있고, 각자의 데이터베이스를 사용하며, API를 이용해 통신

![image](https://user-images.githubusercontent.com/40031858/224593152-de3dffb2-2042-43b1-b4ea-119bab10f7b0.png)

### MSA로 얻으려는 것

- 모듈성(높은 응집도, 낮은 결합도)
- 서비스 별로 독립적인 개발과 배포가 가능
- 서비스(코드) 크기가 작아져 이해가 쉽고 유지보수가 용이함
- 더 빠른 개발, 테스트, 배포
- 확장성(서비스 별로 개별 확장이 가능)
- 결함 격리 (일부 서비스 실패가 전체 시스템 실패로 이어지지 않음)

### MSA의 단점

- 분산 시스템의 단점을 그대로 가짐
- 통합 테스트의 어려움
- 모니터링과 디버깅의 복잡도 증가
- 트랜잭션 관리의 어려움
- 서비스간 통신 구조에 대한 고민이 필요

=> 동기 vs 비동기 , 프로토콜, 통신 브로커 사용 등

## `Event-Driven` 아키텍처란?

- 분산 시스템에서의 통신 방식을 정의한 아키텍처로, 이벤트의 생성/소비 구조로 통신이 이루어짐
- 각 서비스들은 이벤트 저장소인 Event-broker와의 의존성만 가짐

### Event-Driven 아키텍처의 모습

- 각 서버들은 Event Broker에 이벤트를 생산/소비함으로써 통신 

![image](https://user-images.githubusercontent.com/40031858/224594363-e0c7334e-54f6-4ec3-a6c8-0d40e7cc1175.png)

### Event-Driven 아키텍처의 장점

- 이벤트 생산자/소비자 간의 결합도가 낮아짐(공통적인 Event-broker에 대한 결합만 있음)
- 생산자/소비자의 유연한 변경(서버 추가, 삭제 시에 다른 서버를 변경할 필요가 적어짐)
- 장애 탄력성(이벤트를 소비할 일부 서비스에 장애가 발생해도 이벤트는 저장되고 이후에 처리됨)

### Event-Driven 아키텍처의 단점

- 시스템의 예측가능성이 떨어짐(느슨하게 연결된 상호작용에서 기인함)
- 테스트의 어려움
- 장애 추적의 어려움

---

## Redis Streams의 이해

### Redis Streams

- append-only log를 구현한 자료구조
- 하나의 key로 식별되는 하나의 stream에 엔트리가 계속 추가되는 구조
- 하나의 엔트리는 entry ID + (key-value 리스트)로 구성
- 추가된 데이터는 사용자가 삭제하지 않는 한 지워지지 않음

![image](https://user-images.githubusercontent.com/40031858/224595567-c61d1dd6-0f88-4a5c-b9a9-0985514dd6f0.png)

### Redis Streams의 활용

- 센서 모니터링(지속적으로 변하는 데이터인 시간 별 날씨 수집 등)
- 유저별 알림 데이터 저장
- 이벤트 저장소

### Redis Streams의 명령어 : 엔트리 추가

`XADD` : 특정 key의 stream에 엔트리를 추가한다 (해당 key에 stream이 없으면 생성)

    XADD [key] [id] [field-value]

예제) user-notifications라는 stream에 1개의 엔트리를 추가하며 2개의 field-value 쌍을 넣음

![image](https://user-images.githubusercontent.com/40031858/224606795-145c1194-1b15-42bb-95f5-a1940f59918e.png)

### Redis Streams의 명령어 : 엔트리 읽기 (범위 기반)

`XRANGE` : 특정 ID 범위의 엔트리를 반환한다

    XRANGE [key] [start] [end]

예제) user-notifications의 모든 범위를 조회

![image](https://user-images.githubusercontent.com/40031858/224607050-a091017f-beb9-4226-933c-350f1f352268.png)

### Redis Streams의 명령어: 엔트리 일기(Offset 기반) -1

`XREAD` : 한 개 이상의 key에 대해 특정 ID 이후의 엔트리를 반환한다 (동기 수행 가능)

    XREAD BLOCK [milliseconds] STREAMS [key] [id]

예제) user-notifications의 0보다 큰 ID 조회

![image](https://user-images.githubusercontent.com/40031858/224607214-3f73bea6-2b71-4b33-81fd-e48beca506fa.png)

### Redis Streams의 명령어 : 엔트리 일기(Offset 기반) -2

예제) user-notifications에서 새로 들어오는 엔트리를 동기 방식으로 조회

![image](https://user-images.githubusercontent.com/40031858/224607499-9b40b724-1912-4395-ba09-ab8afcd281ce.png)

=> 앞으로 들어올 데이터를 동기 방식으로 조회하여 event listener와 같은 방식으로 사용 가능

### Redis Streams의 명령어 : Consumer Group

- 한 stream을 여러 consumer가 분산 처리할 수 있는 방식
- 하나의 그룹에 속한 consumer는 서로 다른 엔트리들을 조회하게 됨

![image](https://user-images.githubusercontent.com/40031858/224608119-48ade7fc-34f4-4a3e-a39c-c66efe3000bc.png)

### Redis Streams의 명령어: Consumer Group - 1

`XGROUP CREATE` : consumer group을 생성

    XGROUP CREATE [key] [group name] [id]

예제) user-notifications에 group1이라는 consumer group을 생성

![image](https://user-images.githubusercontent.com/40031858/224608371-108c6ff4-7eaf-42cc-adb7-79c770db718f.png)

### Redis Streams의 명령어 : Consumer Group -2

`XREADGROUP` : 특정 key의 stream을 조회하되, 특정 consumer group에 속한 consumer로 읽음

    XREADGROUP GROUP [group name] [consumer name] COUNT [count] STREAMS [key] [id]

예제) user-notifications에서 group1 그룹으로 2개의 컨슈머가 각각 1개씩 조회

![image](https://user-images.githubusercontent.com/40031858/224609257-11c63b4f-35ad-4f55-a175-1d4f0196af31.png)

=> id에 ">"를 지정하면 아직 소비되지 않은 메시지를 가져오게 된다

<img width="1044" alt="image" src="https://user-images.githubusercontent.com/40031858/224609889-ee7962cc-23d8-44e1-a228-f73f6286ede5.png">

<img width="895" alt="image" src="https://user-images.githubusercontent.com/40031858/224609988-5e6c1a17-7142-476b-b4d9-44028d2c9cc4.png">

