package gr.cite.geoanalytics.dataaccess.dao;

import gr.cite.geoanalytics.dataaccess.entities.Entity;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public interface Dao<T extends Entity, PK extends Serializable> {
    public T create(T t);
    public T read(PK id);
    public T update(T t);
    public void delete(T t);
//    public int deleteById(UUID uuid);

    public List<T> getAll();
    public long count();
    
    public T loadDetails(T t);
    public boolean isLoaded(T t);
}
