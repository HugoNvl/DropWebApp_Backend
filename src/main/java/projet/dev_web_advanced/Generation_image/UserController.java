package projet.dev_web_advanced.Generation_image;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class UserController {

    private UserDAO dao;
    private CollectionDAO collection_dao;

    @PostMapping(value="api/user/createAccount")
    public ResponseEntity<User> createAccount(@RequestBody String mail_adress, String name, String password) {
        User u = new User();
        u.setMail_adress(mail_adress);
        u.setName(name);
        u.setPassword(password);
        try{
            dao.createUser(u);
            return ResponseEntity.ok(u);
        } catch(Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping(value="api/user/connect")
    public ResponseEntity<User> connect(@RequestBody String username, String password) {
        User u = dao.getUser(username, password);
        if(u != null) {
            return ResponseEntity.ok(u);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping(value="api/user/disconnect")
    public void disconnect(@RequestBody Long id) {
        User u = dao.getUser(id);
        u.setConnected(false);
        dao.modifyUser(u);        
    }
    

    @PostMapping(value="api/user/getUserCollections")
    public ResponseEntity<List<Collection>> getUserCollections(@RequestBody Long id) {
        List<Collection> list_collections = collection_dao.findCollection(id);
        if(list_collections != null) {
            return ResponseEntity.ok(list_collections);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value="api/user/getUserImages")
    public ResponseEntity<List<Image>> getUserImages(@RequestBody Long id) {
        User user = dao.getUser(id);
        List<Image> list_images = user.getImages();
        if(list_images != null) {
            return ResponseEntity.ok(list_images);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value="api/user/modifyUser")
    public ResponseEntity<User> modifyUser(@RequestBody User u) {
        dao.modifyUser(u);
        User userUpdated = dao.getUser(u.getId());
        if(userUpdated != null) {
            return ResponseEntity.ok(userUpdated);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value="api/user/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestBody Long id) {
        dao.deleteUser(dao.getUser(id));
        User user = dao.getUser(id);
        if(user == null) {
            return ResponseEntity.ok("Account deleted with success");
        }
        else {
            return ResponseEntity.status(500).body("An error occured");}
    }
}
