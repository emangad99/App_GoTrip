package com.mayv.gotrip;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterFragment extends Fragment {

    View view;
    EditText lEmail, lPassword, lConfirmPassword;
    Button btnSignUp;
    FirebaseAuth mFirebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LocaleHelper.setLanguage(getContext());
        view = inflater.inflate(R.layout.fragment_register, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        lEmail = view.findViewById(R.id.login_email);
        lPassword = view.findViewById(R.id.login_password);
        lConfirmPassword = view.findViewById(R.id.register_confirm_password);
        btnSignUp = view.findViewById(R.id.button_signUp);

        if (mFirebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(getContext(), LoginRegisterActivity.class);
            startActivity(intent);
            getActivity().finish();

        }

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = lEmail.getText().toString();
                String pass = lPassword.getText().toString();
                String conPass = lConfirmPassword.getText().toString();


                if (TextUtils.isEmpty(email)) {
                    lEmail.setError(" Email is required.");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    lPassword.setError("Password is required.");
                    return;
                }
                if (pass.length() < 6) {
                    lPassword.setError("Password must be greater than 6 characters");
                    return;
                }

                if (TextUtils.isEmpty(conPass)) {
                    lConfirmPassword.setError("confirmed password is required.");
                    return;
                }
                mFirebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "User created", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        return view;
    }
}