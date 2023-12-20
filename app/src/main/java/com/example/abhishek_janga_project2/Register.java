package com.example.abhishek_janga_project2;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    EditText fullName, email, password, confirm_password, phone;
    Button registerBtn, login;
    CheckBox isUser;
    boolean isValid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        confirm_password = findViewById(R.id.confirm_password);
        phone = findViewById(R.id.registerPhone);
        isUser = findViewById(R.id.isUser);
        registerBtn = findViewById(R.id.registerBtn);
        login = findViewById(R.id.login);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDataValid = validateFields();
                if (isDataValid) {
                    fAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser fUser = fAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Account Created ", Toast.LENGTH_SHORT).show();
                            DocumentReference docRef = fStore.collection("User").document(fUser.getUid());
                            Map<String, Object> userDetails = new HashMap<>();
                            userDetails.put("FullName", fullName.getText().toString());
                            userDetails.put("Email", email.getText().toString());
                            userDetails.put("Phone Number", phone.getText().toString());
                            userDetails.put("isUser", isUser.isChecked() ? "1" : "0");

                            docRef.set(userDetails);
                            startActivity(new Intent(getApplicationContext(), Login.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String errorMessage = e.getMessage();
                            Toast.makeText(Register.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (isEmpty(fullName) || isEmpty(email) || isEmpty(password) || isEmpty(confirm_password) || isEmpty(phone)) {
            isValid = false;
        }

        if (!isValidName(fullName.getText().toString())) {
            fullName.setError("Invalid name (Only letters and spaces allowed)");
            isValid = false;
        }

        if (!isValidEmail(email.getText().toString())) {
            email.setError("Invalid email please (enter a valid email or only in lower case alphabets)");
            isValid = false;
        }

        if (!isValidPassword(password.getText().toString())) {
            password.setError("Password should be at least 8 characters and contain atleast 1 special character, 1 Uppercase, 1 number");
            isValid = false;
        }

        if (!password.getText().toString().equals(confirm_password.getText().toString())) {
            confirm_password.setError("Passwords do not match");
            isValid = false;
        }

        if (!isValidPhoneNumber(phone.getText().toString())) {
            phone.setError("Invalid phone number (Should be 10 digits)");
            isValid = false;
        }
        if (!isUser.isChecked()) {
            Toast.makeText(Register.this, "Please accept sign up as a user", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }

    private boolean isEmpty(EditText textField) {
        if (textField.getText().toString().isEmpty()) {
            textField.setError("Field cannot be empty");
            return true;
        }
        return false;
    }

    private boolean isValidEmail(String email) {
        // Email validation using a simple regex pattern
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return Pattern.matches(emailPattern, email);
    }

    private boolean isValidPassword(String password) {
        String pattern = "^(?=.*[A-Z])(?=.*[!@#$%^&*()])(?=.*[0-9])(?=.*[a-z]).{8,}$";

        return password.matches(pattern);
    }

    private boolean isValidName(String name) {
        // Validate name using regex (only letters and spaces allowed)
        String namePattern = "^[a-zA-Z ]+$";
        return Pattern.matches(namePattern, name);
    }

    private boolean isValidPhoneNumber(String phone) {
        // Validate phone number (exactly 10 digits)
        String phonePattern = "^[0-9]{10}$";
        return Pattern.matches(phonePattern, phone);
    }
}
