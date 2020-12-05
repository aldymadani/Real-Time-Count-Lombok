package com.example.realtimecountlombok.general;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.realtimecountlombok.R;
import com.example.realtimecountlombok.admin.MainManageSuaraActivity;
import com.example.realtimecountlombok.owner.PilihKecamatanActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EditText emailField, passwordField;
    private Button btnSubmit, btnRegister, totalSuaraButton, totalSuaraKecamatanButton, registerButton;
    private FirebaseAuth mFirebaseAuth;
    private TextInputLayout textInputEmail, textInputPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailField = findViewById(R.id.loginEmail);
        passwordField = findViewById(R.id.loginPassword);
        btnSubmit = findViewById(R.id.loginSignInButton);
        btnRegister = findViewById(R.id.loginCekSuaraButton);
        textInputEmail = findViewById(R.id.loginEmailLayout);
        textInputPassword = findViewById(R.id.loginPasswordLayout);
        registerButton = findViewById(R.id.loginRegisterButton);

        emailField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                textInputEmail.setErrorEnabled(false);
            }
        });

        passwordField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                textInputPassword.setErrorEnabled(false);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnSubmit.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    protected boolean inputValidation(String email, String password) {
        boolean isValid = false;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // Email checking
        boolean emailValidation = false;
        if (email.isEmpty()) {
            textInputEmail.setError("Please enter your email");
        } else if (!email.matches(emailPattern)) {
            textInputEmail.setError("Please input a valid email");
        } else {
            emailValidation = true;
            textInputEmail.setErrorEnabled(false);
        }

        // Password checking
        if (password.isEmpty()) {
            textInputPassword.setError("Please enter your password");
        } else {
            textInputPassword.setErrorEnabled(false);
        }

        // User authentication
        if (emailValidation && !password.isEmpty()) {
            isValid = true;
        }

        return isValid;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginCekSuaraButton:
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
                View registerDialog = getLayoutInflater().inflate(R.layout.dialog_pilihan, null);
                mBuilder.setView(registerDialog);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                totalSuaraButton = registerDialog.findViewById(R.id.totalSuaraDialogButton);
                totalSuaraKecamatanButton = registerDialog.findViewById(R.id.totalSuaraKecamatanDialogButton);
                totalSuaraButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LoginActivity.this, TotalSuaraActivity.class);
                        startActivity(i);
                        dialog.dismiss();
                    }
                });
                totalSuaraKecamatanButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setUpPilihKecamatanDialog(dialog);
                    }
                });
                break;
            case R.id.loginSignInButton:
                emailField.clearFocus();
                passwordField.clearFocus();
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                if (inputValidation(email, password)) {
                    btnRegister.setEnabled(false);
                    btnSubmit.setVisibility(View.INVISIBLE);
                    authenticateUser(email, password);
                }
                break;
        }
    }

    private void setUpPilihKecamatanDialog(AlertDialog previousDialog) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
        View pilihKecamatanDialog = getLayoutInflater().inflate(R.layout.dialog_pilihan_kecamatan, null);
        mBuilder.setView(pilihKecamatanDialog);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Button konfirmasiKecamatanButton = pilihKecamatanDialog.findViewById(R.id.pilihKecamatanDialogKonfirmasiButton);
        Spinner spinner = pilihKecamatanDialog.findViewById(R.id.pilihKecamatanDialogSpinner);
        konfirmasiKecamatanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, TotalSuaraKecamatanActivity.class);
                i.putExtra("namaKecamatan", spinner.getSelectedItem().toString());
                startActivity(i);
                dialog.dismiss();
                previousDialog.dismiss();
            }
        });

        String listKecamatan[] = {"Kec. Praya", "Kec. Praya Tengah", "Kec. Praya Barat", "Kec. Praya Barat Daya", "Kec. Praya Timur",
                "Kec. Pujut", "Kec. Janapria", "Kec. Batukliang", "Kec. Batukliang Utara", "Kec. Jonggat", "Kec. Kopang", "Kec. Pringgarata"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listKecamatan);
        spinner.setAdapter(arrayAdapter);
    }

    private void authenticateUser(String email, String password) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        textInputEmail.setError("The account is not registered yet");
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        textInputPassword.setError("The password doesn't match the email address");
                    } else {
                        Toast.makeText(LoginActivity.this, "Login error, please try again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    textInputPassword.setErrorEnabled(false);
                    textInputEmail.setErrorEnabled(false);
                    String uuid = mFirebaseAuth.getCurrentUser().getUid();
                    if (mFirebaseAuth.getCurrentUser().getEmail().equalsIgnoreCase("admin@gmail.com")) {
                        Intent intent = new Intent(LoginActivity.this, PilihKecamatanActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(LoginActivity.this, MainManageSuaraActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
                btnSubmit.setVisibility(View.VISIBLE);
                btnRegister.setEnabled(true);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}