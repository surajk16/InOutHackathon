package inout.hackathon.com.hackathon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by suraj on 07-10-2017.
 */

public class LoginActivity extends Activity {
    //private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private String reply;
    //private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        //db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * function to verify login details in mysql db
     */
    private void checkLogin(final String email, final String password) {
                pDialog.setMessage("Logging in ...");
        showDialog();

        if (!email.isEmpty() && !password.isEmpty()) {
            new LoginVerify().execute();
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "Please enter your details!", Toast.LENGTH_LONG)
                    .show();
        }
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void showError(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    class LoginVerify extends AsyncTask<Void, Void, Void> {
        String email,password;

        @Override
        protected void onPreExecute() {
            pDialog.setMessage("Logging in ...");
            showDialog();
            email = inputEmail.getText().toString().trim();
            password = inputPassword.getText().toString().trim();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection client = null;
            try {
                URL url = new URL (getResources().getString(R.string.base_url)+"/users/login");
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setRequestProperty("emailid", email);
                client.setRequestProperty("password", password);
                client.setDoOutput(true);

                OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
                outputPost.flush();
                outputPost.close();

                InputStream in = client.getInputStream();
                StringBuilder sb = new StringBuilder();
                try {
                    int chr;
                    while ((chr = in.read()) != -1) {
                        sb.append((char) chr);
                    }
                    reply = sb.toString();
                    Log.d("Response", reply);

                } catch (MalformedURLException error) {
                    showError("Handles an incorrectly entered UR");
                } catch (SocketTimeoutException error) {
                    showError("Handles URL access timeout");
                } catch (IOException error) {
                    showError("Handles input and output errors");
                } finally {
                    if (client != null)
                        client.disconnect();
                }


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideDialog();
            try {
                JSONObject j = new JSONObject(reply);
                if ((boolean)j.get("success")) {
                    session.setLogin(true,email);
                    Intent intent = new Intent(LoginActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    showError((String) j.get("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
