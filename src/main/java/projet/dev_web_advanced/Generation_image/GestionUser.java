package projet.dev_web_advanced.Generation_image;

public class GestionUser {
    
    UserDAO dao;

    public GestionUser() {

    }

    public Long createUser(User u) {
        dao.createUser(u);
        return u.getId();
    }

    public void modifyUser(User u) {
        dao.modifyUser(u);
    }

    public void deleteUser(User u) {
        dao.deleteUser(u);
    }

    public User getUser(Long Id) {
        User u = dao.getUser(Id);
        return u;
    }
}
