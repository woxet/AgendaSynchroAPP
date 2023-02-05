package agenda.synchro.service;

import agenda.synchro.R;
import agenda.synchro.ressources.RDV;
import agenda.synchro.ressources.Ressources;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;


public class MainActivity extends AppCompatActivity {
    private BroadcastReceiver broadcastReceiver;
    private String selectedDate;
    private CalendarView calendarView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar c = Calendar.getInstance();
        selectedDate = Ressources.dateFormat.format(c.getTime());

        // Initialisation des éléments graphiques
        calendarView = findViewById(R.id.calendarView);
        listView = findViewById(R.id.list_view);
        FloatingActionButton fab = findViewById(R.id.fabAddRdv);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Récupération de la date sélectionnée
                selectedDate = Ressources.dateFormat.format(new Date(year - 1900, month, dayOfMonth));

                getDataFromServer(listView, selectedDate);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("CHECK", selectedDate);
                if (selectedDate != null) {
                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                    intent.putExtra("selected_date", selectedDate);
                    startActivity(intent);
                }
            }
        });

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Calendar c = Calendar.getInstance();
                String date = Ressources.dateFormat.format(c.getTime());
                getDataFromServer(listView, date);
            }
        };
    }

    public void getDataFromServer(ListView listView, String date) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Récupération de la date sélectionnée
                //Calendar c = Calendar.getInstance();
                //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                //String date = dateFormat.format(c.getTime());
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(Ressources.getIP() + Ressources.getPath() + "getdate/" + date);
                    Log.i("URL", "URL == " + url);

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    Scanner scanner = new Scanner(in);
                    final String jsonString = scanner.nextLine();
                    Log.i("Exchange-JSON", "Result == " + jsonString);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Traitement de la réponse
                                JSONArray jsonArray = new JSONArray(jsonString);
                                List<RDV> rdvList = new ArrayList<>();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int idRDV = jsonObject.getInt("idRDV");
                                    String name = jsonObject.getString("name");
                                    String time = jsonObject.getString("time");
                                    RDV rdv = new RDV(idRDV, name, time);
                                    rdvList.add(rdv);
                                }

                                ArrayAdapter<RDV> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, rdvList);
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        RDV rdv = (RDV) parent.getItemAtPosition(position);
                                        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                                        intent.putExtra("idRDV", rdv.getIdRDV());
                                        startActivity(intent);
                                    }
                                });

                            } catch (JSONException ex) {
                                throw new RuntimeException(ex);
                            }
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

    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.ACTION_REFRESH_VIEW");
        registerReceiver(broadcastReceiver, filter);
    }
}

