package unical.it.oauth2nativeapps.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.HttpUrl;
import unical.it.oauth2nativeapps.constants.K;
import unical.it.oauth2nativeapps.R;
import unical.it.oauth2nativeapps.pkce.PKCEUtils;
import unical.it.oauth2nativeapps.requests.IVolleyManager;
import unical.it.oauth2nativeapps.requests.VolleyManager;
import unical.it.oauth2nativeapps.utils.NetworkUtils;
import unical.it.oauth2nativeapps.utils.PreferenceUtils;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private Button openBrowserButton, requestTokenButton, requestFilesButton;

    private String authorizationCode;

    private String codeChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        openBrowserButton = (Button) findViewById(R.id.openBrowserButton);
        requestTokenButton = (Button) findViewById(R.id.requestTokenButton);
        requestFilesButton = (Button) findViewById(R.id.requestFilesButton);
        openBrowserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkAvailable(LoginActivity.this)) {
                    PreferenceUtils.storeWithBoolean(LoginActivity.this, true, "resumeAuthorizationCode");
                    makeAuthorizationRequest();
                }
                else
                    Toast.makeText(LoginActivity.this, "Network not avaiable!", Toast.LENGTH_LONG).show();
            }
        });
        requestTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(authorizationCode)) {
                    VolleyManager volleyManager = VolleyManager.getInstance();
                    volleyManager.setContext(LoginActivity.this);
                    volleyManager.showProgressDialog(LoginActivity.this);
                    volleyManager.requestAccessToken(authorizationCode, new IVolleyManager() {
                        @Override
                        public void completed() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    requestTokenButton.setVisibility(View.GONE);
                                    requestFilesButton.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                }
                else
                    Toast.makeText(LoginActivity.this,"authorization code is empty!", Toast.LENGTH_LONG).show();
            }
        });
        requestFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolleyManager volleyManager = VolleyManager.getInstance();
                volleyManager.setContext(LoginActivity.this);
                volleyManager.showProgressDialog(LoginActivity.this);
                volleyManager.requestGoogleDriveFiles(new IVolleyManager() {
                    @Override
                    public void completed() {
                        Intent intent = new Intent(LoginActivity.this, DriveFileActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }
                });
            }
        });
    }

    private void makeAuthorizationRequest() {
        String codeVerifier = PKCEUtils.generateCodeVerifier();
        PreferenceUtils.storeWithString(this, PreferenceUtils.CODE_VERIFIER, codeVerifier);
        codeChallenge = PKCEUtils.computeCodeChallenge(codeVerifier);
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setContentText(codeChallenge)
                .setTitleText("SHA-256(code_verifier): ")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        HttpUrl authorizeUrl = HttpUrl.parse(K.URL_AUTH)
                                .newBuilder()
                                .addQueryParameter("client_id", K.CLIENT_ID)
                                .addQueryParameter("scope", K.API_SCOPE)
                                .addQueryParameter("redirect_uri", K.REDIRECT_URI)
                                .addQueryParameter("response_type", K.CODE)
                                .addQueryParameter("code_challenge_method", K.SHA256)
                                .addQueryParameter("code_challenge", codeChallenge)
                                .build();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(String.valueOf(authorizeUrl.url())));
                        startActivity(i);
                        sweetAlertDialog.dismissWithAnimation();
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PreferenceUtils.isBooleanValue(LoginActivity.this, "resumeAuthorizationCode")) {
            Uri data = getIntent().getData();
            if (data != null && !TextUtils.isEmpty(data.getScheme())) {
                if (K.REDIRECT_URI_ROOT.equals(data.getScheme())) {
                    authorizationCode = data.getQueryParameter(K.CODE);
                    new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText(authorizationCode)
                            .setTitleText("Authorization Code:")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            openBrowserButton.setVisibility(View.GONE);
                                            requestTokenButton.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            }).show();
                    String error = data.getQueryParameter(K.ERROR_CODE);
                    if (!TextUtils.isEmpty(error))
                        Toast.makeText(this, "Error authorizationCode.", Toast.LENGTH_LONG).show();
                }
            }
            PreferenceUtils.storeWithBoolean(LoginActivity.this, false, "resumeAuthorizationCode");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}
