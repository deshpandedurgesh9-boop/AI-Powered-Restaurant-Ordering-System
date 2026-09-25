import os
from pathlib import Path
from dotenv import load_dotenv
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain.agents import create_agent
from langchain.agents.middleware import HumanInTheLoopMiddleware
from langgraph.checkpoint.memory import InMemorySaver

from restaurant_tools import check_item_stock, place_order

env_path = Path(__file__).resolve().parent / ".env"
load_dotenv(dotenv_path=env_path)

system_prompt = (
    "You are a helpful restaurant ordering assistant.\n"
    "To place an order, you MUST collect ALL 4 pieces of information:\n"
    "1. Food item name and quantity\n"
    "2. Customer full name\n"
    "3. Contact phone number\n"
    "4. Delivery address\n\n"
    "RULES:\n"
    "- If ANY information is missing, ask the user for it. Do NOT call place_order.\n"
    "- Only call check_item_stock when verifying availability.\n"
    "- Once all 4 details are collected, ask the user to explicitly confirm. "
    "Do NOT trigger place_order until confirmation is received."
)

llm = ChatGoogleGenerativeAI(
    model="gemini-3.8-flash",
    api_key=os.getenv("GEMINI_API_KEY"),
    request_timeout=30,
)

tools = [check_item_stock, place_order]

agent = create_agent(
    model=llm,
    tools=tools,
    system_prompt=system_prompt,
    checkpointer=InMemorySaver(),
    middleware=[
        HumanInTheLoopMiddleware(
            interrupt_on={
                "place_order": {
                    "allowed_decisions": ["approve", "reject"]
                }
            }
        )
    ],
)