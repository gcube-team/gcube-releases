package gr.cite.regional.data.collection.application.dtos;

public class ImportToTabmanDto implements Dto {
    private String scope;
    private String token;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
