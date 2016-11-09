package dao;

import java.util.Collection;
import java.util.List;

public interface DAO <T>{

    boolean save(T t);

    boolean delete(T t);

    T findById(long id);

    List<T> getAll();

    void syncListToDB();

}
