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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Scanner;

public class UpdateActivity extends AppCompatActivity {
    private TextInputEditText nameTextInput;
    private TextInputEditText dateTextInput;
    private TextInputEditText timeTextInput;
    private TextInputEditText locationTextInput;
    private Button send;
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

        send = findViewById(R.id.send_button);
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

                    //Genson genson = new GensonBuilder().withConverter(new DateSerializer(), java.util.Date.class).create();
                    final RDV rdv = new Genson().deserialize(scanner.nextLine(), RDV.class);
                    Log.i("Exchange-JSON", "Result == " + rdv);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nameTextInput.setText(rdv.getName());
                            dateTextInput.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Calendar cal = Calendar.getInstance();
                                    int year = cal.get(Calendar.YEAR);
                                    int month = cal.get(Calendar.MONTH);
                                    int day = cal.get(Calendar.DAY_OF_MONTH);

                                    DatePickerDialog dialog = new DatePickerDialog(
                                            UpdateActivity.this,
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
                            dateTextInput.setText(rdv.getDate());
                            timeTextInput.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Calendar calendar = Calendar.getInstance();
                                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                    int minute = calendar.get(Calendar.MINUTE);
                                    TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                            String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                                            timeTextInput.setText(time);
                                        }
                                    }, hour, minute, true);
                                    timePickerDialog.show();
                                }
                            });
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
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            public void run() {
                                RDV rdv = new RDV(
                                        Objects.requireNonNull(idRDV),
                                        Objects.requireNonNull(nameTextInput.getText()).toString(),
                                        Objects.requireNonNull(dateTextInput.getText()).toString(),
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
                                    urlConnection.setRequestProperty("Content-Type", "text/plain");
                                    //urlConnection.setRequestProperty("Accept", "text/plain");

                                    OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
                                    os.write(json.getBytes());
                                    Log.i("Exchange-JSON", "JSON == " + json);
                                    //os.flush();
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
