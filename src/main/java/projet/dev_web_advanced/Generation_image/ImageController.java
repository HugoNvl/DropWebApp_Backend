package projet.dev_web_advanced.Generation_image;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class ImageController {

    private ImageDAO dao;
    private CollectionDAO collection_DAO;

    @PostMapping("/api/image/getImage")
    public ResponseEntity<Image> getImage(@RequestBody Long id) {
        Image i = dao.getImage(id);

        if (i != null) {
            return ResponseEntity.ok(i);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value="/api/image/setImage")
    public void setImage(@RequestBody Image i) {
        dao.modifyImage(i);
    }

    @PostMapping(value="/api/image/addImageToCollection")
    public ResponseEntity<String> addImageToCollection(@RequestBody Long Id_collection, Long Id_image) {
        Image i = dao.getImage(Id_image);
        Collection c = collection_DAO.getCollection(Id_collection);
        List<Image> list_image = c.getList_images();
        int length = list_image.size();
        list_image.add(i);
        c.setList_images(list_image);
        collection_DAO.modifyCollection(c);
        if (length == list_image.size()-1) {
            return ResponseEntity.ok("Image added");
        } else {
            return ResponseEntity.status(500).body("An error occured");
        }
    }

    @PostMapping(value="/api/image/removeImageFromCollection")
    public ResponseEntity<String> removeImageFromCollection(@RequestBody Long Id_collection, Long Id_image) {
        Image i = dao.getImage(Id_image);
        Collection c = collection_DAO.getCollection(Id_collection);
        List<Image> list_image = c.getList_images();
        int length = list_image.size();
        list_image.remove(i);
        c.setList_images(list_image);
        collection_DAO.modifyCollection(c);
        if (length == list_image.size()+1) {
            return ResponseEntity.ok("Image removed");
        } else {
            return ResponseEntity.status(500).body("An error occured");
        }
    }

    @PostMapping(value="/api/image/generate")
    public ResponseEntity<Image> generateImage(@RequestBody User user, String instruction, String selectedButtons, String buttonLabelsByTab, int width, int height, String seed, int generationSteps, float guidanceScale) {
        String url = "";
        Image newImage = new Image();
        newImage.setCreator(user);
        newImage.setPrompt(instruction);
        //TODO: other fields

        dao.createImage(newImage);

        return ResponseEntity.ok(new Image());
    }

}
