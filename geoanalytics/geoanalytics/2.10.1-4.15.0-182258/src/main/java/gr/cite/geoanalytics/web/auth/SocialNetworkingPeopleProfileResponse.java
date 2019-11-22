package gr.cite.geoanalytics.web.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SocialNetworkingPeopleProfileResponse {
    private boolean success;
    private String message;
    private SocialNetworkingUserProfile result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SocialNetworkingUserProfile getResult() {
        return result;
    }

    public void setResult(SocialNetworkingUserProfile result) {
        this.result = result;
    }
}
