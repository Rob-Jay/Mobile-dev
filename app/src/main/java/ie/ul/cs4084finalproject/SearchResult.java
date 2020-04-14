package ie.ul.cs4084finalproject;

import android.widget.ImageView;

public class SearchResult {

    private String advertPrice;
    private String advertQuality;
    private String advertTitle;
    private String advertDescription;
    private ImageView advertImage;



    public SearchResult(){
    // empty constructor
    }

    public SearchResult(String advertTitle, String advertDescription,String advertPrice, String advertQuality, ImageView advertImage){
        this.advertTitle=advertTitle;
        this.advertDescription=advertDescription;
        this.advertImage=advertImage;
        this.advertPrice = advertPrice;
        this.advertQuality=advertQuality;

    }

    public String getAdvertTitle() {
        return advertTitle;
    }

    public String getAdvertDescription() {
        return advertDescription;
    }

    public ImageView getAdvertImage() {
        return advertImage;
    }
    public String getAdvertPrice() {
        return advertPrice;
    }

    public String getAdvertQuality() {
        return advertQuality;
    }

}
