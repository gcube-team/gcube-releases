package gr.cite.geoanalytics.web.auth;

public interface UserIdentificationService {
    UserProfile getUserProfile(String userToken, String scope) throws ServiceDiscoveryException;
}
