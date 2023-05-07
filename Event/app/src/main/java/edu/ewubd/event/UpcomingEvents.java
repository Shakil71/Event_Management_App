package edu.ewubd.event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpcomingEvents extends AppCompatActivity {

    private ArrayList<Event> events;
    private CustomEventAdapter adapter;
    private ListView lvEvents;
    private Button btCreateNew, btHistory, btExit;

    private void showDialog(String message, String title, String btn01, String btn02, String key, String name) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(UpcomingEvents.this);

        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setCancelable(false).setPositiveButton(btn01, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (btn01.equals("Yes")) {
                    try {
                        KeyValueDB kvDB = new KeyValueDB(UpcomingEvents.this);
                        int isDeleted = kvDB.deleteDataByKey(key);
                        System.out.println(isDeleted);
                        if (isDeleted != 0) {
                            loadData();
                            adapter.notifyDataSetChanged();

                            Toast.makeText(getApplicationContext(), name + " successfully deleted!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error to delete " + name, Toast.LENGTH_LONG).show();
                        }
                        kvDB.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                dialog.cancel();
            }
        }).setNegativeButton(btn02, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void loadData() {
        //events = new ArrayList<>();
        events.clear();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String dateAndTime = "";
        try {
            KeyValueDB kvDB = new KeyValueDB(this);
            Cursor cursor = kvDB.getAllKeyValues();
            int count = 0;
            if (cursor != null) {
                int startPos = cursor.getPosition();
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    String[] cols = cursor.getColumnNames();
                    String key = "";
                    int length = cols.length;

                    for (int i = 0; i < length; i++) {
                        try {
                            if (i == 0) {
                                key = cursor.getString(i);
                            } else {
                                String values = cursor.getString(i);
                                ArrayList<String> list = new ArrayList<>(Arrays.asList(values.split(", ")));

                                try {
                                    Date date = dateFormat.parse(list.get(3));
                                    dateAndTime = newDateFormat.format(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                events.add(new Event(key, list.get(0), list.get(1), list.get(2), dateAndTime, list.get(4), list.get(5), list.get(6), list.get(7), list.get(8)));
                            }
                        } catch (SQLiteException e) {
                            e.printStackTrace();
                        }
                    }

                    count++;
                }
                cursor.moveToPosition(startPos);
                if (count == 0) {
                    System.out.println("No data in the cursor\n");
                }
            } else {
                System.out.println("Cursor is null\n");
            }
            kvDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEventListByServerData(String data){
        try{
            JSONObject jo = new JSONObject(data);
            if(jo.has("events")){
                try {
                    events.clear();
                    KeyValueDB kvDB = new KeyValueDB(this);

                    JSONArray ja = jo.getJSONArray("events");
                    for(int i=0; i<ja.length(); i++){
                        JSONObject event = ja.getJSONObject(i);
                        String eventKey = event.getString("e_key");
                        String eventValue = event.getString("e_value");
                        // split eventValue to show in event list

                        ArrayList<String> list = new ArrayList<>(Arrays.asList(eventValue.split(", ")));
                        events.add(new Event(eventKey, list.get(0), list.get(1), list.get(2), list.get(3), list.get(4), list.get(5), list.get(6), list.get(7), list.get(8)));
                    }
                    kvDB.close();
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception e){}
    }

    private void httpRequest(final String keys[],final String values[]){
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params=new ArrayList<NameValuePair>();
                for (int i=0; i<keys.length; i++){
                    params.add(new BasicNameValuePair(keys[i],values[i]));
                }
                String url= "https://www.muthosoft.com/univ/cse489/index.php";
                String data="";
                try {
                    data=JSONParser.getInstance().makeHttpRequest(url,"POST",params);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String data){
                if(data!=null){
                    System.out.println(data);
                    updateEventListByServerData(data);
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events);

        lvEvents = findViewById(R.id.lvEvents);

        btCreateNew = findViewById(R.id.btCreateNew);
        btHistory = findViewById(R.id.btHistory);
        btExit = findViewById(R.id.btExit);

        btCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), CreateEventActivity.class);
                startActivity(i);
            }
        });

        btHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "History!", Toast.LENGTH_LONG).show();
            }
        });

        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        events = new ArrayList<>();
        loadData();

        String keys[] = {"action", "id", "semester"};
        String value[] = {"restore", "id", "semester"};
        httpRequest(keys, value);

        adapter = new CustomEventAdapter(this, events);
        lvEvents.setAdapter(adapter);

        // handle the click on an event-list item
        lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                // String item = (String) parent.getItemAtPosition(position);
                System.out.println(position);
                Intent i = new Intent(UpcomingEvents.this, CreateEventActivity.class);
                i.putExtra("key", events.get(position).getKey());
                startActivity(i);
            }
        });

        // handle the long-click on an event-list item
        lvEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String message = "Do you want to delete event - " + events.get(position).getName() + "?";
                showDialog(message, "Delete Event", "Yes", "No", events.get(position).getKey(), events.get(position).getName());
                return true;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        loadData();

        adapter.notifyDataSetChanged();
    }
}