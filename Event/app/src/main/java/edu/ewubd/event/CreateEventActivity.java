package edu.ewubd.event;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etName, etPlace, etDateTime, etCapacity, etBudget, etEmail, etPhone, etDescription;
    private TextView tvError;
    private RadioButton rbIndoor, rbOutdoor, rbOnline;
    private Button btCancel, btShare, btSave;
    private ArrayList<Event> events = new ArrayList<>();
    private String errorMessage = "";

    private String generateUniqueID() {
        return UUID.randomUUID().toString();
    }

    private void clearData() {
        etName.setText(null);
        etPlace.setText(null);

        rbIndoor.setChecked(false);
        rbOutdoor.setChecked(false);
        rbOnline.setChecked(false);

        etDateTime.setText(null);
        etCapacity.setText(null);
        etBudget.setText(null);
        etEmail.setText(null);
        etPhone.setText(null);
        etDescription.setText(null);

        tvError.setText("Error message will be here");
        errorMessage = "";
    }

    private String checkString(String string) {
        if (string == null || string.isEmpty()) {
            string = "";
        }
        return string;
    }

    private Event getFormData() {
        String key = generateUniqueID();
        String name, place, type, dateAndTime, capacity, budget, email, phone, description;

        name = checkString(etName.getText().toString());
        place = checkString(etPlace.getText().toString());

        if (rbIndoor.isChecked()) {
            type = "InDoor";
        } else if (rbOutdoor.isChecked()) {
            type = "OutDoor";
        } else if (rbOnline.isChecked()) {
            type = "Online";
        } else {
            type = "";
        }

        dateAndTime = checkString(etDateTime.getText().toString());
        capacity = checkString(etCapacity.getText().toString());
        budget = checkString(etBudget.getText().toString());
        email = checkString(etEmail.getText().toString());
        phone = checkString(etPhone.getText().toString());
        description = checkString(etDescription.getText().toString());

        return new Event(key, name, place, type, dateAndTime, capacity, budget, email, phone, description);
    }

    private boolean validateData(Event event) {
        ArrayList<String> data = event.getDataAsArray();
        ArrayList<String> labels = event.getLabels();
        int c = 0;
        errorMessage = "";
        System.out.println("\nValidate Event Information:");
        System.out.println("Invalid Data:");
        for (int i = 1; i < data.size(); i++) {
            if (data.get(i).equals("")) {
                System.out.println(labels.get(i) + ": " + data.get(i));
                errorMessage += "Invalid " + labels.get(i).toLowerCase(Locale.ROOT) + "\n";
                c++;
            } else if (labels.get(i).equals("Type") && data.get(i).equals("")) {
                System.out.println("InDoor: false\nOutDoor: false\nOnline: false");
                errorMessage += "Invalid " + labels.get(i) + "\n";
                c++;
            } else if (labels.get(i).equals("Email") && !data.get(i).contains("@") && !data.get(i).contains(".")) {
                errorMessage += "Invalid " + labels.get(i) + "\n";
                c++;
            } else if (labels.get(i).equals("Phone") && data.get(i).length() != 11) {
                System.out.println(labels.get(i) + ": " + data.get(i));
                errorMessage += "Invalid " + labels.get(i) + "\n";
                c++;
            }
        }

        if (c == 0) {
            System.out.println("There is no Invalid Data");
            return false;
        } else {
            System.out.println("Total " + c + " Invalid Data");
            return true;
        }
    }

    private void showDialog(String message, String title, String btn01, String btn02, Event event) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(CreateEventActivity.this);

        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setCancelable(false).setPositiveButton(btn01, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (btn01.equals("Yes")) {
                    try {
                        KeyValueDB kvDB = new KeyValueDB(CreateEventActivity.this);
                        String data = event.getData();

                        if (checkString(btSave.getText().toString()).equals("Save")) {
                            if (Boolean.TRUE.equals(kvDB.insertKeyValue(event.getKey(), data))) {
                                clearData();
                                Toast.makeText(getApplicationContext(), "Event information Successfully Saved!", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error to insert", Toast.LENGTH_LONG).show();
                            }
                        } else if (checkString(btSave.getText().toString()).equals("Update")) {
                            if (Boolean.TRUE.equals(kvDB.updateValueByKey(event.getKey(), data))) {
                                clearData();
                                Toast.makeText(getApplicationContext(), "Event information Successfully Updated!", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error to update", Toast.LENGTH_LONG).show();
                            }
                        }
                        kvDB.close();

                        if (checkString(btSave.getText().toString()).equals("Save") || checkString(btSave.getText().toString()).equals("Update")) {
                            String keys[] = {"action", "id", "semester", "key", "event"};
                            String value[] = {"backup", "id", "semester", event.getKey(), data};
                            httpRequest(keys, value);
                        }
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

    private void dumpCursorToString(Cursor cursor) {
        int count = 0;
        System.out.println("\nDumping cursor to string " + cursor + " :\n");
        if (cursor != null) {
            int startPos = cursor.getPosition();
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                dumpCurrentRow(cursor);
                count++;
            }
            cursor.moveToPosition(startPos);
            if (count == 0) {
                System.out.println("No data in the cursor\n");
            }
        } else {
            System.out.println("Cursor is null\n");
        }
    }

    private void dumpCurrentRow(Cursor cursor) {
        String[] cols = cursor.getColumnNames();
        String key = "";
        int length = cols.length;

        System.out.println("Position " + cursor.getPosition() + ":\n");
        for (int i = 0; i < length; i++) {
            try {
                if (i == 0) {
                    key = cursor.getString(i);
                } else {
                    String values = cursor.getString(i);
                    ArrayList<String> list = new ArrayList<>(Arrays.asList(values.split(", ")));
                    events.add(new Event(key, list.get(0), list.get(1), list.get(2), list.get(3), list.get(4), list.get(5), list.get(6), list.get(7), list.get(8)));
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }

        System.out.println(events.get(events.size() - 1).getDataAsString());
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
                    //updateEventListByServerData(data);
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        etName = findViewById(R.id.etName);
        etPlace = findViewById(R.id.etPlace);
        etDateTime = findViewById(R.id.etDateTime);
        etCapacity = findViewById(R.id.etCapacity);
        etBudget = findViewById(R.id.etBudget);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);

        tvError = findViewById(R.id.tvError);

        rbIndoor = findViewById(R.id.rbIndoor);
        rbOutdoor = findViewById(R.id.rbOutdoor);
        rbOnline = findViewById(R.id.rbOnline);

        btCancel = findViewById(R.id.btCancel);
        btShare = findViewById(R.id.btShare);
        btSave = findViewById(R.id.btSave);

        String key = checkString(getIntent().getStringExtra("key"));

        if (!key.equals("")) {
            btShare.setVisibility(View.GONE);
            btCancel.setText("Finish");
            btSave.setText("Update");

            try {
                KeyValueDB kvDB = new KeyValueDB(CreateEventActivity.this);
                String values = kvDB.getValueByKey(key);
                System.out.println(values);
                ArrayList<String> list = new ArrayList<>(Arrays.asList(values.split(", ")));

                etName.setText(list.get(0));
                etPlace.setText(list.get(1));

                switch (list.get(2)) {
                    case "InDoor":
                        rbIndoor.setChecked(true);
                        break;
                    case "OutDoor":
                        rbOutdoor.setChecked(true);
                        break;
                    case "Online":
                        rbOnline.setChecked(true);
                        break;
                    default:
                        rbIndoor.setChecked(false);
                        rbOutdoor.setChecked(false);
                        rbOnline.setChecked(false);
                }

                etDateTime.setText(list.get(3));
                etCapacity.setText(list.get(4));
                etBudget.setText(list.get(5));
                etEmail.setText(list.get(6));
                etPhone.setText(list.get(7));
                etDescription.setText(list.get(8));

                kvDB.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearData();

                if (checkString(btCancel.getText().toString()).equals("Cancel")) {
                    Toast.makeText(getApplicationContext(), "Event Form Canceled!", Toast.LENGTH_LONG).show();
                } else if (checkString(btCancel.getText().toString()).equals("Finish")) {
                    Toast.makeText(getApplicationContext(), "Event Form Finished!", Toast.LENGTH_LONG).show();
                }

                //Intent i = new Intent(view.getContext(), UpcomingEvents.class);
                //startActivity(i);

                finish();
            }
        });

        btShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearData();

                Toast.makeText(getApplicationContext(), "Event Form Successfully Shared!", Toast.LENGTH_LONG).show();
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event event = getFormData();
                boolean invalid = validateData(event);

                if (checkString(btSave.getText().toString()).equals("Save")) {
                    if (invalid) {
                        tvError.setText(errorMessage);
                        showDialog(errorMessage, "Error", "Ok", null, null);
                    } else {
                        showDialog("Do you want to save this event information?", "Event Information", "Yes", "No", event);
                        System.out.println(event.getDataAsString());
                    }
                } else if (checkString(btSave.getText().toString()).equals("Update")) {
                    if (invalid) {
                        tvError.setText(errorMessage);
                        showDialog(errorMessage, "Error", "Ok", null, null);
                    } else {
                        event.setKey(key);
                        showDialog("Do you want to update this event information?", "Event Information", "Yes", "No", event);
                        System.out.println(event.getDataAsString());
                    }
                }

                try {
                    KeyValueDB kvDB = new KeyValueDB(CreateEventActivity.this);
                    Cursor cursor = kvDB.getAllKeyValues();
                    dumpCursorToString(cursor);
                    kvDB.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}