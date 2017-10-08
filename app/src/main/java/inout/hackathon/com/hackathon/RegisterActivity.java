package inout.hackathon.com.hackathon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputContact;
    private ProgressDialog pDialog;
    private  String reply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputContact = (EditText) findViewById(R.id.contact);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        Constants.session = new SessionManager(getApplicationContext());


        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String contact = inputContact.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !contact.isEmpty()) {
                    new RegisterVerify().execute();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showError(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    class RegisterVerify extends AsyncTask<Void, Void, Void> {
        String name,email,password,contact;

        @Override
        protected void onPreExecute() {
            pDialog.setMessage("Registering ...");
            showDialog();
            name = inputFullName.getText().toString().trim();
            email = inputEmail.getText().toString().trim();
            password = inputPassword.getText().toString().trim();
            contact = inputContact.getText().toString().trim();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection client = null;
            try {
                URL url = new URL (getResources().getString(R.string.base_url)+"/users/register");
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setRequestProperty("name", name);
                client.setRequestProperty("emailId", email);
                client.setRequestProperty("password", password);
                client.setRequestProperty("contact", contact);
                client.setDoOutput(true);

                OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
                outputPost.flush();
                outputPost.close();
                //client.setChunkedStreamingMode(0);


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
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
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


