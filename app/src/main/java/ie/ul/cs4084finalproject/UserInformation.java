package ie.ul.cs4084finalproject;

public class UserInformation {
    private String name;
    private String image_src;
    private String title;

    public UserInformation(){

    }

    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getImage_src(){
        return image_src;
    }
    public void setImage_src(String image_src){
        this.image_src=image_src;
    }
}
