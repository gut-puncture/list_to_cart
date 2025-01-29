from elasticsearch import Elasticsearch
from dotenv import load_dotenv
import os

# Load environment variables
load_dotenv()

ELASTIC_SEARCH_HOST = os.getenv("ELASTIC_SEARCH_HOST", "http://localhost:9200")
PRODUCT_INDEX_NAME = "products"

es_client = Elasticsearch(ELASTIC_SEARCH_HOST)

# Define mapping
mapping = {
    "mappings": {
        "properties": {
            "product_name": {"type": "text"},
            "description": {"type": "text"},
            "image_url": {"type": "keyword"},
            "vector_embedding": {
                "type": "dense_vector",
                "dims": 3072,  # Changed from 384 to 3072 for text-embedding-3-large
                "index": True,
                "similarity": "cosine"
            },
            "sku_details": {
                "type": "nested",
                "properties": {
                    "quantity": {"type": "keyword"},
                    "numeric_quantity": {"type": "float"},
                    "unit": {"type": "keyword"},
                    "is_default": {"type": "boolean"}
                }
            }
        }
    }
}

# Create index if it doesn't exist
if not es_client.indices.exists(index=PRODUCT_INDEX_NAME):
    print(f"Creating index '{PRODUCT_INDEX_NAME}'...")
    es_client.indices.create(index=PRODUCT_INDEX_NAME, body=mapping)
    print(f"Index '{PRODUCT_INDEX_NAME}' created successfully.")
else:
    print(f"Index '{PRODUCT_INDEX_NAME}' already exists.")