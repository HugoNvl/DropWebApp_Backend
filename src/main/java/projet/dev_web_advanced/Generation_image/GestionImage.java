package projet.dev_web_advanced.Generation_image;

import java.util.List;

public class GestionImage {
    
    ImageDAO dao;

    public GestionImage() {

    }

    public Long createImage(Image i) {
        dao.createImage(i);
        return i.getId();
    }

    public void modifyImage(Image i) {
        dao.modifyImage(i);
    }

    public void deleteImage(Image i) {
        dao.deleteImage(i);
    }

    public List<Image> getImages(String prompt) {
        List<Image> list_image = dao.getImage(prompt);
        return list_image;
    }

    public List<Image> getImages(User u) {
        List<Image> list_image = dao.getImage(u);
        return list_image;
    }

    public Image getImage(Long Id) {
        Image i = dao.getImage(Id);
        return i;
    }
}
