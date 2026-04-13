package cinema.DAO;

import java.sql.Connection;
import java.util.List;

public abstract class DAO<T> {

    protected Connection connect = DBManager.getInstance();

    public abstract boolean create(T obj);

    public abstract boolean delete(T obj);

    public abstract boolean update(T obj);

    public abstract T find(int id);

    public abstract List<T> findAll();
}