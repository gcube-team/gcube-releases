package gr.cite.regional.data.collection.dataaccess.daos;

import gr.cite.regional.data.collection.dataaccess.entities.UserReference;

import java.util.List;

public interface UserReferenceDao extends Dao<UserReference, Integer> {
    public List<UserReference> getUserReferenceByUsername(String username);
}
