package projet.dev_web_advanced.Generation_image;

import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CollectionDAO {
    
    @Autowired
    private SessionFactory sessionFactory;

    public void createCollection(Collection newCollection) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(newCollection);
    }

    public void modifyCollection(Collection c) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(c);
    }

    public void deleteCollection(Collection c) {
        Session session = this.sessionFactory.getCurrentSession();
        session.delete(c);
    }

    public Collection getCollection(Long id) {
        Session session = this.sessionFactory.getCurrentSession();
        Collection c = session.find(Collection.class, id);
        return c;
    }

    public List<Collection> findCollection(User creator) {
        Session session = this.sessionFactory.getCurrentSession();
        Query<Collection> query = session.createQuery("FROM Collection c WHERE c.creator LIKE :creator", Collection.class);
        query.setParameter("creator", creator);
        return query.getResultList();
    }
}
