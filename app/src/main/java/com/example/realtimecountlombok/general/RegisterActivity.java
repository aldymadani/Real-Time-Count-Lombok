package com.example.realtimecountlombok.general;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.realtimecountlombok.R;
import com.example.realtimecountlombok.admin.MainManageSuaraActivity;
import com.example.realtimecountlombok.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private TextInputLayout textInputEmail, textInputPassword, textInputConfirmPassword;
    private EditText emailId, passwordId, confirmPasswordId;
    private Button btnSignUp;
    private FirebaseAuth mFirebaseAuth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InitializeComponents();
        progressBar.setVisibility(View.INVISIBLE);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String password = passwordId.getText().toString();
                String confirmPassword = confirmPasswordId.getText().toString();
                emailId.clearFocus();
                passwordId.clearFocus();
                confirmPasswordId.clearFocus();
                Util.hideKeyboard(RegisterActivity.this);
                if (allFieldValidation(email, password, confirmPassword)) {
                    registerUser(email, password);
                }
            }
        });
    }

    protected boolean allFieldValidation(String email, String password, String confirmPassword) {
        boolean isValidated = false;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // Email checking
        boolean emailValidation = false;
        if (email.isEmpty()) {
            textInputEmail.setError("Please input your email");
        } else if (!email.matches(emailPattern)) {
            textInputEmail.setError("Please input a valid email");
        } else {
            emailValidation = true;
            textInputEmail.setErrorEnabled(false);
        }

        // Password checking
        boolean passwordValidation = false;
        if (password.isEmpty() && confirmPassword.isEmpty()) {
            textInputPassword.setError("Please enter your password");
            textInputConfirmPassword.setError("Please enter your password");
        } else {
            boolean passwordValid = false;
            boolean confirmPasswordValid = false;
            if (password.isEmpty()) {
                textInputPassword.setError("Please enter your password");
            } else if (password.length() <= 5) {
                textInputPassword.setError("Password minimum is 6 digits");
            } else {
                passwordValid = true;
                textInputPassword.setErrorEnabled(false);
            }

            if (confirmPassword.isEmpty()) {
                textInputConfirmPassword.setError("Please enter your password");
            } else if (confirmPassword.length() <= 5) {
                textInputConfirmPassword.setError("Password minimum is 6 digits");
            } else {
                confirmPasswordValid = true;
                textInputConfirmPassword.setErrorEnabled(false);
            }

            if (passwordValid && confirmPasswordValid) {
                if (!password.equals(confirmPassword)) {
                    textInputConfirmPassword.setError("The password is not matching");
                } else {
                    textInputConfirmPassword.setErrorEnabled(false);
                    passwordValidation = true;
                }
            }
        }

        // Register user
        if (emailValidation && passwordValidation) {
            isValidated = true;
        }

        return isValidated;
    }

    protected void registerUser(String email, String password
    ) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                        textInputEmail.setError("Email is already used");
                    } else {
                        Toast.makeText(RegisterActivity.this, "Sign Up is unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Intent intent = new Intent(RegisterActivity.this, MainManageSuaraActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(RegisterActivity.this, "You are successfully registered!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void InitializeComponents() {
        emailId = findViewById(R.id.registerEmail);
        passwordId = findViewById(R.id.registerPassword);
        confirmPasswordId = findViewById(R.id.registerConfirmPassword);
        textInputEmail = findViewById(R.id.registerEmailLayout);
        textInputPassword = findViewById(R.id.registerPasswordLayout);
        textInputConfirmPassword = findViewById(R.id.registerConfirmPasswordLayout);
        btnSignUp = findViewById(R.id.registerKonfirmasi);
        progressBar = findViewById(R.id.registerProgressBar);

        emailId.setOnFocusChangeListener(this);
        passwordId.setOnFocusChangeListener(this);
        confirmPasswordId.setOnFocusChangeListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.registerEmail:
                textInputEmail.setErrorEnabled(false);
                break;
            case R.id.registerPassword:
                textInputPassword.setErrorEnabled(false);
                break;
            case R.id.registerConfirmPassword:
                textInputConfirmPassword.setErrorEnabled(false);
                break;
        }
    }
}