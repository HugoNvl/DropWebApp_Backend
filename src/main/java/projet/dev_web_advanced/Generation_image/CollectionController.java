package projet.dev_web_advanced.Generation_image;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class CollectionController {
    
    private CollectionDAO dao;
    private UserDAO user_DAO;
    private ImageDAO image_DAO;

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
        dao.createCollection(col);
        Collection new_col = dao.getCollection(col.getId());
        return ResponseEntity.ok(new_col);
    }

    @PostMapping(value="api/collection/deleteCollection")
    public ResponseEntity<String> deleteCollection(@RequestBody Collection col) {
        dao.deleteCollection(col);
        Collection deleted = dao.getCollection(col.getId());
        if (deleted == null) {
            return ResponseEntity.ok("Collection successfully deleted");
        } else {
            return ResponseEntity.status(500).body("Connection not deleted");
        }
    }

    @PostMapping(value="api/collection/findCollection")
    public ResponseEntity<List<Collection>> findCollection(@RequestBody Long Id_creator) {
        List<Collection> list = dao.findCollection(user_DAO.getUser(Id_creator));
        if (list != null) {
            return ResponseEntity.ok(list);
        } else {
            return ResponseEntity.notFound().build();
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
