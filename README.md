# 🧠 AI Research Assistant

[![Java CI with Maven](https://github.com/AnuragKun/Research-Assistant/actions/workflows/maven.yml/badge.svg)](https://github.com/AnuragKun/Research-Assistant/actions/workflows/maven.yml)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg?logo=postgresql)](https://www.postgresql.org/)
[![Google Gemini](https://img.shields.io/badge/AI-Google_Gemini-orange.svg)](https://deepmind.google/technologies/gemini/)

A full-stack AI-powered Research Assistant. This project consists of a **Chrome Extension** frontend that allows users to instantly summarize, analyze, and save web content, powered by a robust **Spring Boot** REST API backend integrated with the **Google Gemini AI**.

---

## ✨ Features
* **AI-Powered Analysis**: Highlight text on any web page and instantly generate summaries or research suggestions using Google's Gemini AI.
* **Seamless Workspace**: A Chrome Side-Panel UI that acts as a continuous, distraction-free workspace while you browse.
* **Persistent Storage**: Save your research notes directly to a PostgreSQL database, complete with a history log to retrieve past sessions.
* **Stateless Security**: API Key-based authentication to secure the backend endpoints from unauthorized access.
* **Automated CI/CD**: Fully automated testing pipeline using GitHub Actions that spins up a Dockerized PostgreSQL instance to verify code integrity on every push.

---

## 🛠️ Tech Stack
### Frontend (Chrome Extension)
* Vanilla JavaScript (ES6+), HTML5, CSS3
* Chrome Extensions API (Manifest V3, Scripting, SidePanel)

### Backend (Spring Boot)
* **Framework:** Spring Boot 3.3.x (Java 21)
* **Database:** PostgreSQL & Spring Data JPA
* **AI Integration:** Spring RestClient (Google Gemini API)
* **Security:** Spring Security (Custom `OncePerRequestFilter` for API Keys)
* **Testing:** JUnit 5, Mockito, MockMvc
* **DevOps:** GitHub Actions, Docker (Service Containers)

---

## 🚀 Getting Started

### 1. Backend Setup
1. **Clone the repository:**
   ```bash
   git clone https://github.com/AnuragKun/Research-Assistant.git
   cd Research-Assistant
   ```
2. **Database Configuration:**
   Ensure you have PostgreSQL running locally on port `5432` with a database named `ResearchDB` (or update `application.properties` to match your local setup).
3. **Environment Variables:**
   You must provide a Google Gemini API Key. You can set this in your environment or directly in `application.properties`:
   ```properties
   GEMINI_KEY=your_google_gemini_api_key
   app.api.key=my-super-secret-dev-key
   ```
4. **Run the Application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   The backend will start on `http://localhost:8080`.

### 2. Chrome Extension Setup
1. Open Google Chrome and navigate to `chrome://extensions/`.
2. Enable **Developer mode** (toggle in the top right corner).
3. Click **Load unpacked** and select the `ResearchAssistantExtension` folder from this repository.
4. Pin the extension to your toolbar and click it to open the Side-Panel workspace!

---

## 🔒 Security & Architecture
This application utilizes a stateless API architecture. The Spring Boot backend is secured using a custom `ApiKeyAuthFilter` that intercepts all incoming traffic. 
* Public endpoints (e.g., Swagger UI documentation at `/swagger-ui/index.html`) are permitted.
* Protected endpoints (`/api/**`) require an `x-api-key` header to be present and valid, preventing unauthorized database manipulation.

## 🧪 Testing
The backend is thoroughly tested using **JUnit 5** and **Mockito**. 
To run the test suite locally:
```bash
./mvnw test
```
*Note: The test suite uses `@TestPropertySource` to mock API keys, allowing tests to run smoothly in CI environments without exposing secrets.*
