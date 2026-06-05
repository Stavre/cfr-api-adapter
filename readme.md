### Project Overview
A small service that scrapes train and station data from the CFR (Romanian Railway Company) website and exposes it via HTTP endpoints. The implementation is layered to separate concerns: **proxy**, **scraper**, **adapter**, **service**, and **controller**.

---

### Architecture
| **Layer** | **Responsibility** | **Notes**                                          |
|---|---:|----------------------------------------------------|
| **Proxy** | Retrieve HTML pages from CFR using OpenFeign | Handles HTTP requests and responses to CFR website |
| **Scraper** | Parse HTML and produce DTOs | Extracts tokens and page fragments                 |
| **Adapter** | Validate and transform DTOs into domain objects | Applies extraction rules and enriches scraped data |
| **Service** | Orchestrate proxy, scraper, and adapter to provide domain data | Implements additional processing |
| **Controller** | Define and expose REST endpoints | Maps service operations to HTTP routes             |

---

### Scraping Logic
- **Two-step request flow**
    1. **GET** the initial page to obtain HTML tokens and hidden fields.
    2. **POST** a follow-up request using the tokens extracted from the GET response to retrieve the target data.
- **Data extraction** is performed by the scraper layer and passed to the adapter for further validation and enrichment.

---

### Available Data and Domain Model

#### Train
**Available fields**
- **train number**
- **train category**
- **train operator**
- **train stops**
- **branches**

**Train Stop**
- **arrival timestamp** — may be null for terminus stations
- **arrival delay**
- **departure timestamp**
- **departure delay**
- **station name**
- **messages** — any messages left by CFR

**Branches**
- Train endpoints return results per branch. When a train does not have branches, then a default one is applied.

#### Station
**Available fields**
- **station name**
- **list of departures**
- **list of arrivals**
- **merged arrivals and departures** — a merged object combines a train’s arrival and departure into a single record

---

### Endpoints

Below is a concise reference for the HTTP endpoints implemented by the controllers.

| **Path** | **Method** | **Query params** | **Description** | **Response** |
|---|---:|---|---|---|
| **/train/{trainNumber}** | GET | `date` (optional) | Get full timetable (stops) for a train on a given date | **EnrichedTrainDto** |
| **/train/{trainId}/delay** | GET | `date` (optional); `station` (optional) | Get train delay info. If `station` omitted returns overall/last-station delay (includes branches) | **TrainDelayResponseDto** |
| **/train/all** | GET | _none_ | Return the list of all train numbers (from embedded JSON resource) | **List<String>** |
| **/station/{stationName}** | GET | `date` (optional) | Get merged arrivals and departures for a station on a given date | **List<EnrichedStationTrainDto>** |
| **/station/all** | GET | _none_ | Get all stations with their importance | **List<StationDto>** |
| **/station/arrivals/{stationName}** | GET | `date` (optional) | Get all arrivals for a station on a given date | **List<EnrichedStationTrainDto>** |
| **/station/departures/{stationName}** | GET | `date` (optional) | Get all departures for a station on a given date | **List<EnrichedStationTrainDto>** |
| **/station/departures/delayed/{stationName}** | GET | `date` (optional) | Get delayed departures for a station (one entry per delayed train/branch) | **List<EnrichedStationTrainDto>** |
| **/station/departures/delayed/total/{stationName}** | GET | `date` (optional) | Get aggregated total departures delay for a station | **DelayDto** |
| **/station/arrivals/delayed/{stationName}** | GET | `date` (optional) | Get delayed arrivals for a station (one entry per delayed train/branch) | **List<EnrichedStationTrainDto>** |
| **/station/arrivals/delayed/total/{stationName}** | GET | `date` (optional) | Get aggregated total arrivals delay for a station | **DelayDto** |

---

### Notes and behavior details
- **Date handling**: Most endpoints accept an optional `date` query parameter formatted as `dd.MM.yyyy`. If omitted, controllers use today's date.
- **Train delay endpoints**: Delay endpoints return results **per branch** ; if station is omitted, returns delay for branch's last station.
- **Station  delay endpoints**: total endpoints return aggregated delay values wrapped in **DelayDto**.
- **Train list source**: The `/train/all` list is served from a manually scraped JSON resource extracted from a `<script>` tag.
- **Station list source**: The `/station/all` list is served from a manually scraped JSON resource extracted from a `<script>` tag.
- **Response DTOs**:
    - **EnrichedTrainDto** — full train timetable with stops and branches.
    - **EnrichedStationTrainDto** — station-centric train record used for arrivals/departures lists.
    - **TrainDelayResponseDto** — structured delay response for a train (optionally filtered by station).
    - **DelayDto** — wrapper for a single aggregated delay value.
    - **StationDto** — station metadata including importance.

---
