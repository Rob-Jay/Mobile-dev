package ie.ul.cs4084finalproject;

public class Advertisement {

    private String title;
    private String imageUrl;
    private double price;
    private String quality;
    private int distance;
    private String seller;

    public Advertisement(String ttl, String img, double prc, String qlty, int dst, String slr) {
        title = ttl;
        imageUrl = img;
        price = prc;
        seller = slr;
        quality = qlty;
        distance = dst;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public String getSeller() {
        return seller;
    }

    public String getQuality() {
        return quality;
    }

    public int getDistance() {
        return distance;
    }
}
