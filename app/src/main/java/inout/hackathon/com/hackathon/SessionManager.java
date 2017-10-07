package inout.hackathon.com.hackathon;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by suraj on 07-10-2017.
 */

public class SessionManager {
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHiveLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_EMAIL = "email";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn, String email) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString(KEY_EMAIL,email);
        Constants.EMAIL = email;
        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        if (pref.getBoolean(KEY_IS_LOGGEDIN, false)) {
            pref.getString(KEY_EMAIL,Constants.EMAIL);
            return true;
        }
        else
            return false;
    }
}
