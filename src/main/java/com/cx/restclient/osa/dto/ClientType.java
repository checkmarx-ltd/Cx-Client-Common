package com.cx.restclient.osa.dto;

public class ClientType {

    public static final ClientType RESOURCE_OWNER = new ClientType("resource_owner_client",
            "sast_rest_api cxarm_api",
            "014DF517-39D1-4453-B7B3-9930C563627C");

    public static final ClientType CLI = new ClientType("cli_client",
            "sast_rest_api offline_access",
            "B9D84EA8-E476-4E83-A628-8A342D74D3BD");

    private String clientId;
    private String scopes;
    private String clientSecret;

    public ClientType(String clientId, String scopes, String clientSecret) {
        this.clientId = clientId;
        this.scopes = scopes;
        this.clientSecret = clientSecret;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public String getClientId() {
        return clientId;
    }
}
