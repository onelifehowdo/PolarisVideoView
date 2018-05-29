package sline.com.polaris;

/**
 * Created by dell on 2018/3/20.
 */

public class VideoBean {
    private String imageLeft;
    private String imageRight;
    private String nameLeft;
    private String nameRight;

    public VideoBean(String nameLeft, String nameRight) {
        this.nameLeft = nameLeft;
        this.nameRight = nameRight;
        this.imageLeft=nameLeft+".idk1";
        this.imageRight=nameRight+".idk1";
    }

    public String getImageLeft() {
        return imageLeft;
    }

    public String getImageRight() {
        return imageRight;
    }

    public String getNameLeft() {
        return nameLeft;
    }

    public String getNameRight() {
        return nameRight;
    }
}
