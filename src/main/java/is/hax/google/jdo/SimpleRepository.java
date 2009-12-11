package is.hax.google.jdo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jdo.JdoCallback;
import org.springframework.orm.jdo.JdoTemplate;
import org.springframework.stereotype.Service;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.Collection;
import java.util.Map;

import is.hax.autobox.Entity;
import is.hax.autobox.Function;

/**
 * User: pjesi
 * Date: Oct 15, 2009
 * Time: 10:14:00 PM
 */
@Service
public class SimpleRepository {


    protected JdoTemplate jdoTemplate;

    @Autowired
    public SimpleRepository(PersistenceManagerFactory pmf) {
        jdoTemplate = new JdoTemplate(pmf);
    }


    public <T> Collection<T> getAllObjects(final Class<T> objClass) {
        @SuppressWarnings("unchecked")
        Collection<T> collection = jdoTemplate.executeFind(new JdoCallback<Collection<T>>() {
            public Collection<T> doInJdo(PersistenceManager pm) throws JDOException {
                Query query = pm.newQuery(objClass);

                @SuppressWarnings("unchecked")
                Collection<T> result = (Collection<T>) query.execute();
                result.size();
                return result;
            }
        });

        return collection;
    }


    public <T extends Entity> T store(T t) {

        return jdoTemplate.makePersistent(t);

    }

    public <T> T get(Class<T> type, Object id) {

        return jdoTemplate.getObjectById(type, id);

    }

    public <T extends Entity> T update(final T t, final Function<T, T> function) {

        return jdoTemplate.execute(new JdoCallback<T>() {
            public T doInJdo(PersistenceManager pm) throws JDOException {
                @SuppressWarnings("unchecked")
                T persistent = (T) pm.getObjectById(t.getClass(), t.getId());
                return function.call(persistent);
            }
        });

    }

    /* experimental */
    public <T extends Entity, V extends Entity> void update(final T t, final V v,
                                                            final Function<T, T> functionT,
                                                            final Function<V, V> functionV) {


        jdoTemplate.execute(new JdoCallback<T>() {
            public T doInJdo(PersistenceManager pm) throws JDOException {

                @SuppressWarnings("unchecked")
                T persistent = (T) pm.getObjectById(t.getClass(), t.getId());
                functionT.call(persistent);

                @SuppressWarnings("unchecked")
                V persistentV = (V) pm.getObjectById(v.getClass(), v.getId());
                functionV.call(persistentV);

                return null;
            }
        });

    }


    public <T> Collection<T> query(final Class<T> type,
                                   final String filter,
                                   final Map<String, ?> params) {

        @SuppressWarnings("unchecked")
        Collection<T> collection = jdoTemplate.executeFind(new JdoCallback() {
            public Object doInJdo(PersistenceManager pm) throws JDOException {
                Query query = pm.newQuery(type, filter);


                @SuppressWarnings("unchecked")
                Collection<T> result = (Collection<T>) query.executeWithMap(params);
                result.size();
                return result;
            }
        });

        return collection;


    }

    public JdoTemplate getJdoTemplate() {
        return jdoTemplate;
    }
}

