package app.shann;

import app.shann.model.Product;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Solr Synchronizer...");

        // Connect to Solr server
        String urlString = "http://localhost:8983/solr/bigboxstore";
        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        // SolrJ uses a binary format by default; explicitly set XML parser for Solr compatibility
        solr.setParser(new XMLResponseParser());

        // Batch-index a diverse product catalogue
        createIndices(solr);

        // --- Query 1: full-text search across all text fields ---
        System.out.println("\n=== Full-text search: 'iPhone' ===");
        QueryResponse textResponse = query(solr, "iPhone", null, null,
                "id,name,brand,price,inStock", 10);
        printResults(textResponse);

        // --- Query 2: price range [100 TO 500], sorted cheapest-first ---
        System.out.println("\n=== Price range $100-$500, sorted by price asc ===");
        QueryResponse rangeResponse = query(solr, "price:[100 TO 500]", "price", "asc",
                "id,name,price,category", 10);
        printResults(rangeResponse);

        // --- Query 3: category filter for Electronics, in-stock only ---
        System.out.println("\n=== Electronics in stock ===");
        QueryResponse categoryResponse = query(solr, "category:Electronics AND inStock:true",
                "price", "asc", "id,name,brand,price", 10);
        printResults(categoryResponse);
    }

    /**
     * Indexes a diverse batch of products using addBeans() for efficiency.
     */
    private static void createIndices(HttpSolrClient solr) {
        List<Product> products = Arrays.asList(
                new Product("888",  "Apple iPhone 6s",            299.99, "Electronics", "Apple",     "Smartphone with 12 MP camera and Touch ID",                true),
                new Product("889",  "Apple iPhone 14 Pro",        999.99, "Electronics", "Apple",     "Latest iPhone with 48 MP camera and Dynamic Island",       true),
                new Product("890",  "Samsung Galaxy S23",         799.99, "Electronics", "Samsung",   "Android flagship with 200 MP camera",                      true),
                new Product("891",  "Samsung Galaxy A54",         349.99, "Electronics", "Samsung",   "Mid-range Android phone with great battery life",           true),
                new Product("892",  "Google Pixel 7",             599.99, "Electronics", "Google",    "Pure Android experience with Tensor G2 chip",               false),
                new Product("893",  "OnePlus 11",                 699.99, "Electronics", "OnePlus",   "Fast charging flagship with Snapdragon 8 Gen 2",            true),
                new Product("894",  "Sony WH-1000XM5",            349.99, "Electronics", "Sony",      "Industry-leading noise-cancelling headphones",               true),
                new Product("895",  "Apple AirPods Pro 2",        249.99, "Electronics", "Apple",     "Active noise cancellation with H2 chip",                    true),
                new Product("896",  "Dell XPS 15 Laptop",        1299.99, "Computers",   "Dell",      "15-inch OLED laptop with Intel Core i7",                    true),
                new Product("897",  "MacBook Air M2",            1099.99, "Computers",   "Apple",     "Ultra-thin laptop powered by Apple M2 chip",                true),
                new Product("898",  "Lenovo ThinkPad X1 Carbon",  999.99, "Computers",   "Lenovo",    "Business ultrabook with military-grade durability",          false),
                new Product("899",  "HP Pavilion 15",             549.99, "Computers",   "HP",        "Everyday laptop with AMD Ryzen 5 processor",                true),
                new Product("900",  "Sony PlayStation 5",         499.99, "Gaming",      "Sony",      "Next-gen gaming console with 4K gaming and SSD",            true),
                new Product("901",  "Microsoft Xbox Series X",    499.99, "Gaming",      "Microsoft", "Xbox Series X with 12 TFLOPS GPU and Quick Resume",         false),
                new Product("902",  "Nintendo Switch OLED",       349.99, "Gaming",      "Nintendo",  "Hybrid console with vibrant 7-inch OLED display",           true),
                new Product("903",  "LG 55-inch OLED TV",        1499.99, "TVs",         "LG",        "4K OLED TV with Dolby Vision and NVIDIA G-Sync",            true),
                new Product("904",  "Samsung 65-inch QLED TV",   1199.99, "TVs",         "Samsung",   "Quantum dot 4K TV with 120 Hz refresh rate",                true),
                new Product("905",  "Canon EOS R6 Mark II",      2499.99, "Cameras",     "Canon",     "Full-frame mirrorless camera with 40 fps burst mode",        true),
                new Product("906",  "Sony Alpha A7 IV",          2499.99, "Cameras",     "Sony",      "33 MP full-frame mirrorless with 4K60 video",               true),
                new Product("907",  "Bose QuietComfort 45",       279.99, "Electronics", "Bose",      "Wireless noise-cancelling headphones with 24 hr battery",   true)
        );

        try {
            solr.addBeans(products);   // batch index — more efficient than one-by-one addBean calls
            solr.commit();
            System.out.println("Indexed " + products.size() + " products successfully.");
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes a Solr query with optional sort, field list, and row limit.
     *
     * @param solr      the Solr client
     * @param queryStr  Solr query string (supports full-text, range, and boolean queries)
     * @param sortField field to sort by, or null for relevance ranking
     * @param sortOrder "asc" or "desc", ignored when sortField is null
     * @param fl        comma-separated list of fields to return
     * @param rows      maximum number of results to return
     */
    private static QueryResponse query(HttpSolrClient solr, String queryStr,
                                       String sortField, String sortOrder,
                                       String fl, int rows) {
        try {
            SolrQuery query = new SolrQuery();
            query.set("q", queryStr);
            query.set("fl", fl);
            query.setRows(rows);
            if (sortField != null) {
                query.setSort(sortField,
                        "asc".equalsIgnoreCase(sortOrder) ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc);
            }
            return solr.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Prints result count and each document returned by a query. */
    private static void printResults(QueryResponse response) {
        if (response == null) {
            System.out.println("No response received.");
            return;
        }
        SolrDocumentList docs = response.getResults();
        System.out.println("Found " + docs.getNumFound() + " document(s). Showing " + docs.size() + ":");
        for (SolrDocument doc : docs) {
            System.out.println("  " + doc);
        }
    }
}
