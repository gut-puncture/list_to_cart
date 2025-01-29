from flask import Flask, request, jsonify, send_from_directory
from elasticsearch import Elasticsearch
from openai import OpenAI
from flask_cors import CORS
import anthropic
import base64
import json
import os
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

app = Flask(__name__)
CORS(app)

# Configuration
CLAUDE_API_KEY = os.getenv("CLAUDE_API_KEY")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
ELASTIC_SEARCH_HOST = os.getenv("ELASTIC_SEARCH_HOST", "http://localhost:9200")
PRODUCT_INDEX_NAME = "products"

# Initialize clients
anthropic_client = anthropic.Anthropic(api_key=CLAUDE_API_KEY)
openai_client = OpenAI(api_key=OPENAI_API_KEY)
es_client = Elasticsearch(ELASTIC_SEARCH_HOST)

def get_grocery_list_from_image_claude(image_base64):
    try:
        response = anthropic_client.messages.create(
            model="claude-3-5-sonnet-20241022",
            max_tokens=1024,
            messages=[{
                "role": "user",
                "content": [{
                    "type": "text",
                    "text": "Extract grocery items from this image. If there is an adjective or descriptor with the item, include it in the Item name, for example 'organic' in organic strawberry, 'low fat' in low fat etc. Return only a JSON object with format: {'grocery_list': [{'item_name': string, 'quantity': number, 'unit': string}, ...]}"
                }, {
                    "type": "image",
                    "source": {
                        "type": "base64",
                        "media_type": "image/jpeg",
                        "data": image_base64
                    }
                }]
            }]
        )
        
        json_str = response.content[0].text.strip()
        try:
            return json.loads(json_str).get('grocery_list', [])
        except json.JSONDecodeError:
            if '{' in json_str:
                json_str = json_str[json_str.find('{'):json_str.rfind('}')+1]
                data = json.loads(json_str)
                return data.get('items', []) or data.get('grocery_list', [])
            return []
    except Exception as e:
        print(f"Error: {str(e)}")
        return []

def get_product_recommendations(item_name):
    try:
        print(f"Searching for: {item_name}")
        
        embedding_response = openai_client.embeddings.create(
            input=item_name,
            model="text-embedding-3-large"
        )
        embedding = embedding_response.data[0].embedding
        
        # Modified query to include minimum_score
        query = {
            "query": {
                "script_score": {
                    "query": {"match_all": {}},
                    "script": {
                        "source": "cosineSimilarity(params.query_vector, 'vector_embedding') + 1.0",
                        "params": {"query_vector": embedding}
                    },
                    "min_score": 1.45 #arrived by trial and error. should be learned.
                }
            },
            "_source": ["product_name", "description", "sku_details", "image_url"],  # Added image_url
            "size": 8
        }

        response = es_client.search(
            index=PRODUCT_INDEX_NAME,
            body=query
        )
        
        recommendations = []
        for hit in response['hits']['hits']:
            score = hit['_score'] - 1.0  # Convert back to -1 to 1 range
            product = hit['_source']
            print(f"Score: {score:.3f} ({score*100:.1f}%) for product: {product.get('product_name')}")  # Debug print
            recommendations.append({
                "product_name": product.get('product_name', item_name),
                "description": product.get('description', ""),
                "skus": product.get('sku_details', []),
                "similarity_score": round(score * 100, 1),
                "image_url": product.get('image_url')  # Added this line
            })
        
        # Sort by score descending
        recommendations = sorted(recommendations, key=lambda x: x['similarity_score'], reverse=True)
        return recommendations

    except Exception as e:
        print(f"Elasticsearch Error: {str(e)}")
        import traceback
        print(traceback.format_exc())
        return []

@app.route('/process_image', methods=['POST'])
def process_image():
    if 'image' not in request.files:
        print("No image in request")
        return jsonify({"error": "No image part"}), 400
    
    file = request.files['image']
    if file.filename == '':
        print("Empty filename")
        return jsonify({"error": "No selected image"}), 400

    try:
        image_data = file.read()
        print(f"Image size: {len(image_data)} bytes")
        image_base64 = base64.b64encode(image_data).decode('utf-8')
        print(f"Base64 size: {len(image_base64)}")
        grocery_list = get_grocery_list_from_image_claude(image_base64)
        print(f"Got grocery list: {grocery_list}")
        return jsonify({"grocery_list": grocery_list}), 200
    except Exception as e:
        print(f"Error processing image: {str(e)}")
        return jsonify({"error": f"Error processing image: {str(e)}"}), 500

@app.route('/recommendations', methods=['POST'])
def recommendations():
    try:
        data = request.get_json()
        item_name = data.get('item_name')
        app.logger.info(f"Recommendations for: {item_name}")
        product_recs = get_product_recommendations(item_name)
        app.logger.info(f"Found {len(product_recs)} recommendations")
        return jsonify({"recommendations": product_recs}), 200
    except Exception as e:
        app.logger.error(f"Recommendation error: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/images/<path:filename>')
def serve_image(filename):
    app.logger.info(f"Image request for: {filename}")
    image_path = os.path.join(os.path.dirname(__file__), 'ProductImages')
    app.logger.info(f"Looking in: {image_path}")
    try:
        return send_from_directory(image_path, filename)
    except Exception as e:
        app.logger.error(f"Failed to serve {filename}: {e}")
        return str(e), 404
    

if __name__ == '__main__':
    app.run(debug=True, host='192.168.0.100', port=5000)