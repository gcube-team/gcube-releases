package gr.cite.geoanalytics.dataaccess.entities.userworkspacelayer.dao;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.layer.UserWorkspaceLayer;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserWorkspaceLayerDaoImpl extends JpaDao<UserWorkspaceLayer, UUID> implements UserWorkspaceLayerDao {
    @Override
    public UserWorkspaceLayer loadDetails(UserWorkspaceLayer userWorkspaceLayer) {
        return null;
    }
}
