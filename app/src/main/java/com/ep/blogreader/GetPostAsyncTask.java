package com.ep.blogreader;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Eduardo on 09/10/2015.
 */
public class GetPostAsyncTask extends AsyncTask<Void, Void, JSONObject> {

    private static final String TAG = "GetPostAsyncTask";
    private ProgressBar progressBar;
    private JSONObject jsonBlog;
    private Context context;
    private MainListActivity activity;
    private UpdateJsonBlog updateJsonBlog;


    public GetPostAsyncTask(Context context, ProgressBar progressBar, JSONObject jsonBlog, MainListActivity activity) {
        this.progressBar = progressBar;
        this.jsonBlog = jsonBlog;
        this.context = context;
        this.activity = activity;
        this.updateJsonBlog = (UpdateJsonBlog) this.activity;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        int responseCode = -1;
        JSONObject jsonResponse = null;

        try {
            URL blogFeedUrl = new URL(
                    "http://blog.teamtreehouse.com/api/get_recent_summary/?count=" + MainListActivity.POSTS_TO_SHOW);
            HttpURLConnection connection = (HttpURLConnection) blogFeedUrl.openConnection();
            connection.connect();

            responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                Reader reader = new InputStreamReader(inputStream);
                int contentLength = connection.getContentLength();
                char[] charArray = new char[contentLength];
                reader.read(charArray);
                String responseData = new String(charArray);
                jsonResponse = new JSONObject(responseData);
            }
            else {
                Log.i(TAG, "Unsuccessful HTTP Response Code: " + responseCode);
            }
        }
        catch (MalformedURLException e) {
            logException(e);
        }
        catch (IOException e) {
            logException(e);
        }
        catch (Exception e) {
            logException(e);
        }

        return jsonResponse;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        jsonBlog = result;
        handleBlogResponse();
        updateJsonBlog.update(jsonBlog);
    }

    private void logException(Exception e) {
        Log.e(TAG, "Error trying to connect with the blog!", e);
    }

    public void handleBlogResponse() {
        progressBar.setVisibility(View.INVISIBLE);

        if (jsonBlog == null) {
            updateDisplayForError();
        }
        else {
            try {
                JSONArray jsonPosts = jsonBlog.getJSONArray("posts");
                ArrayList<HashMap<String, String>> blogPosts =
                        new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < jsonPosts.length(); i++) {
                    JSONObject post = jsonPosts.getJSONObject(i);
                    String title = post.getString(MainListActivity.TITLE);
                    title = Html.fromHtml(title).toString();
                    String author = post.getString(MainListActivity.AUTHOR);
                    author = Html.fromHtml(author).toString();

                    HashMap<String, String> blogPost = new HashMap<String, String>();
                    blogPost.put(MainListActivity.TITLE, title);
                    blogPost.put(MainListActivity.AUTHOR, author);

                    blogPosts.add(blogPost);
                }

                String[] keys = {MainListActivity.TITLE, MainListActivity.AUTHOR};
                int[] ids = { android.R.id.text1, android.R.id.text2 };
                SimpleAdapter adapter = new SimpleAdapter(context, blogPosts,
                        android.R.layout.simple_list_item_2,
                        keys, ids);

                activity.setListAdapter(adapter);
            }
            catch (JSONException e) {
                logException(e);
            }
        }
    }

    public void updateDisplayForError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(activity.getString(R.string.error_title));
        builder.setMessage(activity.getString(R.string.error_message));
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();

        TextView emptyTextView = (TextView) activity.getListView().getEmptyView();
        emptyTextView.setText(activity.getString(R.string.no_items));
    }

}
