package com.example.mobileproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPVerification extends AppCompatActivity {
    private EditText edt_otp1, edt_otp2, edt_otp3, edt_otp4, edt_otp5, edt_otp6;
    private Button btn_confirm_otp;
    TextView resend_OTP_textView;
    //    true after 60 second
    private boolean resendDisabled = false;
    private int resendTime = 90;
    private int selectEDTPositon = 0;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        edt_otp1 = findViewById(R.id.edt_otp1);
        edt_otp2 = findViewById(R.id.edt_otp2);
        edt_otp3 = findViewById(R.id.edt_otp3);
        edt_otp4 = findViewById(R.id.edt_otp4);
        edt_otp5 = findViewById(R.id.edt_otp5);
        edt_otp6 = findViewById(R.id.edt_otp6);

        resend_OTP_textView = findViewById(R.id.resend_OTP_textView);
        btn_confirm_otp = findViewById(R.id.btn_confirm_otp);


        final TextView otp_email = findViewById(R.id.otp_email);
        final TextView otp_mobile = findViewById(R.id.otp_mobile);

        // get the email and mobile number from the previous activity

        String email = getIntent().getStringExtra("email");
        String mobile = getIntent().getStringExtra("phone_number");

        otp_email.setText(email);
        otp_mobile.setText(mobile);

        edt_otp1.addTextChangedListener(textWatcher);
        edt_otp2.addTextChangedListener(textWatcher);
        edt_otp3.addTextChangedListener(textWatcher);
        edt_otp4.addTextChangedListener(textWatcher);
        edt_otp5.addTextChangedListener(textWatcher);
        edt_otp6.addTextChangedListener(textWatcher);

        //by default the keyboard at edt_op1
        showKeyboard(edt_otp1);
        startCountDownTimer();

        resend_OTP_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                handle the resend OTP
                if (resendDisabled) {
                    startCountDownTimer();
                }
            }
        });
        btn_confirm_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String opt1 = edt_otp1.getText().toString();
                String opt2 = edt_otp2.getText().toString();
                String opt3 = edt_otp3.getText().toString();
                String opt4 = edt_otp4.getText().toString();
                String opt5 = edt_otp5.getText().toString();
                String opt6 = edt_otp6.getText().toString();
                String currentOTP = opt1 + opt2 + opt3 + opt4 + opt5 + opt6;

                if (opt1.isEmpty() || opt2.isEmpty() || opt3.isEmpty() || opt4.isEmpty() || opt5.isEmpty() || opt6.isEmpty() || currentOTP.length() < 6) {
                    Toast.makeText(OTPVerification.this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
                    return;
                } else {


                }


            }
        });

    }


    private void showKeyboard(EditText edt_otp) {
        edt_otp.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(edt_otp, InputMethodManager.SHOW_IMPLICIT);
    }

    private void startCountDownTimer() {
        resendDisabled = false;
        resend_OTP_textView.setTextColor(Color.parseColor("#99000000"));

        new CountDownTimer(resendTime * 1000, 100) {

            @Override
            public void onTick(long l) {
                resend_OTP_textView.setText("Đã gửi OTP in " + l / 1000 + "s");
            }

            @Override
            public void onFinish() {
                resendDisabled = true;
                resend_OTP_textView.setText("Gửi lại OTP");
                resend_OTP_textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        }.start();

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() > 0) {
                if (selectEDTPositon == 0) {
                    selectEDTPositon = 1;
                    showKeyboard(edt_otp2);

                } else if (selectEDTPositon == 1) {
                    selectEDTPositon = 2;
                    showKeyboard(edt_otp3);

                } else if (selectEDTPositon == 2) {
                    selectEDTPositon = 3;
                    showKeyboard(edt_otp4);

                } else if (selectEDTPositon == 3) {
                    selectEDTPositon = 4;
                    showKeyboard(edt_otp5);
                } else if (selectEDTPositon == 4) {
                    selectEDTPositon = 5;
                    showKeyboard(edt_otp6);
                } else if (selectEDTPositon == 5) {
                    selectEDTPositon = 6;
                    showKeyboard(edt_otp6);
                }
            }

        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (selectEDTPositon == 6) {
                selectEDTPositon = 5;
                showKeyboard(edt_otp5);
            } else if (selectEDTPositon == 5) {
                selectEDTPositon = 4;
                showKeyboard(edt_otp4);
            } else if (selectEDTPositon == 4) {
                selectEDTPositon = 3;
                showKeyboard(edt_otp3);
            } else if (selectEDTPositon == 3) {
                selectEDTPositon = 2;
                showKeyboard(edt_otp2);
            } else if (selectEDTPositon == 2) {
                selectEDTPositon = 1;
                showKeyboard(edt_otp1);
            } else if (selectEDTPositon == 1) {
                selectEDTPositon = 0;
                showKeyboard(edt_otp1);
            }

            return true;
        } else {

            return super.onKeyUp(keyCode, event);
        }
    }
}