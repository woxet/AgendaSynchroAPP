package agenda.synchro.service;

import agenda.synchro.R;
import agenda.synchro.ressources.RDV;
import agenda.synchro.ressources.Ressources;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.owlike.genson.Genson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        String selectedDate = getIntent().getStringExtra("selected_date");
        dateTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                                dateTextInput.setText(date);
                            }
                        },
                        year,
                        month,
                        day
                );
                dialog.show();
            }
        });
        dateTextInput.setText(selectedDate);
        timeTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                        timeTextInput.setText(time);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });
        timeTextInput.setText(timeFormat.format(c.getTime()));

        Button send = findViewById(R.id.send_button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    public void run() {
                        String dateString = dateTextInput.getText().toString();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = new Date();
                        try {
                            date = format.parse(dateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Log.i("CHECK",dateString);
                        RDV rdv = null;
                        rdv = new RDV(
                                Objects.requireNonNull(nameTextInput.getText()).toString(),
                                Objects.requireNonNull(date),
                                Objects.requireNonNull(timeTextInput.getText()).toString(),
                                Objects.requireNonNull(locationTextInput.getText()).toString()
                        );

                        //Genson genson = new GensonBuilder().withConverter(new DateSerializer(), java.util.Date.class).create();
                        String json = new Genson().serialize(rdv);
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
