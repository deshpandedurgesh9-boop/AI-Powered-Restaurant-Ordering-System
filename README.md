# AI-Powered Autonomous Restaurant Ordering System

An end-to-end conversational restaurant ordering solution integrating an Android mobile application with a FastAPI backend and a Google Gemini LangGraph agent.

---

## Architecture & Features

- **Frontend (Android / Java):** Clean UI communicating via Retrofit; uses ADB reverse proxying to route local network calls seamlessly to the backend.
- **Backend (FastAPI):** High-concurrency asynchronous API serving menu data, chat streaming, and order processing.
- **AI Agent (Google Gemini & LangGraph):** Implements multi-turn context retention with Human-in-the-Loop (HITL) interrupt handling to verify customer information before committing transactions.
- **Database (MySQL):** Relational schema handling `menu_items` availability and `order_history` with full customer details.

---

## Tech Stack

- **Android Client:** Java, Retrofit2, OkHttp3
- **Backend Service:** Python, FastAPI, Uvicorn
- **AI / LLM:** Google Gemini (`gemini-3.8-flash`), LangChain, LangGraph
- **Database:** MySQL

---

## Local Setup

### 1. Backend Setup
```bash
# Activate your environment
cd Hotel_AI_Project
python -m venv .venv
.venv\Scripts\activate

# Install dependencies
pip install fastapi uvicorn mysql-connector-python langchain-google-genai langgraph python-dotenv

# Run the server
uvicorn api:app --reload --host 0.0.0.0 --port 8000
