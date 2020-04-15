package ie.ul.cs4084finalproject;

public class SearchResult {

    private String price;
    private String quality;
    private String title;
    private String description;
    // private ImageView advertImage;


    public SearchResult() {
        // empty constructor
    }

    public SearchResult(String price, String quality, String title, String description) {
        this.price = price;
        this.quality = quality;
        this.title = title;
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

