package inout.hackathon.com.hackathon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
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

public class Detect extends Activity {
    Button yes, no;
    TextView cntdown;
    private String reply;
    private Boolean done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detect);

        yes = findViewById(R.id.btn_yes);
        no = findViewById(R.id.btn_no);
        cntdown = findViewById(R.id.text_cntdown);

        done = false;

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                cntdown.setText("" + (millisUntilFinished / 1000));
            }

            public void onFinish() {
                if (!done) {
                    done = true;
                    yes.setVisibility(View.INVISIBLE);
                    no.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Time expired. Sending details.", Toast.LENGTH_LONG).show();
                    new SendDetails().execute();
                    Constants.MP.stop();
                    cntdown.setText("0");
                }
            }
        }.start();

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!done) {
                    //Toast.makeText(getApplicationContext(), "Yes", Toast.LENGTH_LONG).show();
                    Constants.MP.stop();
                    startService(new Intent(getApplicationContext(), DetectService.class));
                    startService(new Intent(getApplicationContext(), GPS_Service.class));
                    Constants.STATUS = "START";
                    done = true;
                    finish();
                }
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!done) {
                    //Toast.makeText(getApplicationContext(), "No", Toast.LENGTH_LONG).show();
                    Constants.MP.stop();
                    done = true;
                    new SendDetails().execute();
                    finish();
                }
            }
        });
    }

    private void showError(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    class SendDetails extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection client = null;
            try {
                URL url = new URL(getResources().getString(R.string.base_url) + "/accident/create");
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setRequestProperty("emailid", Constants.EMAIL.trim());
                Log.d("email", "" + Constants.EMAIL);
                client.setRequestProperty("severity", Constants.SEVERITY.trim());
                client.setRequestProperty("location", Constants.LOC.getLatitude() + "," + Constants.LOC.getLongitude());
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

            try {
                JSONObject j = new JSONObject(reply);
                if ((boolean) j.get("success")) {
                    Toast.makeText(getApplicationContext(), "Help will arrive soon!", Toast.LENGTH_LONG).show();
                } else {
                    showError((String) j.get("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
