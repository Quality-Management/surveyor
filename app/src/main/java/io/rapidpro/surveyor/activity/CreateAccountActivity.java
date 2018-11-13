package io.rapidpro.surveyor.activity;

import android.app.Activity;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import io.rapidpro.surveyor.R;
import io.rapidpro.surveyor.SurveyorApplication;
import io.rapidpro.surveyor.data.Org;
import io.rapidpro.surveyor.task.FetchOrgsTask;

/**
 * Activity for creating a new surveyor account
 */
public class CreateAccountActivity extends BaseActivity {

    public static final String CREATE_ACCOUNT_URL = "/org/surveyor/";

    public boolean requireLogin() {
        return false;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_account);

        // load the account creation on to our webview
        WebView web = (WebView) findViewById(R.id.webview);

        final Activity activity = this;
        web.setWebViewClient(new WebViewClient() {

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, getString(R.string.web_load_failure), Toast.LENGTH_SHORT).show();
                activity.finish();
            }

            public void onPageFinished(WebView view, String url) {

                UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(url);
                String email = sanitizer.getValue("user");
                String token = sanitizer.getValue("token");
                String orgName = sanitizer.getValue("org");

                if (email != null && token != null && orgName != null) {
                    fetchOrgAndLogin(email, token);
                }

                if (url.endsWith(CREATE_ACCOUNT_URL)) {
                    getViewCache().hide(R.id.web_progress);
                }
            }

            public void onLoadResource(WebView view, String url) {
                if (url.endsWith(CREATE_ACCOUNT_URL)) {
                    getViewCache().show(R.id.web_progress);
                }
            }
        });

        String createAccountURL = SurveyorApplication.get().getTembaHost() + CREATE_ACCOUNT_URL;

        SurveyorApplication.LOG.d("Connecting to " + createAccountURL + "...");

        web.loadUrl(createAccountURL);
    }

    protected void fetchOrgAndLogin(final String email, final String token) {

        new FetchOrgsTask(new FetchOrgsTask.FetchOrgsListener() {
            @Override
            public void onComplete(Set<String> orgUUIDs) {
                login(email, orgUUIDs);
            }

            @Override
            public void onFailure() {
                // TODO
            }
        }).execute(token);
    }
}
