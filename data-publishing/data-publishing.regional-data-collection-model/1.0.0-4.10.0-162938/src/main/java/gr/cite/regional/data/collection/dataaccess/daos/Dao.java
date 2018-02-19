package gr.cite.regional.data.collection.dataaccess.daos;

import java.io.Serializable;
import java.util.List;

import gr.cite.regional.data.collection.dataaccess.entities.Entity;

public interface Dao<T extends Entity, PK extends Serializable> {
    public T create(T t);
    public T read(PK id);
    public T update(T t);
    public void delete(T t);
    
    public List<T> getAll();
    public long count();
    
    public T loadDetails(T t);
    public boolean isLoaded(T t);
}