# AGENTS.md

## Git

- После каждого изменения в проекте **добавляй файлы в git** (`git add -A`), чтобы новые и
  изменённые файлы не оставались untracked/красными в IDE.
- **Не коммить и не пушить без явной просьбы.** `git add` — можно и нужно, `git commit`/`git push` —
  только по прямой команде пользователя.
- Перед стейджингом смотри `git status --short`; в коммит/стейдж не должны попадать секреты,
  `target/`, `data/`, `.idea/` (они уже в `.gitignore`).
- Текущая рабочая ветка — `dev` (репозиторий `https://github.com/lDummyl/event-driven.git`).

## Сборка и тесты

Maven не в PATH, использовать полный путь `C:\tools\apache-maven-3.9.6\bin\mvn.cmd`.

```powershell
# тесты
& "C:\tools\apache-maven-3.9.6\bin\mvn.cmd" -B -ntp test

# запуск приложения (http://localhost:8080)
& "C:\tools\apache-maven-3.9.6\bin\mvn.cmd" -B -ntp spring-boot:run

# сборка jar
& "C:\tools\apache-maven-3.9.6\bin\mvn.cmd" -B -ntp -DskipTests package
```

После завершения задачи обязательно прогонять `mvn test`, линтер не настроен.

## Проект

Event-sourcing демо: вечный append-only NDJSON-лог + пересобираемый projection в in-memory H2.

- `core/EventRouter` — прокси: `dispatch` (online, текущие правила), `appendRaw` (исторический факт
  в обход валидации), `rebuild` (маршрутизация по `EventKind`). Во время rebuild режим `Mode.REBUILD`
  (сайд-эффекты обязаны молчать).
- `EventKind`: `RECALCULABLE` (пересчитывается текущей логикой при rebuild) vs `FROZEN`
  (применяется как исторический факт, без повторной валидации).
- `core/EventMetadata` — конверт факта: `id`, `correlationId`, `causationId`, `aggregateId`,
  `occurredAt`, `schemaVersion`.
- `core/EventStore` — порт над логом (`EventLog` — файловая реализация).
- Новый ивент = record implements `DomainEvent` + один `@Component EventHandler`, без switch.
- Конфиг: `app.log.path`, `app.log.wipe-on-start`, `app.rating.points-per-100`.
- Модель, инварианты и «швы роста» — в `docs/architecture.md`.
