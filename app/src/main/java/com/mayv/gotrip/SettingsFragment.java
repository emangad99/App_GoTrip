package com.mayv.gotrip;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;


public class SettingsFragment extends Fragment {

    View view;
    Spinner spinner;
    Context context;
    private boolean selected = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        spinner = view.findViewById(R.id.spinner_language);
        setPreviousSelectedLang();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent refreshMain = new Intent(getContext(), MainActivity.class);
                if (position == 0) {
                    context = LocaleHelper.setLocale(getContext(), "en");
                    if (selected) {
                        getActivity().finish();
                        getContext().startActivity(refreshMain);
                    }
                } else if (position == 1) {
                    context = LocaleHelper.setLocale(getContext(), "ar");
                    if (selected) {
                        getActivity().finish();
                        getContext().startActivity(refreshMain);
                    }
                }
                selected = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
        return view;
    }

    private void setPreviousSelectedLang() {
        SharedPreferences shared = getActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        String language = shared.getString("AppLanguage", "en");
        if (language.equals("en")) {
            spinner.setSelection(0);
        } else if (language.equals("ar")) {
            spinner.setSelection(1);
        }
    }
}