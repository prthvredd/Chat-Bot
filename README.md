# SmartNotes AI Chatbot

SmartNotes AI is a simple Spring Boot chatbot that uses the Groq Chat Completions API to explain or summarize text. It includes a clean dark frontend, difficulty levels, copy-to-clipboard, and loading feedback.

## Features

- Chat endpoint for asking questions
- Summarize endpoint for pasted notes or paragraphs
- Difficulty selector: Beginner, Intermediate, Advanced
- Dark themed frontend served by Spring Boot
- Loading state while waiting for AI response
- Copy button for AI output
- Groq API integration

## Tech Stack

- Java 17
- Spring Boot 4
- Spring Web
- Spring Security
- Maven Wrapper
- HTML, CSS, JavaScript
- Groq API

## Setup

Create a `.env` file in the project root:

```env
GROQ_API_KEY=your_groq_api_key_here
```

The `.env` file is ignored by Git, so your API key will not be committed.

## Run Locally

From the project root:

```powershell
.\mvnw.cmd spring-boot:run
```

The app runs on:

```text
http://localhost:8080
```

If port `8080` is already busy, run it on another port:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

Then open:

```text
http://localhost:8081
```

## API Examples

Chat:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/chat" -Method Post -ContentType "application/json" -Body '{"message":"Explain Java in simple terms","level":"beginner"}'
```

Summarize:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/chat/summarize" -Method Post -ContentType "application/json" -Body '{"message":"Java is a high-level programming language used to build applications across platforms.","level":"intermediate"}'
```

Request body:

```json
{
  "message": "Your question or notes here",
  "level": "beginner"
}
```

Allowed levels:

- `beginner`
- `intermediate`
- `advanced`

## Test

```powershell
.\mvnw.cmd test
```

## Docker

Build the image:

```powershell
docker build -t smartnotes-ai .
```

Run the container:

```powershell
docker run --env-file .env -p 8080:8080 smartnotes-ai
```

Then open:

```text
http://localhost:8080
```

## Notes

- CSRF is disabled for easier local API testing.
- `/api/chat` and `/api/chat/summarize` are publicly accessible.
- Other routes remain protected by Spring Security defaults.
- The app uses Groq's OpenAI-compatible chat completions endpoint.
