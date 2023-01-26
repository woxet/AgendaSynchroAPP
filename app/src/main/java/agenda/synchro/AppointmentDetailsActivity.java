package agenda.synchro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AppointmentDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_rdv);

        // Récupération des données de l'Intent
        String name = getIntent().getStringExtra("name");

        // Récupération des références aux vues
        TextView nameTextView = findViewById(R.id.name_text_view);
        TextView dateTextView = findViewById(R.id.date_text_view);
        TextView timeTextView = findViewById(R.id.time_text_view);
        TextView locationTextView = findViewById(R.id.location_text_view);

        // Affichage des données de rendez-vous
        nameTextView.setText(name);
        dateTextView.setText("Monday, January 25th");
        timeTextView.setText("2:00:00");
        locationTextView.setText("UPJV");

        Button close = findViewById(R.id.close_button);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentDetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


}
