from elasticsearch import Elasticsearch
from openai import OpenAI
from dotenv import load_dotenv
import os

# Load environment variables
load_dotenv()

ELASTIC_SEARCH_HOST = os.getenv("ELASTIC_SEARCH_HOST", "http://localhost:9200")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
PRODUCT_INDEX_NAME = "products"

es_client = Elasticsearch(ELASTIC_SEARCH_HOST)
openai_client = OpenAI(api_key=OPENAI_API_KEY)

def get_embedding(text):
    """Get embedding from OpenAI API"""
    response = openai_client.embeddings.create(
        input=text,
        model="text-embedding-3-large"
    )
    return response.data[0].embedding

# Sample products data with more realistic products
sample_products = [
    {
        "product_name": "Amul Gold Full Cream Milk",
        "description": "Fresh organic dairy milk from grass-fed cows, delivered to you, rich and creamy, by Amul.",
        "image_url": "https://b3c7-103-120-251-6.ngrok-free.app/images/amul-gold-milk.jpg",
        "sku_details": [
            {"quantity": "1 litre", "numeric_quantity": 1.0, "unit": "litre", "is_default": True},
            {"quantity": "1/2 litre", "numeric_quantity": 0.5, "unit": "litre", "is_default": False}
        ]
    },
    {
        "product_name": "Amul Taaza Toned Fresh Milk",
        "description": "Fresh organic low fat dairy milk from grass-fed cows, delivered to you by Amul.",
        "image_url": "https://b3c7-103-120-251-6.ngrok-free.app/images/amul-toned-milk.jpg",
        "sku_details": [
            {"quantity": "1 litre", "numeric_quantity": 1.0, "unit": "litre", "is_default": True},
            {"quantity": "1/2 litre", "numeric_quantity": 0.5, "unit": "litre", "is_default": False}
        ]
    },
    {
        "product_name": "Eggoz Nutrition Protein Rich Farm Fresh White Eggs",
        "description": "Free-range brown eggs from local poultry farms. High Protein.",
        "image_url": "https://b3c7-103-120-251-6.ngrok-free.app/images/eggoz-egg-carton.jpg",
        "sku_details": [
            {"quantity": "10 count", "numeric_quantity": 10.0, "unit": "count", "is_default": False}
        ]
    },
    {
        "product_name": "Yojana Poultry's Fresh Eggs",
        "description": "Free-range brown eggs from local poultry farms.",
        "image_url": "https://b3c7-103-120-251-6.ngrok-free.app/images/fresh-eggs-box-30.jpg",
        "sku_details": [
            {"quantity": "30 count", "numeric_quantity": 30.0, "unit": "count", "is_default": True}
        ]
    },
    {
        "product_name": "Britannia Whole Grain Bread",
        "description": "Freshly baked bread made with maida, not wheat.",
        "sku_details": [
            {"quantity": "1 loaf", "numeric_quantity": 1.0, "unit": "loaf", "is_default": True}
        ]
    },
    {
        "product_name": "Britannia Brown Bread",
        "description": "Freshly baked whole wheat bread.",
        "sku_details": [
            {"quantity": "1 loaf", "numeric_quantity": 1.0, "unit": "loaf", "is_default": True}
        ]
    },
    {
        "product_name": "Banana Robusta - Ready to Eat",
        "description": "Fresh organic bananas, perfectly ripened and ready to eat",
        "image_url": "https://b3c7-103-120-251-6.ngrok-free.app/images/ripe-banana.jpg",
        "sku_details": [
            {"quantity": "4 count", "numeric_quantity": 4.0, "unit": "count", "is_default": True},
        ]
    },
    {
        "product_name": "Bananas",
        "description": "Fresh bananas. not ripe.",
        "image_url": "https://b3c7-103-120-251-6.ngrok-free.app/images/unripe-banana.webp",
        "sku_details": [
            {"quantity": "12 count", "numeric_quantity": 12.0, "unit": "count", "is_default": True},
        ]
    },
    {
        "product_name": "Ground Beef",
        "description": "Premium lean ground beef, 90% lean, perfect for burgers and meatballs",
        "sku_details": [
            {"quantity": "1 lb", "numeric_quantity": 1.0, "unit": "pound", "is_default": True},
            {"quantity": "2 lb", "numeric_quantity": 2.0, "unit": "pound", "is_default": False},
            {"quantity": "500g", "numeric_quantity": 0.5, "unit": "kg", "is_default": False}
        ]
    },
    {
        "product_name": "Milky Mist Set Curd Tub",
        "description": "High Protein creamy plain curd with probiotic cultures.",
        "image_url": "https://b3c7-103-120-251-6.ngrok-free.app/images/milky-mist-curd-1-litre.jpg",
        "sku_details": [
            {"quantity": "1l", "numeric_quantity": 1.0, "unit": "litre", "is_default": True}
        ]
    },
    {
        "product_name": "Milky Mist Set Curd Cup",
        "description": "400 gm High Protein creamy plain curd with probiotic cultures.",
        "image_url": "https://b3c7-103-120-251-6.ngrok-free.app/images/milky-mist-curd-400-gm.jpg",
        "sku_details": [
            {"quantity": "400 gm", "numeric_quantity": 400.0, "unit": "gram", "is_default": False}
        ]
    },
    {
        "product_name": "Chicken Breast",
        "description": "Boneless, skinless chicken breast from free-range chickens",
        "sku_details": [
            {"quantity": "1 lb", "numeric_quantity": 1.0, "unit": "pound", "is_default": True},
            {"quantity": "2 lb", "numeric_quantity": 2.0, "unit": "pound", "is_default": False},
            {"quantity": "500g", "numeric_quantity": 0.5, "unit": "kg", "is_default": False}
        ]
    },
    {
        "product_name": "Sweet Bell Peppers",
        "description": "Mixed color bell peppers - red, yellow, and orange, fresh and crisp",
        "sku_details": [
            {"quantity": "3 count", "numeric_quantity": 3.0, "unit": "count", "is_default": True},
            {"quantity": "6 count", "numeric_quantity": 6.0, "unit": "count", "is_default": False},
            {"quantity": "1 lb", "numeric_quantity": 1.0, "unit": "pound", "is_default": False}
        ]
    },
    {
        "product_name": "Avocado Hass Imported",
        "description": "Ripe Hass avocados, perfect for guacamole or sandwiches",
        "image_url": "https://b3c7-103-120-251-6.ngrok-free.app/images/avocado.jpg",
        "sku_details": [
            {"quantity": "2 count", "numeric_quantity": 2.0, "unit": "count", "is_default": True},
            {"quantity": "4 count", "numeric_quantity": 4.0, "unit": "count", "is_default": False},
            {"quantity": "1 bag", "numeric_quantity": 5.0, "unit": "count", "is_default": False}
        ]
    }
]

# Process and insert each product with its embedding
for product in sample_products:
    try:
        # Create embedding from product name and description
        embedding_text = f"{product['product_name']} {product['description']}"
        product['vector_embedding'] = get_embedding(embedding_text)
        
        # Index the product
        es_client.index(index=PRODUCT_INDEX_NAME, document=product)
        print(f"Added product with embedding: {product['product_name']}")
    except Exception as e:
        print(f"Error adding {product['product_name']}: {e}")

print("Sample data population complete!")