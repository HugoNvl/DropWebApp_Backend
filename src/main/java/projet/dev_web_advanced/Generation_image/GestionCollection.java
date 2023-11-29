package projet.dev_web_advanced.Generation_image;

import java.util.List;

public class GestionCollection {
    
    CollectionDAO dao;

    public GestionCollection() {

    }

    public Long createCollection(Collection c) {
        dao.createCollection(c);
        return c.getId();
    }

    public void modifyCollection(Collection c) {
        dao.modifyCollection(c);
    }

    public void deleteCollection(Collection c) {
        dao.deleteCollection(c);
    }

    public List<Collection> getCollections(User u) {
        List<Collection> list_coll = dao.findCollection(u);
        return list_coll;
    }
}
