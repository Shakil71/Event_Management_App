package edu.ewubd.event;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    TextView tvTittle, tvActionTittle, tvAction;
    TableRow trName, trEmail, trPhone, trRePass;
    EditText etName, etEmail, etPhone, etUserID, etPass, etRePass;
    CheckBox cbUserID, cbPass;
    Button btExit, btGo;
    String title, name, email, phone, userID, pass, rePass, errorMessages;
    boolean rememberUserID, rememberPassword;
    SharedPreferences sp;

    private String checkString(String string) {
        if (string == null || string.isEmpty()) {
            string = "";
        }
        return string;
    }

    private void setActionName(String string) {
        SpannableString content = new SpannableString(string);
        content.setSpan(new UnderlineSpan(), 0, string.length(), 0);
        tvAction.setText(content);
    }

    private void clearData() {
        etName.setText(null);
        etEmail.setText(null);
        etPhone.setText(null);
        etUserID.setText(null);
        etPass.setText(null);
        etRePass.setText(null);

        cbUserID.setChecked(false);
        cbPass.setChecked(false);
    }

    private void loginPage() {
        trName.setVisibility(View.GONE);
        trEmail.setVisibility(View.GONE);
        trPhone.setVisibility(View.GONE);
        trRePass.setVisibility(View.GONE);

        tvTittle.setText("Login");
        tvActionTittle.setText("Create a account?");

        setActionName("Signup");

        rememberUserID = Boolean.parseBoolean(sp.getString("rememberUserID", "false"));
        rememberPassword = Boolean.parseBoolean(sp.getString("rememberPassword", "false"));

        if (rememberUserID) {
            etUserID.setText(sp.getString("userID", ""));
            cbUserID.setChecked(true);
        }
        if (rememberPassword) {
            etPass.setText(sp.getString("password", ""));
            cbPass.setChecked(true);
        }
    }

    private void signupPage() {
        trName.setVisibility(View.VISIBLE);
        trEmail.setVisibility(View.VISIBLE);
        trPhone.setVisibility(View.VISIBLE);
        trRePass.setVisibility(View.VISIBLE);

        tvTittle.setText("Signup");
        tvActionTittle.setText("Already have account?");

        setActionName("Login");
        clearData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("userData", MODE_PRIVATE);
        setContentView(R.layout.activity_signup);

        tvTittle = findViewById(R.id.tvTittle);
        tvActionTittle = findViewById(R.id.tvActionTittle);
        tvAction = findViewById(R.id.tvAction);

        trName = findViewById(R.id.trName);
        trEmail = findViewById(R.id.trEmail);
        trPhone = findViewById(R.id.trPhone);
        trRePass = findViewById(R.id.trRePass);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etUserID = findViewById(R.id.etUserID);
        etPass = findViewById(R.id.etPass);
        etRePass = findViewById(R.id.etRePass);

        cbUserID = findViewById(R.id.cbUserID);
        cbPass = findViewById(R.id.cbPass);

        btExit = findViewById(R.id.btExit);
        btGo = findViewById(R.id.btGo);

        if (sp.getString("userID", "").equals("")) {
            signupPage();
        } else {
            loginPage();
        }

        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = checkString(tvTittle.getText().toString()).trim();

                if (title.equals("Signup")) {
                    loginPage();
                } else if (title.equals("Login")) {
                    signupPage();
                }
            }
        });

        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = checkString(tvTittle.getText().toString()).trim();
                userID = checkString(etUserID.getText().toString()).trim();
                pass = checkString(etPass.getText().toString()).trim();

                if (title.equals("Signup")) {
                    errorMessages = "";

                    name = checkString(etName.getText().toString()).trim();
                    email = checkString(etEmail.getText().toString()).trim();
                    phone = checkString(etPhone.getText().toString()).trim();
                    rePass = checkString(etRePass.getText().toString()).trim();

                    if (name.equals("") || email.equals("") || phone.equals("") || userID.equals("") || pass.equals("") || rePass.equals("")) {
                        errorMessages += "Invalid input\n";
                    }
                    if (!pass.equals("") && !rePass.equals("") && !pass.equals(rePass)) {
                        errorMessages += "Password don't match\n";
                    }
                    if (!email.contains("@") || !email.contains(".")) {
                        errorMessages += "Invalid email address\n";
                    }
                    if (errorMessages.length() == 0) {
                        rememberUserID = cbUserID.isChecked();
                        rememberPassword = cbPass.isChecked();

                        SharedPreferences.Editor spEditor = sp.edit();
                        spEditor.putString("name", name);
                        spEditor.putString("email", email);
                        spEditor.putString("phone", phone);
                        spEditor.putString("userID", userID);
                        spEditor.putString("password", pass);
                        spEditor.putString("rememberUserID", String.valueOf(rememberUserID));
                        spEditor.putString("rememberPassword", String.valueOf(rememberPassword));
                        spEditor.apply();

                        clearData();
                        loginPage();

                        Toast.makeText(getApplicationContext(), "Successfully signup", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), errorMessages, Toast.LENGTH_LONG).show();
                    }
                } else if (title.equals("Login")) {
                    errorMessages = "";

                    SharedPreferences.Editor spEditor = sp.edit();

                    String mUserID = sp.getString("userID", "");
                    String mPass = sp.getString("password", "");

                    if (!checkString(mUserID).equals("") && !checkString(mPass).equals("") && userID.equals(mUserID) && pass.equals(mPass)) {
                        rememberUserID = cbUserID.isChecked();
                        rememberPassword = cbPass.isChecked();

                        spEditor.putString("rememberUserID", String.valueOf(rememberUserID));
                        spEditor.putString("rememberPassword", String.valueOf(rememberPassword));
                        spEditor.apply();

                        Intent i = new Intent(v.getContext(), UpcomingEvents.class);
                        startActivity(i);

                        Toast.makeText(getApplicationContext(), "Successfully login", Toast.LENGTH_LONG).show();

                        finish();
                    } else {
                        errorMessages += "Invalid user ID and password";
                        Toast.makeText(getApplicationContext(), errorMessages, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), errorMessages, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}