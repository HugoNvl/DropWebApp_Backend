package projet.dev_web_advanced.Generation_image;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;


import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserDAO {
    
    @Autowired
    private SessionFactory sessionFactory;

    public Long createUser(User newUser) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(newUser);
        return newUser.getId();
    }

    public void modifyUser(User u) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(u);
    }

    public void deleteUser(User u) {
        Session session = this.sessionFactory.getCurrentSession();
        session.delete(u);
    }

    public User getUser(Long Id) {
        Session session = this.sessionFactory.getCurrentSession();
        Query<User> q = session.createQuery("FROM User u WHERE u.id = :id", User.class);
        q.setParameter("id", Id);
        System.out.println(q.getResultList().get(0).getClass().getName());
        return q.getResultList().get(0);
    }

    public User getUser(String username, String password) {
        Session session = this.sessionFactory.getCurrentSession();
             
        Query<User> q = session.createQuery("FROM User u WHERE u.name = :name AND u.password = :password", User.class);
        q.setParameter("name", username);
        q.setParameter("password", password);
        User u = q.getResultList().get(0);
        return u;
        
    }
}
