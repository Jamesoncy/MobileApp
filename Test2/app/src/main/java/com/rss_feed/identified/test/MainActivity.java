package com.rss_feed.identified.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;
import com.koushikdutta.ion.Ion;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {

    public ListView lv;
    private Socket socket;
    public TelephonyManager TM;
    String insUrl = "http://192.168.1.7:80/test/insert.php";
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.listView);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
      //  View load = (View) findViewById(R.id.loadMore);
        try {
            socket = IO.socket("http://192.168.1.7:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            this.listener();
        } catch (Exception e) {
            e.printStackTrace();
        }
        TM  =  (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        socket.connect();

        loadMore(null);

        likeEventReceiver();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String title = ((TextView) arg1.findViewById(R.id.list_item_string)).getText().toString();
                String blurb = ((TextView) arg1.findViewById(R.id.blurb)).getText().toString();
                String picture = ((TextView) arg1.findViewById(R.id.picture)).getText().toString();
                String link = ((TextView) arg1.findViewById(R.id.link)).getText().toString();
                Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();
                View checkBoxView = View.inflate(MainActivity.this, R.layout.dialogbox, null);
                ((TextView) checkBoxView.findViewById(R.id.title)).setText(title);
                ((TextView) checkBoxView.findViewById(R.id.blurb)).setText(blurb);

                TextView linkSet = (TextView) checkBoxView.findViewById(R.id.link);
                linkSet.setClickable(true);
                linkSet.setMovementMethod(LinkMovementMethod.getInstance());
                String text = "<a href= '" + link + "'> " + link + " </a>";
                linkSet.setText(Html.fromHtml(text));
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                Ion.with(getApplicationContext())
                        .load(picture)
                        .withBitmap()
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                                // .animateLoad(spinAnimation)
                                // .animateIn(fadeInAnimation)
                        .intoImageView((ImageView) checkBoxView.findViewById(R.id.imageView));
                builder.setTitle("Details");
                builder.setView(checkBoxView)
                        .setCancelable(false)
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });



    }





    public void loadMore (View v)
    {
        socket.emit("request", String.valueOf(lv.getChildCount()), this.TM.getDeviceId().toString());
    }





    public void listener() throws  Exception{

        socket.on("request", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                final String json = args[0].toString();

                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            if (json != "{}") {
                                JSONObject jObj = new JSONObject(json);
                                if (jObj.has("feeds")) {
                                    JSONObject jValue = new JSONObject(jObj.getJSONObject("feeds").toString());

                                    JSONArray item = new JSONArray(jValue.getJSONArray("item").toString());

                                    ArrayList<String> list = new ArrayList<String>();
                                    ArrayAdapter<String> adapter;
                                    for (int i = 0; i < item.length(); i++) {
                                        JSONArray title = item.getJSONObject(i).getJSONArray("title");
                                        JSONArray blurb = item.getJSONObject(i).getJSONArray("blurb");
                                        JSONArray link = item.getJSONObject(i).getJSONArray("link");
                                        JSONArray picture = item.getJSONObject(i).getJSONArray("picture");
                                        JSONArray number = item.getJSONObject(i).getJSONArray("number");
                                        JSONArray action = item.getJSONObject(i).getJSONArray("action");
                                        for (int x = 0; x < title.length(); x++) {
                                            //   Toast.makeText(getApplicationContext(), title.get(x).toString()+ " " +blurb.get(x).toString(),Toast.LENGTH_LONG).show();
                                            list.add(title.get(x).toString() + "|" + link.get(x).toString() + "|" + blurb.get(x).toString() + "|" + picture.get(x).toString() + "|" + item.getJSONObject(i).getJSONArray("id").get(0).toString() + "|" + number.get(x).toString() + "|" + action.get(x).toString());
                                        }

                                    }

                                    listView(list);

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }


                });

            }
        });

    }

    public void likeEvent(View v)
    {
        Button btn = (Button)v;
        final String  action = btn.getText().toString();
        final String tag = v.getTag().toString();
        socket.emit("likeEvent", TM.getDeviceId().toString(), tag, action);
        if (action == "Like")
            btn.setText("Liked");

        else
            btn.setText("Like");


    }

    public void likeEventReceiver(){

        socket.on("likeEvent", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                final String like = args[0].toString();

                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            JSONObject jObj = new JSONObject(like);
                            String item =  jObj.getString("item_id");
                            int res = jObj.getInt("res");
                            int count = lv.getCount();
                            editCount(lv, count, item, res);
                        }
                        catch (Exception e){
                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }


                });

            }
        });
    }


    public void editCount(ListView lv, int count,String item_id, int res){
       for(int i = 0; i < count ; i++){

            Button tag  = (Button)lv.getChildAt(i).findViewById(R.id.like);


           if (tag.getTag().toString().equals(item_id)){
                        Button setText = (Button)lv.getChildAt(i).findViewById(R.id.numberText);
                        String txt = setText.getText().toString();
                        int newVal = Integer.parseInt(txt) + res;
                         setText.setText(String.valueOf(newVal));

           }


        }
        reOrder(lv,count);
    }


    public void listEvent(View v)
    {

        Toast.makeText(getApplicationContext(),String.valueOf(this.TM.getDeviceId()),Toast.LENGTH_SHORT).show();

    }



    public void reOrder(ListView lv,int count){

        for(int i = 0; i < count - 1 ; i++){
            for(int y = i+ 1; y < count  ; y++) {
                Button less = (Button) lv.getChildAt(i).findViewById(R.id.numberText);
                Button great = (Button) lv.getChildAt(y).findViewById(R.id.numberText);
                TextView title;
                TextView blurb;
                TextView link;
                TextView picture;
                String temp_title;
                String temp_blurb;
                String temp_link;
                String temp_picture;
                String link_counts;
                String button_tag;
                String button_text;

                    if(Integer.parseInt(less.getText().toString()) < Integer.parseInt(great.getText().toString()) ){

                            temp_title = ((TextView)lv.getChildAt(i).findViewById(R.id.list_item_string)).getText().toString();
                            temp_link = ((TextView)lv.getChildAt(i).findViewById(R.id.link)).getText().toString();
                            temp_blurb = ((TextView)lv.getChildAt(i).findViewById(R.id.blurb)).getText().toString();
                            temp_picture = ((TextView)lv.getChildAt(i).findViewById(R.id.picture)).getText().toString();
                            link_counts = ((Button)lv.getChildAt(i).findViewById(R.id.numberText)).getText().toString();
                        button_tag = ((Button)lv.getChildAt(i).findViewById(R.id.like)).getTag().toString();
                        button_text = ((Button)lv.getChildAt(i).findViewById(R.id.like)).getText().toString();
                            ((TextView)lv.getChildAt(i).findViewById(R.id.list_item_string)).setText(((TextView)lv.getChildAt(y).findViewById(R.id.list_item_string)).getText().toString());
                            ((TextView)lv.getChildAt(i).findViewById(R.id.link)).setText(((TextView)lv.getChildAt(y).findViewById(R.id.link)).getText().toString());
                            ((TextView)lv.getChildAt(i).findViewById(R.id.blurb)).setText(((TextView)lv.getChildAt(y).findViewById(R.id.blurb)).getText().toString());
                            ((TextView)lv.getChildAt(i).findViewById(R.id.picture)).setText(((TextView)lv.getChildAt(y).findViewById(R.id.picture)).getText().toString());
                            ((Button)lv.getChildAt(i).findViewById(R.id.like)).setText(((Button) lv.getChildAt(y).findViewById(R.id.like)).getText().toString());
                            ((Button)lv.getChildAt(i).findViewById(R.id.like)).setTag(((Button) lv.getChildAt(y).findViewById(R.id.like)).getTag().toString());
                            ((Button)lv.getChildAt(i).findViewById(R.id.numberText)).setText(((Button) lv.getChildAt(y).findViewById(R.id.numberText)).getText().toString());

                                    ((TextView)lv.getChildAt(y).findViewById(R.id.list_item_string)).setText(temp_title);
                                    ((TextView)lv.getChildAt(y).findViewById(R.id.link)).setText(temp_link);
                                    ((TextView)lv.getChildAt(y).findViewById(R.id.blurb)).setText(temp_blurb);
                                    ((TextView)lv.getChildAt(y).findViewById(R.id.picture)).setText(temp_picture);
                                    ((Button)lv.getChildAt(y).findViewById(R.id.like)).setText(button_text);
                                    ((Button)lv.getChildAt(y).findViewById(R.id.like)).setTag(button_tag);
                                    ((Button)lv.getChildAt(y).findViewById(R.id.numberText)).setText(link_counts);



                    }

            }

        }


    }

    public void listView(ArrayList<String> list){
       CustomList adapters = new CustomList(list, this);

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
