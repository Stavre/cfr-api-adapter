# Architecture

## Layered Architecture Overview

```
HTTP Request
     │
     ▼
┌─────────────┐
│  Controller │  REST endpoints (Spring MVC @RestController)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Service   │  Business logic, delay calculation, merging, static JSON loading
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Repository  │  Orchestrates proxy + scraper per data type
└──────┬──────┘
       │
       ├──────────────────────────────────┐
       ▼                                  ▼
┌─────────────┐                  ┌─────────────────┐
│    Proxy    │                  │     Scraper     │
│  (Feign)    │ ── HTML resp ──▶ │  (JSoup parse)  │
└─────────────┘                  └────────┬────────┘
                                          │ raw DTO
                                          ▼
                                 ┌─────────────────┐
                                 │     Adapter     │  validate, transform, enrich
                                 └────────┬────────┘
                                          │ enriched DTO
                                          ▼
                                   response DTO
```

## Layer Descriptions

### Controller (`controller/`)

Four `@RestController` classes, each mapping a subset of routes:

| Class | Routes |
|---|---|
| `TrainController` | `/train/{trainNumber}`, `/train/{trainId}/delay`, `/train/all` |
| `StationController` | `/station/{stationName}`, `/station/all` |
| `StationArrivalsController` | `/station/arrivals/delayed/{stationName}`, `/station/arrivals/delayed/total/{stationName}` |
| `StationDeparturesController` | `/station/departures/delayed/{stationName}`, `/station/departures/delayed/total/{stationName}` |

Controllers receive optional `date` query parameters and delegate all logic to services.

### Service (`service/`)

| Class | Responsibilities |
|---|---|
| `TrainService` | Loads static train list from JSON; calls repository for timetable and delay data; supports branch-level delay filtering by station |
| `StationService` | Loads static station list from JSON; calls repository; merges arrival and departure records for the same train; calculates delayed-only subsets and total delay aggregations |

Services contain no HTTP or parsing logic — that is pushed down to repositories and below.

### Repository (`repository/`)

| Class | Responsibilities |
|---|---|
| `TrainRepository` | Calls `TrainTimeTableProxy` for the two-step GET+POST scrape; passes HTML to train scrapers; returns `TrainDto` |
| `StationRepository` | Calls `TrainStationProxy` for the two-step GET+POST scrape; passes HTML to station scrapers; returns lists of `TrainArrivalDepartureDto` |

### Proxy (`proxy/`)

OpenFeign `@FeignClient` interfaces that make HTTP calls to the CFR website. Each proxy handles HTTP request initiation and response retrieval — it is not aware of the data inside the HTML, only the transport:

| Interface | Purpose |
|---|---|
| `TrainTimeTableProxy` | GET initial train page (token extraction) + POST for timetable HTML |
| `TrainStationProxy` | GET initial station page (token extraction) + POST for arrivals/departures HTML |

Both proxies are configured with the `cfr.base-url` base URL and the custom `FeignConfig` (full logging).

### Two-Step Scraping Flow

CFR's website uses hidden form tokens and CSRF protection. Every data fetch requires two HTTP requests:

```
1. GET  /[page]                       → receive HTML with hidden form fields
2. Extract tokens (form fields, CSRF) → build POST request body
3. POST /[page]                       → receive HTML table with actual data
4. Parse HTML table                   → produce raw DTO
```

This flow is implemented in the repository layer, coordinated between the proxy (HTTP) and scrapers (parsing).

### Scraper (`scraper/`)

Organised into two sub-packages:

**`scraper/train/`** (5 scrapers):
- Token and hidden form field extraction from the initial GET response (these are page fragments, not data)
- Train metadata parsing (number, category, operator)
- Stop list parsing per branch
- Branch detection

**`scraper/station/`** (5 scrapers):
- Token and hidden form field extraction from the initial GET response
- Arrival row parsing
- Departure row parsing
- Train metadata parsing for station context

Scrapers use JSoup selectors and `ScraperUtils` helpers. They produce raw `scraper/` DTOs.

### Adapter (`adapter/`)

Seven adapter classes that apply extraction rules, validate, and enrich raw scraped DTOs into enriched or response DTOs:

| Class | Transformation |
|---|---|
| `TrainAdapter` | `TrainDto` → `EnrichedTrainDto`; validates stop data |
| `StationTrainAdapter` | Pairs arrival/departure `TrainArrivalDepartureDto` records → `EnrichedStationTrainDto` |
| `EnrichedTrainArrivalDepartureAdapter` | Post-processes enriched station train records |
| `TrainStopAdapter` | Transforms individual stop rows, resolves platform and messages |
| `TrainTimestampAdapter` | Adjusts timestamps for trains that span midnight (multi-day journeys) |

### DTO Hierarchy (`dto/`)

| Sub-package | Contents | When used |
|---|---|---|
| `dto/request/` | CFR website form POST bodies | Built in scrapers/repositories before POST calls |
| `dto/scraper/` | Raw parsed data: `TrainDto`, `TrainMetadataDto`, `TrainStopDto`, `TrainArrivalDepartureDto` | Output of scraper layer |
| `dto/enriched/` | Transformed data: `EnrichedTrainDto`, `EnrichedStationTrainDto` | Output of adapter layer |
| `dto/response/` | API response shapes: `StationDto`, `TrainDelayResponseDto`, `DelayDto` | Serialized by controllers |

All DTOs are Java records (immutable) where possible, with Lombok used for mutable builder-based construction.

### Exception Handling (`exception/`)

- `CfrException` — custom runtime exception carrying a list of error messages.
- `CfrExceptionHandler` — `@RestControllerAdvice` that maps `CfrException` to appropriate HTTP error responses.

### Utils (`utils/`)

| Class | Purpose |
|---|---|
| `ScraperUtils` | JSoup helpers: select elements, read attributes, parse table rows |
| `DateTimeUtils` | Format and parse dates in `dd.MM.yyyy` and related CFR-specific patterns |
| `AdapterUtils` | Shared transformation helpers used across multiple adapters |

### Validator (`validator/`)

- `TrainPageValidator` — inspects scraped train pages before processing; throws `CfrException` if the page indicates an error (e.g. train not found, invalid date).

## Package Structure

```
com.stavre.cfrapiadapter/
├── CfrApiAdapterApplication.java
├── adapter/          (7 classes)
├── config/           (FeignConfig)
├── controller/       (4 controllers)
├── dto/
│   ├── enriched/     (4 records)
│   ├── request/      (2 records)
│   ├── response/     (3 records)
│   └── scraper/      (5 records)
├── exception/        (CfrException, CfrExceptionHandler)
├── proxy/            (2 Feign interfaces)
├── repository/       (2 repositories)
├── scraper/
│   ├── station/      (5 scrapers)
│   └── train/        (5 scrapers)
├── service/          (2 services)
├── utils/            (ScraperUtils, DateTimeUtils, AdapterUtils)
└── validator/        (TrainPageValidator)
```

