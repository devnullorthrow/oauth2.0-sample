package com.yoksnod.oauth2sample.server;

/**
 * Created by d.donskoy on 10.05.2016.
 */
public abstract class VkOAuth2Params {
    public static final String CLIENT_ID = "stub_client_id";
    public static final String CLIENT_SECRET = "stub_client_secret";
    public static final String REDIRECT_URI = "https://oauth.vk.com/blank.html";

    public static final String AUTH_SERVER_URL = "https://oauth.vk.com/authorize";

    public static final String TOKEN_SERVER_URL = "https://oauth.vk.com/access_token";
    public static final String SCOPE = "friends,audio,docs";
}
