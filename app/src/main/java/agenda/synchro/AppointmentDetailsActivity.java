package agenda.synchro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.owlike.genson.Genson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

public class AppointmentDetailsActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView locationTextView;
    private Button close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_rdv);

        // Récupération des données de l'Intent
        String name = getIntent().getStringExtra("name");

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
        Button close = findViewById(R.id.close_button);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentDetailsActivity.this, MainActivity.class);
                startActivity(intent);
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
                    URL url = new URL("http://192.168.1.10:8080/ASI_war/rest/rdv/get/0");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

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
                    if( urlConnection != null) urlConnection.disconnect();
                }
            }
        }).start();
    }

}
