# Real-Time Log Aggregation & Alerting System

A Kafka-based log processing pipeline that ingests logs from multiple services, detects error spikes in real time, and fires alerts. Built with Spring Boot microservices.

---

## Architecture

```
log-ingestion-service  →  logs-topic  →  log-processor-service
                                                  │
                                    ┌─────────────┴──────────────┐
                                    ▼                            ▼
                              alerts-topic                  logs-dlq
                                    │
                                    ▼
                            alert-service
```

Three services, three Kafka topics. Ingestion is fire-and-forget via REST; processing handles all the business logic; the alert service is intentionally thin.

---

## Services

**log-ingestion-service**  
Exposes `POST /logs`. Validates the payload and publishes to `logs-topic`. Nothing else.

**log-processor-service**  
The core of the system. Consumes from `logs-topic`, runs a sliding window per service to detect error spikes, publishes alerts to `alerts-topic`, and routes bad messages to `logs-dlq`. Alert thresholds are pulled from MySQL (cached via `@Cacheable`) so you can tune them without redeploying.

**alert-service**  
Consumes from `alerts-topic`. Currently logs alerts — designed to be extended for email/Slack/PagerDuty.

---

## How the Sliding Window Works

Each service gets its own in-memory queue of ERROR log timestamps. On every incoming log:

1. Drop timestamps older than `time_window_seconds`
2. If the remaining count exceeds `error_threshold` → publish an alert

Thresholds are per-service and stored in MySQL, so `payment-service` can have a tighter threshold than `auth-service`.

```json
// Alert event shape
{
  "serviceName": "payment-service",
  "alertType": "ERROR_SPIKE",
  "count": 25,
  "timeWindow": "60s",
  "timestamp": 1710000000000
}
```

---

## Kafka Topics

| Topic | Purpose |
|---|---|
| `logs-topic` | Incoming log events |
| `alerts-topic` | Generated alerts |
| `logs-dlq` | Invalid / unprocessable messages |

Partitioned by `serviceName` to preserve ordering per service.

Consumer groups: `log-processor-group`, `alert-consumer-group`.

---

## Database Schema

```sql
CREATE TABLE alert_config (
  service_name        VARCHAR(100) PRIMARY KEY,
  error_threshold     INT NOT NULL,
  time_window_seconds INT NOT NULL
);
```

---

## Running Locally

**Prerequisites:** Kafka + Zookeeper running, MySQL up with `alert_config` populated.

```bash
# Create topics
kafka-topics.sh --create --topic logs-topic --bootstrap-server localhost:9092
kafka-topics.sh --create --topic alerts-topic --bootstrap-server localhost:9092
kafka-topics.sh --create --topic logs-dlq --bootstrap-server localhost:9092

# Start services (in any order)
./mvnw spring-boot:run -pl log-ingestion-service
./mvnw spring-boot:run -pl log-processor-service
./mvnw spring-boot:run -pl alert-service
```

**Quick test:**

```bash
# Normal log — no alert
curl -X POST http://localhost:8080/logs \
  -H "Content-Type: application/json" \
  -d '{"serviceName":"payment-service","logLevel":"INFO","message":"ok","timestamp":1710000000000}'

# Spam errors to trigger alert
for i in {1..25}; do
  curl -X POST http://localhost:8080/logs \
    -H "Content-Type: application/json" \
    -d '{"serviceName":"payment-service","logLevel":"ERROR","message":"Payment failed","timestamp":1710000000000}'
done

# Invalid payload → goes to DLQ
curl -X POST http://localhost:8080/logs -d 'not json'
```

---

## Tech Stack

- Java 17, Spring Boot, Spring Kafka
- Apache Kafka
- MySQL
- Spring Cache (`@Cacheable`)

---

## What's Missing / Roadmap

- Retry logic before messages hit the DLQ
- Persist alerts to DB + expose a query API
- Swap in-memory cache for Redis (needed once you run multiple processor instances)
- Prometheus metrics + Grafana dashboard
- Proper Kafka Streams DSL instead of manual window logic
