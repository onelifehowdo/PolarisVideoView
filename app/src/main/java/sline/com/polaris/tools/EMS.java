package sline.com.polaris.tools;

/**
 * Created by dell on 2018/8/31.
 */

public class EMS {
    String video, image;
    Double size;
    Long downloadTime;

    public EMS(String video, String image, Double size, Long downloadTime) {
        this.video = video;
        this.image = image;
        this.size = size;
        this.downloadTime=downloadTime;

    }

    public String getVideo() {
        return video;
    }

    public String getImage() {
        return image;
    }

    public Double getSize() {
        return size;
    }

    public Long getDownloadTime() {
        return downloadTime;
    }
}
