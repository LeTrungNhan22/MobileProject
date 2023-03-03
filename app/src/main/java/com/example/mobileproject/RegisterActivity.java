package com.example.mobileproject;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    EditText username, fullName, email, password, phone_number;
    ImageView show_password_icon;
    private boolean passwordShowing = false;
    Button register;
    TextView txt_login;

    FirebaseAuth auth;

    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone_number = findViewById(R.id.phone_number);
        show_password_icon = findViewById(R.id.show_password_icon);

        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);

        auth = FirebaseAuth.getInstance();


        show_password_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                 Xem rằng mật khẩu có được hiển thị hay không
                if (passwordShowing) {
                    passwordShowing = false;
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    show_password_icon.setImageResource(R.drawable.show_password);
                } else {
                    passwordShowing = true;
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    show_password_icon.setImageResource(R.drawable.hide_password);

                }
//                Đặt con trỏ cuối cùng của EditText
                password.setSelection(password.length());
            }
        });

        txt_login.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_username = username.getText().toString();
                String str_fullName = fullName.getText().toString();
                String str_email = email.getText().toString();
                String str_phone_number = phone_number.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_username) ||
                        TextUtils.isEmpty(str_fullName) ||
                        TextUtils.isEmpty(str_email) ||
                        TextUtils.isEmpty(str_password) ||
                        TextUtils.isEmpty(str_phone_number)
                ) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else if (str_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();

                } else {
                    pd = new ProgressDialog(RegisterActivity.this);
                    pd.setMessage("Vui lòng chờ...");
                    pd.show();
//                    register(str_username, str_fullName, str_email, str_password, str_phone_number);

                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(auth)
                                    .setPhoneNumber(String.valueOf(Integer.parseInt(str_phone_number)))       // Phone number to verify
                                    .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(RegisterActivity.this)                 // Activity (for callback binding)
                                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {

                                        }

                                        @Override
                                        public void onCodeSent(@NonNull String verificationId,
                                                               @NonNull PhoneAuthProvider.ForceResendingToken token) {

                                            Log.d(TAG, "onCodeSent:" + verificationId);


                                        }

                                    })          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);


//                   chuyển sang trang OTP
                    Intent intent = new Intent(RegisterActivity.this, OTPVerification.class);
                    intent.putExtra("username", str_username);
                    intent.putExtra("fullName", str_fullName);
                    intent.putExtra("email", str_email);
                    intent.putExtra("phone_number", str_phone_number);
                    intent.putExtra("password", str_password);

                    startActivity(intent);
                }
            }
        });


    }

    private void register(String username, String fullName, String email, String password, String phone_number) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String uid = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                    HashMap<String, Object> hashMap = new HashMap<>();

                    hashMap.put("id", uid);
                    hashMap.put("username", username.toLowerCase());
                    hashMap.put("fullName", fullName);
                    hashMap.put("email", email);
                    hashMap.put("phone_number", phone_number);
                    hashMap.put("bio", "");
                    hashMap.put("imageURL", "https://firebasestorage.googleapis.com/v0/b/mobileproject-30a59.appspot.com/o/common%2FAvatar.png?alt=media&token=b2cc192a-ea6a-4f7d-8916-a10ab61352c3");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                pd.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }
                        }
                    });

                } else {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Bạn không thể đăng ký với email này", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}