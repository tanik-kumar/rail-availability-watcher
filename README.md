# Indian Railways Current Availability Watcher

Production-style personal watcher for Indian Railways current booking availability. The app monitors availability status for specific train/date/class/station combinations and sends alerts when booking opens or seats become available. It does not auto-book, log in, pay, bypass CAPTCHA, or interact with IRCTC protected flows.

## What it does

- Monitors multiple watch jobs for train number, journey date, source, destination, boarding station, class, and quota.
- Uses smart polling that ramps up near chart preparation and remote-location chart windows.
- Persists status history, alert history, and provider errors in PostgreSQL.
- Sends alerts through Telegram, email, and optional webhook.
- Exposes REST APIs and a small Thymeleaf dashboard.
- Supports alias mapping for trains and stations.

## Compliance warning

Use this project only with a lawful and permitted data source. You are responsible for complying with Indian Railways, IRCTC, and any third-party provider terms. Do not use this project to automate login, payment, OTP, CAPTCHA, booking, or bot-protection bypass.

## Stack

- Java 21
- Spring Boot 4.0.3
- Maven
- PostgreSQL
- Redis optional
- Thymeleaf UI
- Docker / Docker Compose

## Local run on Mac

1. Copy `.env.example` to `.env`.
2. Start PostgreSQL with Docker Compose:
   ```bash
   docker compose up -d postgres
   ```
3. If you want Redis-backed de-duplication:
   ```bash
   docker compose --profile redis up -d redis
   ```
4. Start the app:
   ```bash
   chmod +x mvnw
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```
5. Open:
   - Dashboard: `http://localhost:8080/dashboard`
   - Actuator: `http://localhost:8080/actuator/health`

## Linux / VPS run

1. Install Docker and Docker Compose plugin.
2. Copy `.env.example` to `.env` and update DB, SMTP, Telegram, and provider settings.
3. Launch:
   ```bash
   docker compose up --build -d
   ```
4. Optional Redis:
   ```bash
   docker compose --profile redis up --build -d
   ```

## Telegram alerts

1. Create a bot with BotFather.
2. Put the bot token in `TELEGRAM_BOT_TOKEN`.
3. Send one message to the bot from your Telegram account.
4. Obtain your chat id and set `TELEGRAM_CHAT_ID`.
5. Enable `notifyTelegram=true` on each watcher you want routed to Telegram.

The app sends alerts through `POST https://api.telegram.org/bot<token>/sendMessage` and does not perform any Telegram polling.

## Where to plug in the real provider

- Interface: `src/main/java/com/example/railwatcher/provider/AvailabilityProvider.java`
- Placeholder implementation: `src/main/java/com/example/railwatcher/provider/HttpPlaceholderAvailabilityProvider.java`
- Registry: `src/main/java/com/example/railwatcher/provider/AvailabilityProviderRegistry.java`

Replace the placeholder HTTP parsing with a compliant provider contract that returns:

- normalized availability status
- raw status text
- booking-open signal
- chart-prepared signal if available
- available seats if available

## Smart polling behavior

- Far from charting: 20 minutes by default
- Within 2 hours of expected charting: 5 minutes
- Within 30 minutes of expected charting: 45 seconds
- Quiet hours: 1 hour unless the watcher is inside the burst window
- Error backoff: exponential from 1 minute to 30 minutes
- Expiry: 15 minutes after boarding departure by default

## Example watcher set

Three sample watcher payloads are included:

- `docs/examples/watcher-1.json`
- `docs/examples/watcher-2.json`
- `docs/examples/watcher-3.json`

## Sample cURL

```bash
curl -X POST http://localhost:8080/api/watchers \
  -H 'Content-Type: application/json' \
  -d @docs/examples/watcher-1.json
```

```bash
curl http://localhost:8080/api/watchers
```

```bash
curl -X POST http://localhost:8080/api/watchers/<watcher-id>/check-now
```

## API and docs

- API reference: `docs/API.md`
- ER diagram: `docs/ER_DIAGRAM.md`
- Phased implementation plan: `docs/IMPLEMENTATION_PLAN.md`
- Assumptions and limitations: `docs/ASSUMPTIONS_AND_LIMITATIONS.md`
- Future enhancements: `docs/FUTURE_ENHANCEMENTS.md`
- Codex session resume notes: `docs/CODEX_RESUME.md`

## Legal / compliance boundaries to respect

- Do not integrate IRCTC login automation.
- Do not automate booking, payment, OTP, or CAPTCHA.
- Do not bypass bot-protection, fingerprinting, or rate limits.
- Do not scrape aggressively.
- Use only official, public, or otherwise permitted endpoints.
- Review the data source terms before switching from the mock provider to a live provider.
