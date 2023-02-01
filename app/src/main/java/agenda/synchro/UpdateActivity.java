package agenda.synchro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import ressources.DateSerializer;
import ressources.Ressources;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Scanner;

public class UpdateActivity extends AppCompatActivity {
    private TextInputEditText nameTextInput;
    private TextInputEditText dateTextInput;
    private TextInputEditText timeTextInput;
    private TextInputEditText locationTextInput;
    private int idRDV;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_rdv);

        idRDV = getIntent().getIntExtra("idRDV", -1);
        Log.i("IDRDV","==" +idRDV);
        // Récupération des références aux vues
        nameTextInput = findViewById(R.id.addRDV_name);
        dateTextInput = findViewById(R.id.addRDV_date);
        timeTextInput = findViewById(R.id.addRDV_time);
        locationTextInput = findViewById(R.id.addRDV_location);

        Button send = findViewById(R.id.send_button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    public void run() {
                        RDV rdv = null;

                        rdv = new RDV(
                                Objects.requireNonNull(idRDV),
                                Objects.requireNonNull(nameTextInput.getText()).toString(),
                                new SimpleDateFormat("yyyy-MM-dd").format(Objects.requireNonNull(dateTextInput.getText()).toString()),
                                Objects.requireNonNull(timeTextInput.getText()).toString(),
                                Objects.requireNonNull(locationTextInput.getText()).toString()
                        );
                        String json = new Genson().serialize(rdv);
                        Log.i("Exchange-JSON", "Update == " + json);

                        HttpURLConnection urlConnection = null;

                        try {
                            URL url = new URL(Ressources.getIP() + Ressources.getPath() + "update");

                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestMethod("PUT");
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

                finish();
            }
        });
    }

    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(Ressources.getIP() + Ressources.getPath() + "getid/" + idRDV);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    Scanner scanner = new Scanner(in);

                    Genson genson = new GensonBuilder().withConverter(new DateSerializer(), java.util.Date.class).create();
                    final RDV rdv = genson.deserialize(scanner.nextLine(), RDV.class);
                    Log.i("Exchange-JSON", "Result == " + rdv);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nameTextInput.setText(rdv.getName());
                            dateTextInput.setText(rdv.getDate().toString());
                            timeTextInput.setText(rdv.getTime());
                            locationTextInput.setText(rdv.getLocation());
                        }
                    });
                    in.close();
                } catch (IOException e) {
                    Log.e("Exchange-JSON", "Cannot found HTTP server", e);
                } finally {
                    if( urlConnection != null) urlConnection.disconnect();
                }
            }
        }).start();
    }

    @Override
    public void finish() {
        // Déclencher l'action ou l'événement qui actualisera la vue dans l'activité principale
        Intent intent = new Intent().setAction("com.example.ACTION_REFRESH_VIEW");
        sendBroadcast(intent);

        super.finish();
    }
}
