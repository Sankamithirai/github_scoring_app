# 🧩 GitHub Scoring App

A **Spring Boot** application that fetches the most popular GitHub repositories for a given programming language, applies a **custom popularity scoring algorithm**, and exposes the results as a REST API returning JSON.

## 🚀 Overview
This service helps identify trending repositories on GitHub by applying a scoring formula that balances **stars**, **forks**, and **recency**.
It fetches data directly from the GitHub Search API, computes scores, and returns the top-ranked repositories.

## 🧠 Features
- Fetches repositories from the GitHub Search API
- Filters by language, creation date, and limit
- Computes a weighted popularity score based on: stars, forks, and recency
- Supports configurable weights and recency decay constants
- Exposes REST endpoint `/api/repos/popular`
- Fully test-covered

## 🏗️ Project Structure
src/main/java/com/
├── app/ScoringApp.java
├── client/GitHubClient.java
├── config/RestTemplateConfig.java, ScoreConfig.java
├── controller/ScoringController.java
├── parser/WeightParser.java
├── repo/GitHubRepo.java, ScoreRepo.java
└── service/ScoreCalculator.java, ScoringService.java

## ⚙️ Configuration
| Property | Description | Default |
|-----------|-------------|----------|
| GITHUB_TOKEN | GitHub personal access token | none |
| score.weights | Comma-separated weights | stars:0.7,forks:0.2,recency:0.1 |
| score.tau-days | Recency decay constant (days) | 60 |
| github.base-url | GitHub API base URL | https://api.github.com |

Example `application.yml`:
```
github:
  base-url: https://api.github.com
score:
  weights: stars:0.7,forks:0.2,recency:0.1
  tau-days: 60
```

## 🧰 Building and Running
### Prerequisites
Java 17+, Maven, Internet access

### Build JAR
```
cd github_scoring_app/github_scoring_app
./mvnw clean package
```

### Run the app
```
./mvnw spring-boot:run
```

## 🌐 API Usage
GET /api/repos/popular?created_from=2024-01-01&language=Java&limit=5

Example:
```
curl -s "http://localhost:8080/api/repos/popular?created_from=2024-01-01&language=Java&limit=5" | jq
```

## 🧮 Scoring Algorithm
score = (stars * wStars) + (forks * wForks) + (recency_factor * wRecency * 100)
recency_factor = exp(-days_since_last_push / tauDays)

## 🧪 Testing
```
./mvnw test
```

## ⚡ Troubleshooting
| Issue | Cause | Fix |
|-------|--------|-----|
| No qualifying bean | Missing ScoreConfig | Add @Configuration |
| Count=0 | Rate limit | Use GITHUB_TOKEN |
| 401 Unauthorized | Invalid token | Regenerate token |

