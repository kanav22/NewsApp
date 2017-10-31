package com.example.android.newsListing;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Helper methods related to requesting and receiving Book data from Google API.
 */
public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    public static List<News> fetchDataFromUrl(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, " " + R.string.Http_problem_message, e);
        }

        ArrayList<News> newslist = fetchNewsData(jsonResponse);

        return newslist;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, " " + R.string.url_problem_message, e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "" + R.string.response_error + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "" + R.string.json_problem, e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String formatDate(String rawDate) {
        String jsonDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat jsonFormatter = new SimpleDateFormat(jsonDatePattern, Locale.US);
        try {
            Date parsedJsonDate = jsonFormatter.parse(rawDate);
            String finalDatePattern = "MMM d, yyy";
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalDatePattern, Locale.US);
            return finalDateFormatter.format(parsedJsonDate);
        } catch (ParseException e) {
            Log.e("QueryUtils", " " + R.string.error_json_date, e);
            return "";
        }
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static ArrayList<News> fetchNewsData(String SAMPLE_JSON_RESPONSE) {

        if (TextUtils.isEmpty(SAMPLE_JSON_RESPONSE)) {
            return null;
        }
        ArrayList<News> newsList = new ArrayList<>();
        try {
            JSONObject jsonResponse = new JSONObject(SAMPLE_JSON_RESPONSE);
            JSONObject jsonResults = jsonResponse.getJSONObject("response");
            JSONArray resultsArray = jsonResults.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentResult = resultsArray.getJSONObject(i);
                String webTitle = currentResult.getString("webTitle");
                Log.d("Web Title", webTitle);

                String url = currentResult.getString("webUrl");
                String date = currentResult.getString("webPublicationDate");
                date = formatDate(date);
                Log.d("Date", date);
                String section = currentResult.getString("sectionName");
                String author = "";
                if (currentResult.has("tags")) {
                    JSONArray tagsArray = currentResult.getJSONArray("tags");

                    if (tagsArray.length() == 0) {
                        author = null;
                    } else {
                        for (int j = 0; j < tagsArray.length(); j++) {
                            JSONObject firstObject = tagsArray.getJSONObject(j);
                            author += firstObject.getString("webTitle") + ". ";
                        }
                    }

                }
                newsList.add(new News(webTitle, author, url, date, section));
            }
        } catch (JSONException e) {
            Log.e("Queryutils", " " + R.string.error_json_response, e);
        }
        return newsList;
    }

}