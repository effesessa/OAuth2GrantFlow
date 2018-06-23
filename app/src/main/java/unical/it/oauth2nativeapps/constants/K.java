package unical.it.oauth2nativeapps.constants;

public class K {

    public static final String CLIENT_ID = "YOUR_CLIENT_ID";

    public static final String REDIRECT_URI = "YOUR_PACKAGE:/oauth2redirect";

    public static final String REDIRECT_URI_ROOT = "YOUR_PACKAGE";

    public static final String API_SCOPE = "https://www.googleapis.com/auth/drive";

    //if you use google accounts https://accounts.google.com/o/oauth2/v2/auth
    public static final String URL_AUTH = "PUT_URL";

    //Don't touch CODE, SHA256, ERROR_CODE, GRANT_TYPE_AUTHORIZATION_CODE, GRANT_TYPE_REFRESH_TOKEN
    public static final String CODE = "code";

    public static final String SHA256 = "S256";

    public static final String ERROR_CODE = "error";

    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";

    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
}
