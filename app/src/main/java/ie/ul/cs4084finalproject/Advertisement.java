package ie.ul.cs4084finalproject;

import com.google.android.gms.maps.model.LatLng;

public class Advertisement {

    private String advertisement_id;
    private String title;
    private String description;
    private String imageUrl;
    private double price;
    private String quality;
    private int distance;
    private String seller;
    private LatLng location;

    public Advertisement(String ttl, String img, double prc, String qlty, int dst, String slr, String desc) {
        title = ttl;
        imageUrl = img;
        price = prc;
        seller = slr;
        quality = qlty;
        distance = dst;
        description = desc;
    }

    public Advertisement(String id, String ttl, String img, double prc, String qlty, int dst, String slr) {
        advertisement_id = id;
        title = ttl;
        imageUrl = img;
        price = prc;
        seller = slr;
        quality = qlty;
        distance = dst;
    }

    public String getAdvertisement_id() { return advertisement_id; }

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

    public String getDescription() { return description; }

    public void setLocation(LatLng point) {
        location = point;
    }

    public LatLng getLocation(){ return location; }
}
