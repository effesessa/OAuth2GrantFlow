package unical.it.oauth2nativeapps.pkce;

import android.util.Base64;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PKCEUtils {

    //min-max length 43-128 by BCP PKCE
    public static final int CODE_VERIFIER_LENGTH = 64;

    private static final int BASE64_ENCODE_SETTINGS = Base64.NO_WRAP | Base64.NO_PADDING | Base64.URL_SAFE;

    public static String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[CODE_VERIFIER_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.encodeToString(randomBytes,Base64.NO_WRAP | Base64.NO_PADDING | Base64.URL_SAFE);
    }

    public static String computeCodeChallenge(String codeVerifier) {
        String codeChallenge = null;
        try {
            MessageDigest sha256Digester = MessageDigest.getInstance("SHA-256");
            sha256Digester.update(codeVerifier.getBytes("ISO_8859_1"));
            byte[] digestBytes = sha256Digester.digest();
            codeChallenge = Base64.encodeToString(digestBytes, BASE64_ENCODE_SETTINGS);
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return codeChallenge;
    }
}
