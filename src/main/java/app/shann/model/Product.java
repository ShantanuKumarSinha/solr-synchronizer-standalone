package app.shann.model;

import org.apache.solr.client.solrj.beans.Field;

public class Product {

    @Field("id")
    private String id;

    @Field("name")
    private String name;

    @Field("price")
    private Double price;

    @Field("category")
    private String category;

    @Field("brand")
    private String brand;

    @Field("description")
    private String description;

    @Field("inStock")
    private boolean inStock;

    // Required no-arg constructor for SolrJ bean binding
    public Product() {}

    public Product(String id, String name, Double price, String category, String brand, String description, boolean inStock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.brand = brand;
        this.description = description;
        this.inStock = inStock;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getBrand() { return brand; }
    public String getDescription() { return description; }
    public boolean isInStock() { return inStock; }

    protected void setId(String id) { this.id = id; }
    protected void setName(String name) { this.name = name; }
    protected void setPrice(Double price) { this.price = price; }
    protected void setCategory(String category) { this.category = category; }
    protected void setBrand(String brand) { this.brand = brand; }
    protected void setDescription(String description) { this.description = description; }
    protected void setInStock(boolean inStock) { this.inStock = inStock; }
}

