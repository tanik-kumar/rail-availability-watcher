# Assumptions and Limitations

- The app is designed to monitor availability only. It does not book tickets, log in to IRCTC, or interact with payment, CAPTCHA, or OTP systems.
- Smart charting heuristics require accurate departure times. For intermediate boarding stations, provide `boardingDepartureTime` for better remote-chart detection.
- The bundled HTTP provider is intentionally generic. You must connect it only to a lawful, permitted data source.
- Scheduler locking is single-node in-memory. For multi-instance deployment, add distributed locking or database-backed job leasing.
- Redis is optional and used only for alert de-duplication. Without Redis, the app still works with DB plus in-memory de-duplication.
- The default UI is intentionally small and admin-focused rather than a full end-user portal.
