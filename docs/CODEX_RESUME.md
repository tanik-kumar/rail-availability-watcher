# Codex Resume Notes

Last updated: 2026-03-30

## Project State

This repository contains a production-style Spring Boot application for a personal Indian Railways current availability watcher. The application is intentionally limited to monitoring and alerting. It does not perform booking, login automation, CAPTCHA handling, payment automation, OTP handling, or any other restricted IRCTC flow.

Core implementation is already in place:

- Spring Boot 4.0.3, Java 21, Maven
- PostgreSQL persistence with Flyway migration
- Optional Redis-backed alert de-duplication
- REST APIs for watcher lifecycle, history, alerts, and provider health
- Thymeleaf dashboard UI
- Provider abstraction with mock provider and placeholder HTTP provider
- Alerting via Telegram, email, and optional webhook
- Smart polling, retry, timeout, circuit-breaker-style behavior, and rate limiting
- Structured application configuration for local/dev/prod
- Dockerfile and `docker-compose.yml`

## Verified Build Status

The following commands were run successfully on 2026-03-30 in `/Users/tanik/Projects/rail-availability-watcher`:

```bash
./mvnw -B test
./mvnw -B -DskipTests package
```

Results:

- Tests passed: `Tests run: 8, Failures: 0, Errors: 0, Skipped: 0`
- Packaged artifact:
  - `target/rail-availability-watcher-0.0.1-SNAPSHOT.jar`
  - `target/rail-availability-watcher-0.0.1-SNAPSHOT.jar.original`

## Important Constraints

Keep these boundaries intact in future changes:

- Do not add auto-booking.
- Do not add IRCTC login automation.
- Do not add CAPTCHA, OTP, payment, or bot-protection bypass.
- Do not implement aggressive scraping.
- Preserve the provider abstraction so a compliant data source can be swapped in safely.

## Key Files

Application and architecture:

- `src/main/java/com/example/railwatcher/RailAvailabilityWatcherApplication.java`
- `src/main/java/com/example/railwatcher/service/AvailabilityPollingService.java`
- `src/main/java/com/example/railwatcher/service/AlertDecisionEngine.java`
- `src/main/java/com/example/railwatcher/service/ChartingWindowEstimator.java`
- `src/main/java/com/example/railwatcher/scheduler/WatchJobScheduler.java`
- `src/main/java/com/example/railwatcher/provider/AvailabilityProvider.java`
- `src/main/java/com/example/railwatcher/provider/MockAvailabilityProvider.java`
- `src/main/java/com/example/railwatcher/provider/HttpPlaceholderAvailabilityProvider.java`

API and UI:

- `src/main/java/com/example/railwatcher/api/WatchController.java`
- `src/main/java/com/example/railwatcher/api/ProviderHealthController.java`
- `src/main/java/com/example/railwatcher/ui/DashboardController.java`
- `src/main/resources/templates/dashboard.html`

Persistence and migrations:

- `src/main/resources/db/migration/V1__initial_schema.sql`
- `src/main/java/com/example/railwatcher/persistence/entity/`
- `src/main/java/com/example/railwatcher/persistence/repository/`

Docs:

- `README.md`
- `docs/API.md`
- `docs/ER_DIAGRAM.md`
- `docs/IMPLEMENTATION_PLAN.md`
- `docs/ASSUMPTIONS_AND_LIMITATIONS.md`
- `docs/FUTURE_ENHANCEMENTS.md`
- `docs/examples/`

## Git and Repository Status

- Local folder is not yet initialized as a Git repository.
- Intended GitHub repository URL supplied by the user:
  - `https://github.com/tanik-kumar/rail-availability-watcher`
- Empty GitHub scaffolding directories were created and are ready to populate if needed:
  - `.github/ISSUE_TEMPLATE/`
  - `.github/workflows/`

## Pending Work Identified

The next Codex session had already identified a repo-hardening pass as the next likely step. Planned additions:

1. Repository hygiene files:
   - `.gitignore`
   - `.gitattributes`
   - `.editorconfig`
2. Community and publishing files:
   - `LICENSE`
   - `CONTRIBUTING.md`
   - `SECURITY.md`
   - `SUPPORT.md`
   - `CHANGELOG.md`
   - `CODE_OF_CONDUCT.md`
3. GitHub metadata:
   - issue templates
   - pull request template
   - `CODEOWNERS`
   - CI workflow
   - Dependabot config
4. README refresh:
   - badges
   - repository layout
   - contribution and security links
   - GitHub-friendly structure

## Suggested Resume Checklist

If resuming work in a future session:

1. Read `README.md` and this file first.
2. Confirm whether the next goal is:
   - repo publication polish
   - git initialization and remote wiring
   - live provider integration
   - UI/docs polish
3. If the goal is repo publication, start with the pending repo-hardening items above.
4. If the goal is provider integration, modify only the provider layer and keep compliance boundaries explicit.
5. Re-run:

```bash
./mvnw -B test
./mvnw -B -DskipTests package
```

## Notes for Future Sessions

- The mock provider is the safe default for development and tests.
- The HTTP provider is intentionally a placeholder and should only be connected to a lawful, permitted data source.
- The UI currently ships as a single dashboard page with add/edit controls, status display, theme toggle, and watcher actions.
- README already documents local Mac setup, Linux/VPS setup, Telegram configuration, provider extension points, and legal boundaries.
