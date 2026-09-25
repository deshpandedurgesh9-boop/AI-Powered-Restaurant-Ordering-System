import os
from dotenv import load_dotenv
import mysql.connector

load_dotenv()

print("Testing environment...")
print("GEMINI_API_KEY present:", bool(os.getenv("GEMINI_API_KEY")))

try:
    conn = mysql.connector.connect(
        host=os.getenv("DB_HOST", "localhost"),
        user=os.getenv("DB_USER", "root"),
        password=os.getenv("DB_PASSWORD"),
        database=os.getenv("DB_NAME", "restaurant_db"),
    )
    print("MySQL connection: SUCCESS")
    conn.close()
except Exception as e:
    print(f"MySQL connection FAILED: {e}")