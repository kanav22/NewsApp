/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.newsListing;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {
    public static final String LOG_TAG = NewsActivity.class.getName();
    static final String SEARCH_RESULTS = "" + R.string.searchresults;
    private static final String URL = "http://content.guardianapis.com/search?&=";
    // Constant for the API search Key
    private static final String API_KEY = "api-key";
    // Constant value for the API Key
    private static final String KEY = "test";
    private static final int NEWS_LOADER_ID = 1;
    private TextView mEmptyStateTextView;
    private NewsAdapter mAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Open the Settings page through an Intent when the Settings icon is clicked or tapped on
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, com.example.android.newsListing.SettingsActivity.class);
            startActivity(settingsIntent);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String searchSection = sharedPreferences.getString(
                getString(R.string.settings_search_by_news_key),
                getString(R.string.settings_news_label));

        // Create an URI and an URI Builder
        Uri baseUri = Uri.parse(URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append the search parameters to the request URL
        uriBuilder.appendQueryParameter("q", searchSection);
        uriBuilder.appendQueryParameter(API_KEY, KEY);

        // Create a NewsLoader with the request URL
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        mAdapter.clear();
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);
        ListView newsListView = (ListView) findViewById(R.id.list);
        Parcelable state = newsListView.onSaveInstanceState();

        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);
        newsListView.onRestoreInstanceState(state);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                News news = mAdapter.getItem(i);
                String url = news.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        if (isNetworkConnected()) {

            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        } else {
            Toast.makeText(NewsActivity.this, R.string.Nointernet_message,
                    Toast.LENGTH_SHORT).show();
        }

        if (savedInstanceState != null) {
            News[] newsList = (News[]) savedInstanceState.getParcelableArray(SEARCH_RESULTS);
            mAdapter.addAll(newsList);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        News[] newsList = new News[mAdapter.getCount()];
        for (int i = 0; i < newsList.length; i++) {
            newsList[i] = mAdapter.getItem(i);
        }
        outState.putParcelableArray(SEARCH_RESULTS, (Parcelable[]) newsList);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}



