package ph.com.bpi.training;

import java.util.List;

public interface Repository<T, ID> {

    T save(T entity);

    void delete(T entity);

    void deleteById(ID id);

    T findById(ID id);

    List<T> findAll();
}
