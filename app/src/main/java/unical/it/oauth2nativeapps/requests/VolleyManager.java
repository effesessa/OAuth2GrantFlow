package unical.it.oauth2nativeapps.requests;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.pedant.SweetAlert.SweetAlertDialog;
import unical.it.oauth2nativeapps.constants.K;
import unical.it.oauth2nativeapps.R;
import unical.it.oauth2nativeapps.model.DriveFile;
import unical.it.oauth2nativeapps.model.OAuthToken;
import unical.it.oauth2nativeapps.utils.PreferenceUtils;


public class VolleyManager {

    private static VolleyManager volleyManager;

    private RequestQueue queue;

    private Context context;

    private VolleyManager() {

    }

    public void setContext(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
    }

    public static VolleyManager getInstance() {
        if(volleyManager == null)
            volleyManager = new VolleyManager();
        return volleyManager;
    }

    public void requestAccessToken(String authorizationCode, final IVolleyManager iVolleyManager) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.googleapis.com")
                .appendPath("oauth2")
                .appendPath("v4")
                .appendPath("token")
                .appendQueryParameter("client_id", K.CLIENT_ID)
                .appendQueryParameter("redirect_uri", K.REDIRECT_URI)
                .appendQueryParameter("grant_type", K.GRANT_TYPE_AUTHORIZATION_CODE)
                .appendQueryParameter("code", authorizationCode)
                .appendQueryParameter("code_verifier", PreferenceUtils.getCodeVerifier(context));
        String urlStr = builder.build().toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                urlStr,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null) {
                            Gson gson = new Gson();
                            PreferenceUtils.storeOAuthToken(context, response.toString());
                            OAuthToken oAuthToken = gson.fromJson(response.toString(), OAuthToken.class);
                            System.out.println(oAuthToken.toString());
                            progressDialog.dismiss();
                            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText(oAuthToken.getAccess_token())
                                    .setTitleText("Access Token 2.0:")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            iVolleyManager.completed();
                                        }
                                    })
                                    .show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Request Token error!", Toast.LENGTH_LONG).show();
                    }
                }
        );
        queue.add(jsonObjectRequest);
    }

    public void requestGoogleDriveFiles(final IVolleyManager iVolleyManager) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.googleapis.com")
                .appendPath("drive")
                .appendPath("v2")
                .appendPath("files")
                .appendQueryParameter("access_token", PreferenceUtils.getOAuthToken(context).getAccess_token());
        String urlStr = builder.build().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                urlStr,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null) {
                            System.out.println(response.toString());
                            downloadInformationFiles(response);
                            progressDialog.dismiss();
                            iVolleyManager.completed();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Request Files error!", Toast.LENGTH_LONG).show();
                    }
                }
        );
        queue.add(jsonObjectRequest);
    }

    public void downloadInformationFiles(JSONObject response) {
        try {
            Gson gson = new Gson();
            JSONArray jsonArray = response.getJSONArray("items");
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectFile = jsonArray.getJSONObject(i);
                if(jsonObjectFile.get("fileExtension").equals("png") ||
                        jsonObjectFile.get("fileExtension").equals("jpg") ) {
                    DriveFile driveFile = gson.fromJson(jsonObjectFile.toString(), DriveFile.class);
                    PreferenceUtils.storeDriveFile(context, gson.toJson(driveFile));
                }
            }
        } catch (JSONException e) {
            Toast.makeText(context, "Impossible to download files!", Toast.LENGTH_LONG).show();
        }
    }

    private ProgressDialog progressDialog;

    public void showProgressDialog(Activity activity) {
        progressDialog = new ProgressDialog(activity, R.style.AppTheme_Dialog);
        progressDialog.setMessage("LOADING...");
        progressDialog.show();
    }
}
