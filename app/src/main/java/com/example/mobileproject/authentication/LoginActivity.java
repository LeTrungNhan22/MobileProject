package com.example.mobileproject.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileproject.MainActivity;
import com.example.mobileproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button login;
    TextView txt_signup;
    RelativeLayout loginWithGoogleBtn;
    ImageView show_password_icon;

    private boolean passwordShowing = false;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        txt_signup = findViewById(R.id.txt_signup);
        loginWithGoogleBtn = findViewById(R.id.loginWithGoogleBtn);
        show_password_icon = findViewById(R.id.show_password_icon);

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


        auth = FirebaseAuth.getInstance();

        txt_signup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        login.setOnClickListener(v -> {
            ProgressDialog pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Vui lòng chờ...");
            pd.show();

            String txt_email = email.getText().toString();
            String txt_password = password.getText().toString();

            if (txt_email.isEmpty() || txt_password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Không được để trống email và mật khẩu", Toast.LENGTH_SHORT).show();
            } else {
                auth.signInWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(auth.getCurrentUser().getUid());

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                pd.dismiss();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                pd.dismiss();

                            }
                        });
                    } else {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}