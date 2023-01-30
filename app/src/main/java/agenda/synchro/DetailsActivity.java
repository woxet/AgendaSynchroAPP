package agenda.synchro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.owlike.genson.Genson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class DetailsActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView locationTextView;
    private int idRDV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_rdv);

        // Récupération des données de l'Intent
        idRDV = getIntent().getIntExtra("idRDV", -1);

        // Récupération des références aux vues
        nameTextView = findViewById(R.id.name_text_view);
        dateTextView = findViewById(R.id.date_text_view);
        timeTextView = findViewById(R.id.time_text_view);
        locationTextView = findViewById(R.id.location_text_view);
    /*
        // Affichage des données de rendez-vous
        nameTextView.setText(name);
        dateTextView.setText("Monday, January 25th");
        timeTextView.setText("2:00:00");
        locationTextView.setText("UPJV");
*/
        Button update = findViewById(R.id.update_button);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, UpdateActivity.class);
                intent.putExtra("idRDV", idRDV);
                startActivity(intent);
            }
        });

        Button delete = findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection urlConnection = null;
                        try {
                            URL url = new URL("http:///10.1.24.72:8080/ASI_war/rest/rdv/delete/" + idRDV);
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestMethod("GET");
                            Log.i("HTTP", "URL == " + url);

                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            Scanner scanner = new Scanner(in);
                            Log.i("Exchange JSON", "Result == " + scanner.nextLine());
                            in.close();
                        } catch (IOException e) {
                            Log.i("Exchange-JSON", "Cannot found http server : ", e);
                        } finally {
                            if (urlConnection != null) urlConnection.disconnect();
                        }
                        Log.i("Exchange-JSON", "Delete == " + idRDV);
                    }
                }).start();

                finish();
            }
        });

        Button close = findViewById(R.id.close_button);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (idRDV == -1) {
            Log.e("Exchange-JSON", "idRDV not provided");
        }
    }

    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(Ressources.ip + Ressources.path + "getid/" + idRDV);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    Log.i("HTTP", "URL == " + url);

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    Scanner scanner = new Scanner(in);
                    final RDV rdv = new Genson().deserialize(scanner.nextLine(), RDV.class);
                    Log.i("Exchange-JSON", "Result == " + rdv);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nameTextView.setText(rdv.getName());
                            dateTextView.setText(rdv.getDate());
                            timeTextView.setText(rdv.getTime());
                            locationTextView.setText(rdv.getLocation());
                        }
                    });
                    in.close();
                } catch (IOException e) {
                    Log.e("Exchange-JSON", "Cannot found HTTP server", e);
                } finally {
                    if (urlConnection != null) urlConnection.disconnect();
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
