package com.mayv.gotrip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.Arrays;

public class LoginFragment extends Fragment {

    EditText loginEmail, loginPassword;
    FirebaseAuth mFirebaseAuth;
    View view;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private static final String TAG = "LoginFragment";
    SharedPreferences shared;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LocaleHelper.setLanguage(getContext());
        view = inflater.inflate(R.layout.fragment_login, container, false);
        shared = getActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        Button login = view.findViewById(R.id.button_login);
        loginEmail = view.findViewById(R.id.login_email);
        loginPassword = view.findViewById(R.id.login_password);
        mFirebaseAuth = FirebaseAuth.getInstance();
        Button signInButton = view.findViewById(R.id.button_gmail_login);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
                Intent signInIntent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(signInIntent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = loginEmail.getText().toString();
                String pass = loginPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    loginEmail.setError(" Email is required.");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    loginPassword.setError("Password is required.");
                    return;
                }
                if (pass.length() < 6) {
                    loginPassword.setError("Password must be greater than 6 characters");
                    return;
                }
                mFirebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String[] split = user.getEmail().split("@");
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString("UserEmail", user.getEmail());
                            editor.putString("UserID", split[0]);
                            editor.putBoolean("UserLoggedBefore", true);
                            editor.apply();
                            startMainActivity();
                        } else {
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            handleSignInResult(task);
                        }
                    }
                });
        return view;
    }

    private void saveShared(String email, String userId) {
        String[] split = email.split("@|\\.");
        String emailId = split[0] + split[1] + split[2];
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("UserEmail", email);
        editor.putString("UserID", emailId);
        editor.putBoolean("NeedFirebaseLoad", true);
        editor.putBoolean("UserLoggedBefore", true);
        editor.apply();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            saveShared(account.getEmail(), account.getId());
            startMainActivity();
        } catch (ApiException e) {
            Log.e(TAG, "signInResult:failed code = " + e.getMessage());
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}