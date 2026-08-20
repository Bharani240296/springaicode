# Spring AI + Groq Demo

## Description

This project demonstrates how to integrate Generative AI
with Java Spring Boot using Spring AI and Groq.

## Technologies

- Java 17
- Spring Boot
- Spring AI
- Groq
- Maven

## Features

- Send a question to the AI model
- Receive an AI-generated response
- REST API integration

## API

### Ask AI

GET:

/ai/ask?message=What is Java?

Example:

http://localhost:8080/ai/ask?message=What%20is%20Java?

## Configuration

Set the Groq API key as an environment variable:

GROQ_API_KEY

Then configure:

spring.ai.openai.api-key=${GROQ_API_KEY}

## How to Run

1. Set the `GROQ_API_KEY` environment variable.
2. Clone the project.
3. Run the Spring Boot application.
4. Call the `/ai/ask` API.