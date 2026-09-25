import os
import random
import mysql.connector
from langchain_core.tools import tool

def get_db():
    return mysql.connector.connect(
        host=os.getenv("DB_HOST", "localhost"),
        user=os.getenv("DB_USER", "root"),
        password=os.getenv("DB_PASSWORD"),
        database=os.getenv("DB_NAME", "restaurant_db"),
    )

@tool
def check_item_stock(item_name: str) -> str:
    """Checks if an item is available on the menu and returns its stock and price."""
    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT item_name, price, quantity FROM menu_items WHERE LOWER(item_name) = LOWER(%s)", (item_name,))
    item = cursor.fetchone()
    cursor.close()
    conn.close()
    if not item:
        return f"Item '{item_name}' is not found on the menu."
    return f"{item['item_name']} is available. Price: Rs.{item['price']}, In stock: {item['quantity']}"

@tool
def place_order(item_name: str, quantity: int, customer_name: str, phone_number: str, delivery_address: str) -> str:
    """Places the verified food order into the database."""
    conn = get_db()
    cursor = conn.cursor(dictionary=True)
    
    cursor.execute("SELECT price, quantity FROM menu_items WHERE LOWER(item_name) = LOWER(%s)", (item_name,))
    item = cursor.fetchone()
    if not item:
        cursor.close()
        conn.close()
        return f"Order failed: {item_name} does not exist."

    if item["quantity"] < quantity:
        cursor.close()
        conn.close()
        return f"Order failed: only {item['quantity']} available."

    total_price = float(item["price"]) * quantity
    order_number = f"ORD{random.randint(1000, 9999)}"

    cursor.execute("""
        INSERT INTO order_history (order_number, item_name, quantity, total_price, customer_name, phone_number, delivery_address)
        VALUES (%s, %s, %s, %s, %s, %s, %s)
    """, (order_number, item_name, quantity, total_price, customer_name, phone_number, delivery_address))

    cursor.execute("""
        UPDATE menu_items SET quantity = quantity - %s WHERE LOWER(item_name) = LOWER(%s)
    """, (quantity, item_name))

    conn.commit()
    cursor.close()
    conn.close()

    return f"Order #{order_number} confirmed! Total: Rs.{total_price}. Delivery to {delivery_address}."