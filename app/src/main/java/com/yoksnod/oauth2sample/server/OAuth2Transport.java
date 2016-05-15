package com.yoksnod.oauth2sample.server;

import android.content.SharedPreferences;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by d.donskoy on 02.05.2016.
 */
public class OAuth2Transport {

    private final AuthorizationCodeFlow mAuthFlow;
    private OAuth2Params mParams;

    public OAuth2Transport(OAuth2Params params) {
        mParams = params;
        CredentialStore credentialStore = new StubCredentialStore();
        final Credential.AccessMethod accessMethod = BearerToken.authorizationHeaderAccessMethod();

        final ClientParametersAuthentication auth = new ClientParametersAuthentication(
                params.getClientId(),
                params.getClientSecret());
        mAuthFlow = new AuthorizationCodeFlow
                .Builder(
                    accessMethod,
                    new NetHttpTransport(),
                    new JacksonFactory(),
                    new GenericUrl(params.getTokenUri()),
                    auth,
                    params.getClientId(),
                    params.getAuthUri())
                .setCredentialStore(credentialStore)
                .build();
    }


    private Collection<String> preparePermissionsScope(String permmissions) {
        String[] result = permmissions.split(",");
        Collection<String> collection = new ArrayList<String>();
        Collections.addAll(collection, result);
        return collection;
    }

    public String getAuthUri() {
        String authUrl = mAuthFlow
                .newAuthorizationUrl()
                .setRedirectUri(mParams.getRedirectUri())
                .setScopes(preparePermissionsScope(mParams.getScope()))
                .build();
        return authUrl;
    }

    public TokenResponse getAccessTokenBlocking(String authCode) throws IOException {
        TokenResponse resp = mAuthFlow
                .newTokenRequest(authCode)
                .setScopes(preparePermissionsScope(mParams.getScope()))
                .setRedirectUri(mParams.getRedirectUri())
                .execute();
        return resp;
    }

    static class StubCredentialStore implements CredentialStore {


        @Override
        public boolean load(String userId, Credential credential) throws IOException {
            return false;
        }

        @Override
        public void store(String userId, Credential credential) throws IOException {

        }

        @Override
        public void delete(String userId, Credential credential) throws IOException {

        }
    }
}
