package com.sciecwlms.firebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{
    private ProgressDialog mProgressDialog;
    private EditText edtEmail, edtPassword;
    private Button btnLogin;

    private FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Login");
        mProgressDialog.setMessage("Please wait..");

        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnLogin = (Button) findViewById(R.id.btn_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(this);

        if (mFirebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {

            boolean isEmptyField = false;

            final String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            //validation
            if (TextUtils.isEmpty(email)) {
                isEmptyField = true;
                edtEmail.setError("Enter valid email");
            }

            if (TextUtils.isEmpty(password)) {
                isEmptyField = true;
                edtPassword.setError("Enter valid password");
            }

            if (!isEmptyField) {
                mProgressDialog.show();
                mFirebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    mProgressDialog.dismiss();
                                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                                    Toast.makeText(SignInActivity.this, "Welcome "+currentFirebaseUser.getDisplayName(), Toast.LENGTH_LONG).show();
                                    AppPreference mAppPreference = new AppPreference(SignInActivity.this);
                                    mAppPreference.setEmail(currentFirebaseUser.getEmail());
                                    mAppPreference.setId(currentFirebaseUser.getUid());

                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(SignInActivity.this, "Login user unsuccessful", Toast.LENGTH_SHORT).show();
                                    if (mProgressDialog.isShowing()) {
                                        mProgressDialog.dismiss();
                                    }
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}