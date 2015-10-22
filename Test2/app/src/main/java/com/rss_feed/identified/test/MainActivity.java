package com.rss_feed.identified.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    public ListView lv;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.listView);
        try {
            socket = IO.socket("http://192.168.1.6:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {


            }

        }).on("message", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                final String jsonString = String.valueOf(args[0]);

                runOnUiThread(new Runnable() {
                    public void run() {

                        try {

                            JSONObject jObj = new JSONObject(jsonString);
                            JSONObject jValue = new JSONObject(jObj.getJSONObject("feeds").toString());
                            JSONArray item = new JSONArray(jValue.getJSONArray("item").toString());
                            ArrayList<String> list = new ArrayList<String>();
                            ArrayAdapter<String> adapter;
                            for (int i = 0; i < item.length(); i++) {
                                JSONArray title = item.getJSONObject(i).getJSONArray("title");
                                JSONArray blurb = item.getJSONObject(i).getJSONArray("blurb");
                                JSONArray picture = item.getJSONObject(i).getJSONArray("picture");
                                for (int x = 0; x < title.length(); x++) {
                                  //  Toast.makeText(getApplicationContext(), title.get(x).toString()+ " " +blurb.get(x).toString() + " " + picture.get(x).toString(),Toast.LENGTH_LONG).show();
                                   list.add(title.get(x).toString() + " " + blurb.get(x).toString() + " " + picture.get(x).toString());
                                }

                            }

                            listView(list);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }


                    }

                });
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
            }

        });

        socket.connect();
        // mSocket.on("message", onNewMessage);

    }

    public void listView(ArrayList<String> list){
        CustomList adapters = new CustomList(list, this);
        //adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_2, arrayList);
        lv.setAdapter(adapters);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
