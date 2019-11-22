package gr.cite.geoanalytics.web.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.Response;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SocialNetworkingService implements UserIdentificationService {
    private static final Logger _log = LoggerFactory.getLogger(SocialNetworkingService.class);

    private String serviceName;
    private String socialNetworkingRestServiceName;
    private String socialNetworkingRestServiceClass;
    private String socialNetworkingRestServiceEndsWith;
    private String socialNetworkingRestServiceURL = null;
    private String socialNetworkingRestServiceJsonStatus;
    private String socialNetworkingRestServiceJsonStatusMessage;
    private String socialNetworkingRestServiceJsonJobName;
    private String socialNetworkingRestServicesAndUserNotificationEndpoint;
    private String socialNetworkingUserProfileEndpoint;

    public SocialNetworkingService(String socialNetworkingRestServiceName,
                                   String socialNetworkingRestServiceClass,
                                   String socialNetworkingRestServiceEndsWith,
                                   String socialNetworkingRestServiceJsonStatus,
                                   String socialNetworkingRestServiceJsonStatusMessage,
                                   String socialNetworkingRestServiceJsonJobName,
                                   String serviceName,
                                   String socialNetworkingRestServicesAndUserNotificationEndpoint,
                                   String socialNetworkingUserProfileEndpoint) {
        this.socialNetworkingRestServiceName = socialNetworkingRestServiceName;
        this.socialNetworkingRestServiceClass = socialNetworkingRestServiceClass;
        this.socialNetworkingRestServiceJsonStatus = socialNetworkingRestServiceJsonStatus;
        this.socialNetworkingRestServiceJsonStatusMessage = socialNetworkingRestServiceJsonStatusMessage;
        this.socialNetworkingRestServiceJsonJobName = socialNetworkingRestServiceJsonJobName;
        this.socialNetworkingRestServiceEndsWith = socialNetworkingRestServiceEndsWith == null ? "" : socialNetworkingRestServiceEndsWith;
        this.serviceName = serviceName;
        this.socialNetworkingRestServicesAndUserNotificationEndpoint = socialNetworkingRestServicesAndUserNotificationEndpoint;
        this.socialNetworkingUserProfileEndpoint = socialNetworkingUserProfileEndpoint;
    }

    @Override
    public UserProfile getUserProfile(String userToken, String scope) throws ServiceDiscoveryException {
        HttpClientSocial client = new HttpClientSocial();

        Map<String, Object> headers = new HashMap<>();
        headers.put("gcube-token", userToken);

        String url = this.getSocialNetworkingRestServiceURL( scope ) + this.socialNetworkingUserProfileEndpoint;
        _log.debug("Retrieving user\'s profile info from endpoint: " +  url );

        Response response = client.doGet(url, headers);
        SocialNetworkingPeopleProfileResponse profileResponse = response.readEntity(SocialNetworkingPeopleProfileResponse.class);

        return mapSocialNetworkingUserProfileToUserProfile(profileResponse.getResult());
    }

    private UserProfile mapSocialNetworkingUserProfileToUserProfile(SocialNetworkingUserProfile socialNetworkingUserProfile) {
        UserProfile userProfile = null;

        if (socialNetworkingUserProfile != null) {
            userProfile = new UserProfile();

            userProfile.setEmail(socialNetworkingUserProfile.getUsername());
            userProfile.setUsername(socialNetworkingUserProfile.getUsername());
            userProfile.setFullname(socialNetworkingUserProfile.getFullname());
            userProfile.setUri(socialNetworkingUserProfile.getAvatar());
            userProfile.setRoles(socialNetworkingUserProfile.getRoles());
        }

        return userProfile;
    }

    private String discoverSocialNetworkingRestService(String scope) throws ServiceDiscoveryException {
        EndpointManager em = new EndpointManager();
        String theUrl = em.getServiceEndpoints(scope, this.getSocialNetworkingRestServiceProfile()).stream().filter(url -> url.indexOf("rest") > - 1).collect(Collectors.toList()).get(0);

        _log.info("Social Networking Rest Service discovered: " + theUrl);
        return theUrl;
    }

    private ServiceProfile getSocialNetworkingRestServiceProfile() {
        ServiceProfile socialNetworkingRestServiceProfile = new ServiceProfile();
        socialNetworkingRestServiceProfile.setServiceClass(this.socialNetworkingRestServiceClass);
        socialNetworkingRestServiceProfile.setServiceName(this.socialNetworkingRestServiceName);
        socialNetworkingRestServiceProfile.setPathEndsWith(this.socialNetworkingRestServiceEndsWith);

        return socialNetworkingRestServiceProfile;
    }

    public String getSocialNetworkingRestServiceURL(String scope) throws ServiceDiscoveryException {
        return discoverSocialNetworkingRestService(scope);
//        return this.socialNetworkingRestServiceURL == null ? discoverSocialNetworkingRestService(scope) : this.socialNetworkingRestServiceURL;
    }
}
