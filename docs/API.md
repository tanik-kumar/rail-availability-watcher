# API Documentation

Base URL: `http://localhost:8080`

## Watchers

- `POST /api/watchers`
- `PUT /api/watchers/{id}`
- `GET /api/watchers`
- `GET /api/watchers/{id}`
- `PATCH /api/watchers/{id}/pause`
- `PATCH /api/watchers/{id}/resume`
- `POST /api/watchers/{id}/check-now`
- `DELETE /api/watchers/{id}`
- `GET /api/watchers/{id}/history`
- `GET /api/watchers/{id}/latest-status`
- `GET /api/watchers/{id}/alerts`
- `GET /api/watchers/{id}/errors`

## Provider Health

- `GET /api/health/provider`

## Example Create Request

```json
{
  "providerType": "MOCK",
  "trainNumber": "12004",
  "journeyDate": "2026-04-02",
  "sourceStation": "NDLS",
  "destinationStation": "LKO",
  "boardingStation": "CNB",
  "quota": "GN",
  "travelClass": "3A",
  "originDepartureTime": "2026-04-02T06:10:00",
  "boardingDepartureTime": "2026-04-02T10:25:00",
  "quietHoursStart": "00:30:00",
  "quietHoursEnd": "05:00:00",
  "notifyTelegram": true,
  "notifyEmail": false,
  "notifyWebhook": false,
  "note": "Remote boarding watcher"
}
```

## Example cURL Commands

```bash
curl -X POST http://localhost:8080/api/watchers \
  -H 'Content-Type: application/json' \
  -d @docs/examples/watcher-1.json
```

```bash
curl http://localhost:8080/api/watchers
```

```bash
curl -X POST http://localhost:8080/api/watchers/{id}/check-now
```
