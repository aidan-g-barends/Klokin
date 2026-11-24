<p align="center">
  <img src="docs/klokin-logo.svg" alt="Klokin" width="320">
</p>

<p align="center">
  <strong>Attendance and leave that runs itself.</strong><br>
  Built for South African small businesses with staff in the shop and on client sites.
</p>

<p align="center">
  <a href="https://github.com/aidan-g-barends/klokin/actions/workflows/backend.yml"><img src="https://github.com/aidan-g-barends/klokin/actions/workflows/backend.yml/badge.svg" alt="Backend CI"></a>
</p>

## The problem

Most small businesses still track attendance on paper, in Excel or over WhatsApp. Staff forget to sign in, fill in a whole month from memory, and the boss spends month end adding up hours, overtime and leave by hand.

## How Klokin solves it

- **Automatic clock-in:** the employee's own phone clocks them in when it sees the shop Wi-Fi or enters the shop's location circle.
- **Works offline:** every clock-in is saved on the phone first and synced later, so staff without data aren't penalised. Syncing twice never saves anything twice.
- **Changing client sites:** installers tap "I'm on site" and pick or add the client. Travel and site time count as work.
- **Hours that add up themselves:** a GitHub-style heatmap of the month with a running total ("201 hours this month"), lunch rules and overtime worked out automatically.
- **Live feed for the boss:** who clocked in, when and where, with flags for anything that needs a look (location off, phone clock changed, clocked in away from work).

## Tech stack

| Part | Tech |
| --- | --- |
| Backend | Java 21, Spring Boot 4, Spring Data JPA |
| Database | H2 in development, PostgreSQL in production |
| Mobile app | React Native with Expo (coming next) |
| Boss dashboard | React (coming later) |
| CI | GitHub Actions runs the backend tests on every push |

## Project structure

```
klokin/
├── backend/     Spring Boot API
├── mobile/      Expo mobile app (coming soon)
├── dashboard/   React boss dashboard (coming soon)
└── docs/        logo, brand assets and user stories
```

The backend follows a domain-driven layout: entities with builders, factories that validate, repositories, services behind interfaces, and thin REST controllers.

## Run the backend

You need Java 21. No database install: the dev profile uses an in-memory H2 database and seeds a demo company with six weeks of realistic clock-ins.

```bash
cd backend
./mvnw spring-boot:run
```

Then open `http://localhost:8080/api/employees/1/attendance` to see a month of hours, or use `backend/klokin.http` to try every endpoint from IntelliJ.

Run the tests with `./mvnw test`.

## API overview

| Method | Endpoint | What it does |
| --- | --- | --- |
| GET | `/api/health` | Is the API up? |
| POST, GET | `/api/companies` | Create and list companies |
| POST, GET | `/api/companies/{id}/employees` | Add and list staff |
| GET, PUT | `/api/employees/{id}` | Read and update an employee |
| POST | `/api/employees/{id}/deactivate` | Deactivate (history is kept) |
| POST, GET | `/api/companies/{id}/locations` | The shop's geofence and Wi-Fi |
| POST, GET | `/api/companies/{id}/clients` | Pick or add a client site |
| POST | `/api/sync/events` | Offline-safe sync of clock-ins from a phone |
| GET | `/api/companies/{id}/feed` | Live feed of clock-ins for the boss |
| GET | `/api/employees/{id}/attendance?month=2026-11` | Monthly hours, overtime and heatmap data |

## Status

In development. Portfolio project by Aidan Barends.
