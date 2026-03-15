# CRF web scraping

API that provides data about stations and trains by web scrapping CFR's (Romanian Railways Company) website.

## How does it work

The app scrapes

https://mersultrenurilor.infofer.ro/ro-RO/Tren/{trainNumber}
https://mersultrenurilor.infofer.ro/ro-RO/Statie/{station}

and provides scraped data through endpoints.

In order to access any CFR page you first need to make a GET request, scrape the CSRF tokens,
and then make a POST request to another page.