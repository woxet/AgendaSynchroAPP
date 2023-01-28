package agenda.synchro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.owlike.genson.Genson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class addRdvActivity extends AppCompatActivity {
    private TextInputEditText nameTextInput;
    private TextInputEditText dateTextInput;
    private TextInputEditText timeTextInput;
    private TextInputEditText locationTextInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange_json);

        // Récupération des références aux vues
        nameTextInput = findViewById(R.id.addRDV_name);
        dateTextInput = findViewById(R.id.addRDV_date);
        timeTextInput = findViewById(R.id.addRDV_time);
        locationTextInput = findViewById(R.id.addRDV_location);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        dateTextInput.setText(dateFormat.format(c.getTime()));
        timeTextInput.setText(timeFormat.format(c.getTime()));

        Button send = findViewById(R.id.send_button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    public void run() {
                        RDV rdv = new RDV(
                                Objects.requireNonNull(nameTextInput.getText()).toString(),
                                Objects.requireNonNull(dateTextInput.getText()).toString(),
                                Objects.requireNonNull(timeTextInput.getText()).toString(),
                                Objects.requireNonNull(locationTextInput.getText()).toString()
                        );
                        String json = new Genson().serialize(rdv);
                        Log.i("Exchange-JSON", "Send == " + json);

                        HttpURLConnection urlConnection = null;

                        try {
                            URL url = new URL("http://192.168.1.10:8080/ASI_war/rest/rdv/add");

                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestMethod("POST");
                            urlConnection.setDoOutput(true);
                            urlConnection.setRequestProperty("Content-Type", "application/json");
                            urlConnection.setRequestProperty("Accept", "application/json");

                            OutputStream os = urlConnection.getOutputStream();
                            os.write(json.getBytes());
                            os.flush();
                            os.close();
                            Log.i("Exchange-JSON", "URL == " + url);

                            // Traitement de la réponse
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            Scanner scanner = new Scanner(in);
                            Log.i("Exchange JSON", "Result == " + scanner.nextLine());
                            in.close();
                        } catch (IOException e) {
                            Log.i("Exchange-JSON", "Cannot found http server : ", e);
                        } finally {
                            if (urlConnection != null) urlConnection.disconnect();
                        }
                    }
                }).start();

                Intent intent = new Intent(addRdvActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
