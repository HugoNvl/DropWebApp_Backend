package projet.dev_web_advanced.Generation_image;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CollectionController {
    
    private CollectionDAO dao = new CollectionDAO();
    private UserDAO user_DAO = new UserDAO();
    private ImageDAO image_DAO = new ImageDAO();

    @PostMapping(value="api/collection/getCollection")
    public ResponseEntity<Collection> getCollection(@RequestBody Long Id_collection) {
        Collection col = dao.getCollection(Id_collection);
        if (col != null) {
            return ResponseEntity.ok(col);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value="api/collection/setCollection")
    public ResponseEntity<Collection> setCollection(@RequestBody Collection col) {
        if (col != null) {
            dao.modifyCollection(col);
            Collection new_col = dao.getCollection(col.getId());
            return ResponseEntity.ok(new_col);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value="api/collection/createCollection")
    public ResponseEntity<Collection> createCollection(@RequestBody String name, Long Id_creator) {
        Collection col = new Collection();
        col.setName(name);
        col.setCreator(user_DAO.getUser(Id_creator));
        try {
            dao.createCollection(col);
            Collection new_col = dao.getCollection(col.getId());
            return ResponseEntity.ok(new_col);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping(value="api/collection/deleteCollection")
    public ResponseEntity<String> deleteCollection(@RequestBody Long id) {
        dao.deleteCollection(dao.getCollection(id));
        Collection deleted = dao.getCollection(id);
        if (deleted == null) {
            return ResponseEntity.ok("Collection successfully deleted");
        } else {
            return ResponseEntity.status(500).body("Connection not deleted");
        }
    }

    @PostMapping(value="api/collection/getImagesOfCollection")
    public ResponseEntity<List<Image>> getImagesOfCollection(@RequestBody Long Id_collection) {
        List<Image> list = image_DAO.getImage(dao.getCollection(Id_collection));
        if (list != null) {
            return ResponseEntity.ok(list);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
