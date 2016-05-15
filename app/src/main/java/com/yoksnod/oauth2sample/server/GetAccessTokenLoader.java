package com.yoksnod.oauth2sample.server;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.api.client.auth.oauth2.TokenResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by d.donskoy on 04.05.2016.
 */
public class GetAccessTokenLoader extends AsyncTaskLoader<GetAccessTokenLoader.Result> {

    public static final String BUNDLE_OAUTH_PARAMS = "bundle_oauth_params";
    public static final int ID = 0x309;
    public static final String CODE = "code=";
    public static final String UTF_8 = "UTF-8";

    private final OAuth2Transport mTransport;
    @NonNull
    private final OAuth2Params mParams;
    private Result mResult;

    public GetAccessTokenLoader(Context context, @NonNull OAuth2Params params) {
        super(context);
        mParams = params;
        mTransport = new OAuth2Transport(params);
    }

    @Override
    public Result loadInBackground() {
        final Result result = new Result();

        try {
            if (mParams.getRedirectUri().contains(CODE)) {
                String authCode = getAuthCodeSafely();
                TokenResponse resp = mTransport.getAccessTokenBlocking(authCode);
                result.setTokenResponse(resp);
            }

        } catch (Exception e) {
            Log.d("GetAccessTokenLoader, ", e.getMessage());
            result.setErrorMsg(e.getMessage());
        }
        return result;
    }


    @Override
    public void deliverResult(Result result) {

        if (isReset()) {
            if (result != null) {
                onComplete(result);
            }
        }

        mResult = result;

        if (isStarted()) {
            super.deliverResult(result);
        }
    }

    protected void onComplete(Result result) {
    }

    @Override
    protected void onStartLoading() {
        if (mResult != null) {
            deliverResult(mResult);
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Result result) {
        super.onCanceled(result);
        onComplete(result);
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();

        if (mResult != null) {
            onComplete(mResult);
            mResult = null;
        }
    }


    @Nullable
    private String getAuthCodeSafely() throws Exception {
        return getAuthCodeInternal(mParams.getRedirectUri());
    }

    @NonNull
    private static String getAuthCodeInternal(String url) throws UnsupportedEncodingException {
        final String[] separated = url.split(CODE);
        final String code = separated[separated.length - 1];
        return URLDecoder.decode(code, UTF_8);
    }


    public static class Result {
        private TokenResponse tokenResponse;
        private String mErrorMsg;
        private String mProfile;

        public TokenResponse getTokenResponse() {
            return tokenResponse;
        }

        public void setTokenResponse(TokenResponse tokenResponse) {
            this.tokenResponse = tokenResponse;
        }

        public String getErrorMsg() {
            return mErrorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.mErrorMsg = errorMsg;
        }
    }
}
