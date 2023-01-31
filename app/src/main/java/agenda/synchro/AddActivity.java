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
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class AddActivity extends AppCompatActivity {
    private TextInputEditText nameTextInput;
    private TextInputEditText dateTextInput;
    private TextInputEditText timeTextInput;
    private TextInputEditText locationTextInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_rdv);

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
                        // Expression régulière pour le format de date yyyy-MM-dd
                        String pattern = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$";
                        String dateString = dateTextInput.getText().toString();
                        // Vérifier si la saisie respecte le format
                        if (!dateString.matches(pattern)) {
                            // Afficher un message d'erreur si le format n'est pas respecté
                            //Toast.makeText(this, "La date doit être au format yyyy-MM-dd", Toast.LENGTH_SHORT).show();
                        } else {
                            // Traiter la date correctement formatée ici
                        }

                        RDV rdv = null;
                        try {
                            rdv = new RDV(
                                    Objects.requireNonNull(nameTextInput.getText()).toString(),
                                    new SimpleDateFormat("yyyy-MM-dd").parse(Objects.requireNonNull(dateTextInput.getText()).toString()),
                                    Objects.requireNonNull(timeTextInput.getText()).toString(),
                                    Objects.requireNonNull(locationTextInput.getText()).toString()
                            );
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                        Genson genson = new GensonBuilder().withConverter(new DateSerializer(), java.util.Date.class).create();
                        String json = genson.serialize(rdv);
                        Log.i("Exchange-JSON", "Send == " + json);

                        HttpURLConnection urlConnection = null;

                        try {
                            URL url = new URL(Ressources.getIP() + Ressources.getPath() + "add/");

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

                finish();
            }
        });
    }

    @Override
    public void finish() {
        // Déclencher l'action ou l'événement qui actualisera la vue dans l'activité principale
        Intent intent = new Intent().setAction("com.example.ACTION_REFRESH_VIEW");
        sendBroadcast(intent);

        super.finish();
    }
}
