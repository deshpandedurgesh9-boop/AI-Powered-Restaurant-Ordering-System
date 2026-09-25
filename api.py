import os
import mysql.connector
from dotenv import load_dotenv
from fastapi import FastAPI
from pydantic import BaseModel
from langchain_core.messages import HumanMessage
from langgraph.types import Command

from agent_core import agent

load_dotenv()

app = FastAPI(title="Restaurant AI API")

def get_db():
    return mysql.connector.connect(
        host=os.getenv("DB_HOST", "localhost"),
        user=os.getenv("DB_USER", "root"),
        password=os.getenv("DB_PASSWORD"),
        database=os.getenv("DB_NAME", "restaurant_db"),
    )

def extract_text(content):
    if isinstance(content, str):
        return content
    if isinstance(content, list):
        parts = []
        for part in content:
            if isinstance(part, str):
                parts.append(part)
            elif isinstance(part, dict) and "text" in part:
                parts.append(part["text"])
        return " ".join(parts)
    return str(content)

class ChatRequest(BaseModel):
    user_id: str
    message: str

class DecisionRequest(BaseModel):
    user_id: str
    decision: str

@app.get("/menu")
def get_menu():
    try:
        conn = get_db()
        cursor = conn.cursor(dictionary=True)
        cursor.execute("SELECT item_no, item_name, price, quantity FROM menu_items")
        items = cursor.fetchall()
        cursor.close()
        conn.close()
        return {"menu": items if items else []}
    except Exception as e:
        print(f"Menu fetch error: {e}")
        return {"menu": [], "error": str(e)}

@app.get("/orders")
@app.get("/history")
def get_order_history():
    try:
        conn = get_db()
        cursor = conn.cursor(dictionary=True)
        cursor.execute("""
            SELECT order_id, 
                   COALESCE(order_number, '') AS order_number, 
                   COALESCE(item_name, '') AS item_name, 
                   COALESCE(quantity, 0) AS quantity, 
                   COALESCE(total_price, 0.0) AS total_price, 
                   COALESCE(customer_name, '') AS customer_name, 
                   COALESCE(phone_number, '') AS phone_number, 
                   COALESCE(delivery_address, '') AS delivery_address, 
                   COALESCE(DATE_FORMAT(order_time, '%Y-%m-%d %H:%i'), '') AS order_time 
            FROM order_history 
            ORDER BY order_time DESC
        """)
        orders = cursor.fetchall()
        cursor.close()
        conn.close()
        return {"orders": orders if orders else []}
    except Exception as e:
        print(f"History fetch error: {e}")
        return {"orders": [], "error": str(e)}

@app.post("/chat")
def chat_with_agent(req: ChatRequest):
    try:
        config = {"configurable": {"thread_id": req.user_id}}
        result = agent.invoke(
            {"messages": [HumanMessage(content=req.message)]},
            config=config,
        )

        if "__interrupt__" in result:
            return {
                "status": "confirmation_needed",
                "reply": "Do you want to confirm placing this order? (approve/reject)"
            }

        last_message = result["messages"][-1]
        return {
            "status": "success",
            "reply": extract_text(last_message.content)
        }
    except Exception as e:
        print(f"Chat Error: {e}")
        return {
            "status": "error",
            "reply": f"Internal Agent Error: {str(e)}"
        }

@app.post("/confirm_order")
def confirm_order(req: DecisionRequest):
    try:
        config = {"configurable": {"thread_id": req.user_id}}
        result = agent.invoke(
            Command(resume={"decisions": [{"type": req.decision}]}),
            config=config,
        )
        last_message = result["messages"][-1]
        return {
            "status": "success",
            "reply": extract_text(last_message.content)
        }
    except Exception as e:
        print(f"Confirm Error: {e}")
        return {
            "status": "error",
            "reply": f"Internal Confirm Error: {str(e)}"
        }