package sline.com.polaris.tools;

/**
 * Created by dell on 2018/3/20.
 */

public class VideoBean {
    private String imageLeft;
    private String imageRight;
    private String nameLeft;
    private String nameRight;
    private Double sizeLeft;
    private Double sizeRight;
    private Long videoTimeLeft,videoTimeRight;

    public VideoBean(String imageLeft, String imageRight, String nameLeft, String nameRight, Double sizeLeft, Double sizeRight,Long videoTimeLeft,Long videoTimeRight) {
        this.imageLeft = imageLeft;
        this.imageRight = imageRight;
        this.nameLeft = nameLeft;
        this.nameRight = nameRight;
        this.sizeLeft = sizeLeft;
        this.sizeRight = sizeRight;
        this.videoTimeLeft=videoTimeLeft;
        this.videoTimeRight=videoTimeRight;
    }

    public Double getSizeLeft() {
        return sizeLeft;
    }

    public Double getSizeRight() {
        return sizeRight;
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

    public Long getVideoTimeLeft() {
        return videoTimeLeft;
    }

    public Long getVideoTimeRight() {
        return videoTimeRight;
    }
}
