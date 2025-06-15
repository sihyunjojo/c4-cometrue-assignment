# 🔄 Logstash 수정 사항 반영 가이드

## ✅ 왜 수정 후 재빌드 & 재시작이 필요한가요?

Logstash는 Docker 이미지에 포함된 설정(`logstash.conf`, `Dockerfile_LS`, `.jar` 파일 등)을 기반으로 실행됩니다. 하지만 다음과 같은 변경 사항은 **기존 컨테이너에 자동 반영되지 않습니다**:

- `logstash.conf` 파일 내용 변경
- JDBC 드라이버 등 `.jar` 파일 교체
- `Dockerfile_LS` 내 의존성 추가 또는 환경 구성 변경
- Pipeline 설정 추가 또는 제거

**즉, 설정만 바꾸고 재빌드하지 않으면, 이전 설정으로 Logstash가 실행됩니다.**

---

## 🛠 수정 후 반드시 실행해야 할 명령어

```bash
# 1. 이미지 캐시를 무시하고 새로 빌드합니다
docker compose build --no-cache logstash01

# 2. 새 이미지로 컨테이너를 재시작합니다
docker compose up -d logstash01

# 3. 로그를 확인하여 정상 기동 여부를 확인합니다
docker logs -f logstash01
```


---


# 🛠️ 로컬 서버를 Docker 컨테이너(Logstash 등)에 연결할 때 주의할 점

Spring Boot와 같은 로컬 애플리케이션이 Docker로 실행 중인 Logstash, Redis, Kafka 등에 접근할 때는 **"localhost"로 접근하면 안 됩니다**. 로컬 환경과 Docker 환경은 **서로 다른 네트워크 공간에 있기 때문**입니다. 아래 절차를 따르면 문제 없이 연결할 수 있습니다.

---

## ✅ 문제 상황: 로컬에서 `localhost:5044`로 접근할 경우

Spring Boot 설정에서 다음과 같이 `localhost`를 설정했다면:

```xml
<destination>localhost:5044</destination>
```

이 설정은 **로컬 머신의 5044 포트를 바라보게 됩니다.** 그러나 Logstash는 **Docker 컨테이너 내부에서 실행 중**이기 때문에 **로컬의 localhost가 아닌 컨테이너 내부 IP로 접근해야 합니다.**

---

## ✅ 해결 절차

### 1. Docker Compose에 명시된 네트워크 확인

`docker-compose.yml` 파일 상단에 다음과 같은 네트워크 정의가 있다면:

```yaml
networks:
  default:
    name: elastic-network
    external: false
```

→ 이 네트워크 이름(`elastic-network`)을 기억합니다.

---

### 2. Logstash 컨테이너의 IP 주소 확인

아래 명령어로 **Logstash 컨테이너가 해당 네트워크에서 사용하는 IP**를 조회합니다:

```bash
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' logstash01
```

예시 결과:

```
172.19.0.4
```

---

### 3. 로컬 애플리케이션 설정에서 destination 수정

`logback-spring.xml` 등의 설정 파일에서 **`localhost` 대신 위에서 확인한 컨테이너 IP를 사용**합니다:

```xml
<destination>172.19.0.4:5044</destination>
```

또는 **호스트 네트워크로의 포트 바인딩이 되어 있는 경우**, `127.0.0.1` 대신 `host.docker.internal` 또는 `bridge` IP 등을 사용합니다 (Mac/Windows 한정).

---

### 4. Logstash 포트 확인

Logstash가 `5044` 포트를 제대로 수신하고 있는지 Logstash 설정 (`logstash.conf`)에서 다음 항목을 확인합니다:

```conf
input {
  tcp {
    port => 5044
    codec => json
  }
}
```

포트가 다르다면, `.xml` 또는 애플리케이션 설정에서 동일한 포트로 맞춰야 합니다.

---

### 5. 연결 여부 확인

다음 두 가지 방법 중 하나로 Logstash와의 연결 여부를 확인합니다:

- `docker logs logstash01` 명령어로 Logstash 로그에 접속 메시지 확인  
  → `Successfully connected` 로그가 출력되어야 정상 연결

- Logstash에서 수신한 로그가 Elasticsearch에 적재되거나 stdout으로 찍히는지 확인

---

## 🧩 요약

| 구분 | 설명 |
|------|------|
| ❌ 잘못된 접근 | 로컬 앱에서 `localhost:포트`로 접근 |
| ✅ 올바른 접근 | 로컬 앱에서 `docker inspect`로 확인한 IP 사용 |
| 필요 조치 | Docker 네트워크 확인 → 컨테이너 IP 조회 → 설정 반영 |
| 연결 검증 | `docker logs` 또는 Kibana / Elasticsearch 인덱스 확인 |

---

이 과정을 통해 **로컬에서 실행 중인 Spring Boot 서버와 Docker 내부 Logstash 간의 네트워크 연결 문제를 안전하게 해결할 수 있습니다.**
