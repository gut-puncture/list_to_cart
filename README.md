Small demo for showing how the photo of a list of items can be converted to a full cart for a user.
Check out the demo video here: 

<h2>Working:</h2>
Backend is only three files and extremely easy to implement.
The image is sent to Claude 3.5 Sonnet for getting a list of items from the image. Then the list of items are converted to Vector Embeddings using OpenAI's text-embedding-3-large model.<br>
In an Elasticsearch, we already have the Vector Embeddings for all the products (generated for Product Name and Description) in the catalogue.<br> 
A cosine similarity is run for the item embeddings and the Product Catalogue embeddings and the products with similarity threshold above a number are shown as recommendations 
for each item.

<h3>Cases Covered</h3>
Current demo covers:<br>
1. spelling mistakes <br>
2. hand-written lists <br>
3. adjectives with items like organic tomato, green apple, Amul Ice Cream etc. <br>
4. quantity is written with the item name <br>

<h2>Next Phase</h2>
Not planning to do a phase 2, but that must include:<br><br>
1. Usage of a smalled model for image parsing. Almost isn't worth it because of very low cost but this can definitely be done with smaller models 
(probably even models which can run on CPUs like Moondream). <br>
2. Handling quantity added with items should be handled robustly. It is included in the current implementation.<br>
3. Product Vector embeddings must include the quantity size of the SKU.<br>
4. Dynamic Threshold for Similarity Search, taking user feedback into account.
