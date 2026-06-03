# Project Scope

## Purpose

CFR API Adapter is a web scraping adapter that provides programmatic access to data published on the Romanian Railway Company (CFR) website at `mersultrenurilor.infofer.ro`. CFR does not offer a public API, so this service acts as an adapter layer: it scrapes the website on demand and returns the data in a structured JSON REST API format.

## Problem Solved

Romanian railway data (schedules, delays, station timetables) is only available through a web UI. Any application that needs to consume this data programmatically has no official API to call. CFR API Adapter fills this gap by exposing the same data through a clean, versioned REST API that can be integrated into any application.

## Domain

The adapter operates in the Romanian railway domain:

- **Trains**: identified by a train number and optional date; each train has a category (e.g. InterCity, Regio), an operator, and one or more route branches.
- **Stops**: each branch has an ordered list of stops with the following fields:
  - `arrival timestamp` — may be null for terminus stations
  - `arrival delay`
  - `departure timestamp`
  - `departure delay`
  - `station name`
  - `messages` — any messages left by CFR
- **Branches**: a train can have multiple route branches; results are returned per branch. When a train has no branches, a default branch is applied.
- **Stations**: identified by name; each station has an importance level (major hub vs. local stop), a list of arrivals, a list of departures, and a merged view that combines a train's arrival and departure into a single record.
- **Delays**: represented as durations; can be filtered (only delayed trains) or aggregated (total delay across all trains at a station).

## Key Capabilities

| Capability | Description |
|---|---|
| Train timetable | Full list of stops for a train on a given date, per branch |
| Train delay | Delay per branch, optionally filtered to a specific station |
| Station timetable | Merged arrivals and departures for a station on a given date |
| Delayed filtering | Endpoints to retrieve only delayed arrivals or only delayed departures |
| Total delay aggregation | Sum of all delays for arrivals or departures at a station |
| All trains list | Static JSON resource listing all known train numbers |
| All stations list | Static JSON resource listing all stations with their importance metadata |

## REST Endpoints

| Method | Path | Query params | Description | Response type |
|---|---|---|---|---|
| GET | `/train/{trainNumber}` | `date` (optional) | Full timetable with stops for a train on a given date | `EnrichedTrainDto` |
| GET | `/train/{trainId}/delay` | `date` (optional); `station` (optional) | Delay info per branch; if `station` omitted, returns delay for each branch's last station | `TrainDelayResponseDto` |
| GET | `/train/all` | — | All known train numbers (static embedded resource) | `List<String>` |
| GET | `/station/{stationName}` | `date` (optional) | Merged arrivals and departures for a station | `List<EnrichedStationTrainDto>` |
| GET | `/station/all` | — | All stations with their importance level (static embedded resource) | `List<StationDto>` |
| GET | `/station/departures/delayed/{stationName}` | `date` (optional) | Delayed departures only (one entry per delayed train/branch) | `List<EnrichedStationTrainDto>` |
| GET | `/station/departures/delayed/total/{stationName}` | `date` (optional) | Aggregated total departure delay for a station | `DelayDto` |
| GET | `/station/arrivals/delayed/{stationName}` | `date` (optional) | Delayed arrivals only (one entry per delayed train/branch) | `List<EnrichedStationTrainDto>` |
| GET | `/station/arrivals/delayed/total/{stationName}` | `date` (optional) | Aggregated total arrival delay for a station | `DelayDto` |

All live-data endpoints accept an optional `date` query parameter in `dd.MM.yyyy` format. When omitted, the current date is used.

## Response DTOs

| DTO | Description |
|---|---|
| `EnrichedTrainDto` | Full train timetable with stop list and branches |
| `EnrichedStationTrainDto` | Station-centric train record used for arrivals/departures lists |
| `TrainDelayResponseDto` | Structured delay response for a train, optionally filtered by station |
| `DelayDto` | Wrapper for a single aggregated delay value |
| `StationDto` | Station metadata including importance level |

## Static Data Sources

`/train/all` and `/station/all` are served from embedded JSON files on the classpath. These files were originally extracted from a `<script>` tag on the CFR website and are bundled with the application — they are **not** live-scraped on each request.

## Scope Boundaries

- **In scope**: reading and transforming publicly available data from the CFR website.
- **Out of scope**: booking tickets, authentication with CFR, modifying any data on CFR's systems.
- The adapter is read-only and stateless; it makes live requests to CFR on every API call.
