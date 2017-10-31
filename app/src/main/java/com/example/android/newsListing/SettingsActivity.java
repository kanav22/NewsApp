package com.example.android.newsListing;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference searchNewsData = findPreference(getString(R.string.settings_search_by_news_key));
            bindPreferenceSummaryToValue(searchNewsData);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String Value = value.toString();
            preference.setSummary(Value);
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceStringData = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceStringData);
        }
    }
}