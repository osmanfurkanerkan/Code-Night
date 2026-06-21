# Game+ Gamification & Rule Engine 🏆
**Turkcell Code Night Hackathon — 2nd Place Winner** `[ ⏱️ BUILT FROM SCRATCH IN 10 HOURS ]` `[ ☕ JAVA 21 / SPRING BOOT 3 ]`

An event-driven, deterministic gamification engine built for Turkcell's cloud gaming platform (*Game+*). Designed to ingest raw daily telemetry, resolve complex quest conditions via dynamic rule strategies, and maintain an immutable ledger of user points.

---

## ⚡ The 10-Hour "Ruthless Pragmatism" Philosophy

In a strict 10-hour hackathon timebox, over-engineering kills the MVP. This repository demonstrates high-speed, pragmatic engineering:

1. **Zero-Build Native Frontend:** Bypassed Node.js/React compilation overhead entirely. Built a single-page Vanilla JS + CSS3 responsive dashboard served directly from Spring MVC's native `/static` asset directory (`http://localhost:8080/dashboard.html`).
2. **Embedded Flyway Migrations:** Automated DDL executions so the hackathon jury could pull the image, spin up PostgreSQL, and fire the test suite with zero manual `CREATE TABLE` friction.

---

## 🏛️ Architectural Masterpieces

### 1. The Append-Only Ledger Pattern (`/model/PointsLedgerEntry.java`)
To prevent race conditions and un-auditable database states, user points are **never mutated directly** via `UPDATE` queries. Every point gain is registered as an immutable delta entry (`+150`). Total user balances are strictly derived via `SUM(points_delta)`. 
*(This guarantees 100% financial auditability and mimics modern Fintech ledger architectures).*

### 2. Strategy-Driven Rule Engine (`/service/strategy`)
Quest criteria are expressed as raw strings inside the seed files (e.g., `login_streak_days >= 3`). The `ConditionEvaluator` dynamically parses these expressions at runtime using RegEx and passes the payload to concrete implementations of `ConditionStrategy`, strictly adhering to the **Open/Closed Principle (OCP)**.

### 3. ACID Conflict Resolution
When a telemetry batch triggers multiple conflicting quests for a single user on the same day, the engine passes the array through a deterministic priority matrix, awards the highest-tier quest, and flags the inferiors as `suppressed` within a single transaction roll.

---

## 🚀 Running the Engine Locally

### Prerequisites
* Java 21+
* A running **PostgreSQL** instance at `localhost:5432/gameplus` *(User: `gameplus_user`, Pass: `gameplus_pass`)*

```bash
# 1. Boot the application (Flyway automatically establishes the schema)
./mvnw spring-boot:run
Once initialized, open your browser and navigate to the monitoring hub:

👉 http://localhost:8080/dashboard.html

Triggering the Simulation via CLI
To push a mock daily telemetry dump into the State Machine:

Bash
# Ingest raw CSV datasets into the staging tables
curl -X POST "http://localhost:8080/api/import"

# Execute the Rule Engine for a target simulation date
curl -X GET "http://localhost:8080/api/process?date=2026-03-12"
📦 System Telemetry Flow
Plaintext
[Raw Telemetry CSVs] ──► (ETL Parser) ──► [User State Matrix] ──► (Strategy Rule Engine) ──► [Append-Only Ledger]
