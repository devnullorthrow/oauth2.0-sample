package com.yoksnod.oauth2sample.server;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by d.donskoy on 02.05.2016.
 */
public class OAuth2Params implements Parcelable {

    private final String mClientId;
    private final String mClientSecret;
    private final String mAuthUri;
    private final String mTokenUri;
    private final String mRedirectUri;
    private final String mScope;


    private OAuth2Params(String clientId,
                         String clientSecret,
                         String authUri,
                         String tokenUri,
                         String redirectUri,
                         String scope) {
        mClientId = clientId;
        mClientSecret = clientSecret;
        mAuthUri = authUri;
        mTokenUri = tokenUri;
        mRedirectUri = redirectUri;
        mScope = scope;
    }

    public String getClientId() {
        return mClientId;
    }

    public String getClientSecret() {
        return mClientSecret;
    }

    public String getAuthUri() {
        return mAuthUri;
    }

    public String getTokenUri() {
        return mTokenUri;
    }

    public String getRedirectUri() {
        return mRedirectUri;
    }

    public String getScope() {
        return mScope;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mClientId);
        dest.writeString(this.mClientSecret);
        dest.writeString(this.mAuthUri);
        dest.writeString(this.mTokenUri);
        dest.writeString(this.mRedirectUri);
        dest.writeString(this.mScope);
    }

    protected OAuth2Params(Parcel in) {
        this.mClientId = in.readString();
        this.mClientSecret = in.readString();
        this.mAuthUri = in.readString();
        this.mTokenUri = in.readString();
        this.mRedirectUri = in.readString();
        this.mScope = in.readString();
    }

    public static final Creator<OAuth2Params> CREATOR = new Creator<OAuth2Params>() {
        @Override
        public OAuth2Params createFromParcel(Parcel source) {
            return new OAuth2Params(source);
        }

        @Override
        public OAuth2Params[] newArray(int size) {
            return new OAuth2Params[size];
        }
    };

    public static class Builder {
        private String mClientId;
        private String mClientSecret;
        private String mAuthUri;
        private String mTokenUri;
        private String mRedirectUri;
        private String mScope;

        public Builder(OAuth2Params params) {
            setClientId(params.mClientId)
                    .setClientSecret(params.mClientSecret)
                    .setAuthUri(params.mAuthUri)
                    .setTokenUri(params.mTokenUri)
                    .setScope(params.mScope);
        }

        public Builder() {}

        public Builder setClientId(String clientId) {
            mClientId = clientId;
            return this;
        }

        public Builder setClientSecret(String clientSecret) {
            mClientSecret = clientSecret;
            return this;
        }

        public Builder setAuthUri(String authUri) {
            mAuthUri = authUri;
            return this;
        }

        public Builder setTokenUri(String tokenUri) {
            mTokenUri = tokenUri;
            return this;
        }

        public Builder setRedirectUri(String redirectUri) {
            mRedirectUri = redirectUri;
            return this;
        }

        public Builder setScope(String scope) {
            mScope = scope;
            return this;
        }

        public OAuth2Params build() {
            return new OAuth2Params(
                    mClientId,
                    mClientSecret,
                    mAuthUri,
                    mTokenUri,
                    mRedirectUri,
                    mScope);
        }
    }
}
