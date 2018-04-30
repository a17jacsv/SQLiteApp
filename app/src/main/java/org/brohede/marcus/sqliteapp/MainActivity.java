package org.brohede.marcus.sqliteapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;
import static org.brohede.marcus.sqliteapp.R.id.list_view;

public class MainActivity extends AppCompatActivity {

    protected ArrayList<GetMountains> mountainlist = new ArrayList<>();
    ListView myListView;

    MountainReaderDbHelper mDbHelper;

    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("jacobsdata", MountainReaderContract.SQL_CREATE_ENTRIES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MountainReaderDbHelper mDbHelper = new MountainReaderDbHelper(this);

        // Gets the data repository in write mode
        db = mDbHelper.getWritableDatabase();


        SQLiteDatabase db1 = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                MountainReaderContract.MountainEntry.COLUMN_NAME_NAME,
                MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION,
                MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGHT,
                MountainReaderContract.MountainEntry.COLUMN_NAME_IMAGEURL,
                MountainReaderContract.MountainEntry.COLUMN_NAME_WIKIURL
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = MountainReaderContract.MountainEntry.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = { "Aconcagua" };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION  + " DESC";

        Cursor cursor = db1.query(
                MountainReaderContract.MountainEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order

        );

        Log.d("testkebe", cursor.toString());

        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry._ID));
                Log.d("testkebe", cursor.getString(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_NAME_NAME)));
            itemIds.add(itemId);
        }
        cursor.close();

        Brorsan getJson = new Brorsan();
        getJson.execute();

        ListView myListView = (ListView) findViewById(list_view);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Toast.makeText(getApplicationContext(), mountainlist.get(position).utmatare(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mountainlist.clear();
            new Brorsan().execute();
            Toast refreshed = Toast.makeText(this, "List have been refreshed", Toast.LENGTH_SHORT);
            refreshed.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class Brorsan extends AsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            String s = new String(o.toString());
            Log.d("Jacob","DataFetched"+s);

            try {
                JSONArray mountaindata = new JSONArray(s);

                for(int i = 0; i < mountaindata.length(); i++){
                    JSONObject mountain = mountaindata.getJSONObject(i);

                    String name = mountain.getString("name");
                    String location = mountain.getString("location");
                    int height = mountain.getInt("size");

                    String auxdata = mountain.getString("auxdata");
                    JSONObject aux = new JSONObject(auxdata);
                    String url = aux.getString("img");

                    GetMountains m = new GetMountains(name, height, location, url);
                    mountainlist.add(m);

                    ContentValues values = new ContentValues();
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_NAME, name);
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION, location);
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGHT, height);
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_WIKIURL, url);
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_IMAGEURL, auxdata);

                    db.insert(MountainReaderContract.MountainEntry.TABLE_NAME, null, values);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }


            ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item_textview, R.id.my_item_textview, mountainlist);

            myListView = (ListView)findViewById(list_view);
            myListView.setAdapter(adapter);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String jsonStr = null;

            try {
                // Construct the URL for the php-service
                URL url = new URL("http://wwwlab.iit.his.se/brom/kurser/mobilprog/dbservice/admin/getdataasjson.php?type=brom");

                // Create the request to the PHP-service, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
                return jsonStr;

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Network error", "Error closing stream", e);
                    }
                }
            }
        }
    }
}


    /*
        TODO: Create an App that stores Mountain data in SQLite database

        TODO: Schema for the database must include columns for all member variables in Mountain class
              See: https://developer.android.com/training/data-storage/sqlite.html

        TODO: The Main Activity must have a ListView that displays the names of all the Mountains
              currently in the local SQLite database.

        TODO: In the details activity an ImageView should display the img_url
              See: https://developer.android.com/reference/android/widget/ImageView.html

        TODO: The main activity must have an Options Menu with the following options:
              * "Fetch mountains" - Which fetches mountains from the same Internet service as in
                "Use JSON data over Internet" assignment. Re-use code.
              * "Drop database" - Which drops the local SQLite database

        TODO: All fields in the details activity should be EditText elements

        TODO: The details activity must have a button "Update" that updates the current mountain
              in the local SQLite database with the values from the EditText boxes.
              See: https://developer.android.com/training/data-storage/sqlite.html

        TODO: The details activity must have a button "Delete" that removes the
              current mountain from the local SQLite database
              See: https://developer.android.com/training/data-storage/sqlite.html

        TODO: The SQLite database must not contain any duplicate mountain names

     */
