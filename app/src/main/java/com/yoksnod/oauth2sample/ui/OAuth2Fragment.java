package com.yoksnod.oauth2sample.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewDatabase;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.yoksnod.oauth2sample.R;
import com.yoksnod.oauth2sample.server.GetAccessTokenLoader;
import com.yoksnod.oauth2sample.server.OAuth2Transport;
import com.yoksnod.oauth2sample.server.OAuth2Params;
import com.yoksnod.oauth2sample.server.VkOAuth2Params;

import java.lang.ref.WeakReference;

/**
 * Created by d.donskoy on 04.05.2016.
 */
public class OAuth2Fragment extends Fragment implements LoaderManager.LoaderCallbacks<GetAccessTokenLoader.Result>{

    private WebView mWebView;
    private OAuth2WebViewClient mWebViewClient;
    private OAuth2Params mParams;

    public OAuth2Fragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParams = new OAuth2Params
                .Builder()
                .setClientId(VkOAuth2Params.CLIENT_ID)
                .setClientSecret(VkOAuth2Params.CLIENT_SECRET)
                .setAuthUri(VkOAuth2Params.AUTH_SERVER_URL)
                .setTokenUri(VkOAuth2Params.TOKEN_SERVER_URL)
                .setRedirectUri(VkOAuth2Params.REDIRECT_URI)
                .setScope(VkOAuth2Params.SCOPE)
                .build();
        mWebViewClient = new OAuth2WebViewClient(this, mParams);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_oauth, container);
        FrameLayout webViewContainer = (FrameLayout) root.findViewById(R.id.webview_container);
        mWebView = onPrepareWebView(webViewContainer);
        mWebView.setWebViewClient(mWebViewClient);
        final String authUri = new OAuth2Transport(mParams).getAuthUri();
        mWebView.loadUrl(authUri);
        return root;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mWebViewClient.onDetach();
    }


    @Override
    public Loader<GetAccessTokenLoader.Result> onCreateLoader(int id, Bundle args) {
        OAuth2Params params = args.getParcelable(GetAccessTokenLoader.BUNDLE_OAUTH_PARAMS);
        if (TextUtils.isEmpty(params.getRedirectUri())){
            throw new IllegalArgumentException("can't started loader with empty url");
        }
        return new GetAccessTokenLoader(getContext().getApplicationContext(), params);
    }

    @Override
    public void onLoadFinished(Loader<GetAccessTokenLoader.Result> loader, GetAccessTokenLoader.Result result) {
        TokenResponse resp = result.getTokenResponse();
        if (resp == null || !TextUtils.isEmpty(result.getErrorMsg())) {
            return;
        }
        Toast.makeText(getContext().getApplicationContext(), "access token = " + resp.getAccessToken() +
                        " expired" + resp.getExpiresInSeconds() + " id = " + resp.getTokenType(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private static class OAuth2WebViewClient extends WebViewClient {

        private final WeakReference<OAuth2Fragment> mFragmentRef;
        private final OAuth2Params mOAuthParams;

        OAuth2WebViewClient(OAuth2Fragment fragment, OAuth2Params redirectUri) {
            mFragmentRef = new WeakReference<>(fragment);
            mOAuthParams = redirectUri;
        }

        void onDetach(){
            mFragmentRef.clear();
        }

        @Override
        public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
            final OAuth2Fragment fragment = mFragmentRef.get();
            if (fragment == null){
                return;
            }

            if (url.startsWith(mOAuthParams.getRedirectUri())){
                webView.setVisibility(View.INVISIBLE);

                final Bundle args = new Bundle();

                OAuth2Params copy = new OAuth2Params
                        .Builder(mOAuthParams)
                        .setRedirectUri(url)
                        .build();
                args.putParcelable(GetAccessTokenLoader.BUNDLE_OAUTH_PARAMS, copy);
                final LoaderManager loaderManager = fragment
                        .getActivity()
                        .getSupportLoaderManager();
                final Loader<Object> loader = loaderManager.getLoader(GetAccessTokenLoader.ID);
                if (loader != null){
                    return;
                }
                loaderManager.initLoader(GetAccessTokenLoader.ID, args, fragment);

            } else {
                webView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(final WebView view, final String url) {

        }
    }


    private WebView onPrepareWebView(FrameLayout webViewContainer){
        WebView webview = new WebView(getContext().getApplicationContext());
        webview.getSettings().setJavaScriptEnabled(false);
        webview.setVisibility(View.VISIBLE);
        webview.getSettings().setSavePassword(false);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT, 0);
        webViewContainer.addView(webview, layoutParams);
        WebViewDatabase.getInstance(getContext().getApplicationContext()).clearUsernamePassword();
        return webview;
    }
}
