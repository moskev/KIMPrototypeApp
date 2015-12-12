package edu.calvin.cs.kimprototypeapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * HomeActivity displays the stocks in a list format
 */
public class HomeActivity extends Activity {
    String dbStocks;
    Boolean finished;
    Bundle instanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        instanceState = savedInstanceState;
        //If SERVER running would execute this:
        finished = false;
        new LongRunningGetIO().execute();




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }


//This class is mostly taken from lab09
//It starts a task to query the database
private class LongRunningGetIO extends AsyncTask<Void, Void, String> {

    /**
     * This method extracts text from the HTTP response entity.
     *
     * @return string, the username from the server
     * @throws IllegalStateException
     * @throws IOException
     */
    String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
        InputStream in = entity.getContent();
        StringBuilder out = new StringBuilder();
        int n = 1;
        while (n > 0) {
            byte[] b = new byte[4096];
            n = in.read(b);
            if (n > 0) out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    /**
     * This method issues the HTTP GET request.
     *
     * @param params none
     * @return text, the result of the query
     */
    @Override
    protected String doInBackground(Void... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
            /*
      This inner class sends an HTTP requests to the Monopoly RESTful service API. It uses an
      asynchronous task to take the slow I/O off the main interface thread.
      <p/>
      It uses 10.0.2.2 to access localhost, see
      http://developer.android.com/tools/devices/emulator.html#networkaddresses
      <p/>
      It retains the deprecated classes in order to remain backwards compatible for Android 4, see
      http://stackoverflow.com/questions/29150184/httpentity-is-deprecated-on-android-now-whats-the-alternative
     */
        String PEOPLE_URI = "http://10.0.2.2:9998/kimSQL/stocks";
        HttpGet httpGet = new HttpGet(PEOPLE_URI);
        String text;
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            text = getASCIIContentFromEntity(entity);
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
        return text;
    }

    /**
     * The method takes the results of the request, when they arrive, and updates the interface.
     *
     * @param results (of the query)
     */
    protected void onPostExecute(String results) {
        dbStocks = results;
        finished = true;


        Bundle bundle = new Bundle();
        bundle.putString("STOCKS", dbStocks);

        PlaceholderFragment fragment = new PlaceholderFragment();
        fragment.setArguments(bundle);


        if (instanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment) //starts placeholder fragment
                    .commit();


        }
    }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }




    /*
    This function opens the appropriate activity when a menu button is pushed.
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            startActivity(new Intent(this, PortfolioActivity.class));
            return true;
        } else if (id == R.id.action_about){
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (id == R.id.action_training){
            startActivity(new Intent(this, TrainingActivity.class));
            return true;
        } else if (id == R.id.action_stockPitch){
            startActivity(new Intent(this, StockPitchActivity.class));
            return true;
        } else if (id == R.id.action_logout){
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item); //added git
    }




    /**
 * A placeholder fragment containing a simple view.
 */
public static class PlaceholderFragment extends Fragment {



    ArrayAdapter<String> mStockAdapter; //create the ArrayAdapter, responsible for populating ListView

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String stockString="";



       ///get string from server passed to Placeholder Fragment as an argument
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            stockString = bundle.getString("STOCKS", null); //the 2nd param is the default, i.e. if it cannot
            //read in from the database
        }


        //parse the string into an array of string
        String[] stockData = stockString.split("\\n");

        // Create dummy stock data for ListView
       // String[] data = {
        //        "AAPL", "CSCO", "EA", "FB", "GOOG", "IBM", "JPM", "MSFT", "ORCL", "PM", "SBUX", "TSLA", "UA", "TLU", "XOM", "ZNGA", "BEKA'S STOCK!!!", "Chicken Stock"
//        };

        //takes array of data and stores it as a list
        List<String> stocks = new ArrayList<String>(Arrays.asList(stockData));

        //Adapter takes data from the source and uses it to populate ListView
        //would it be possible to make each of these a diff. color depending on certain charactreisitcs?
        mStockAdapter = new ArrayAdapter<String>(
                getActivity(), //fragment's parent activity xx
                R.layout.list_item_stock, //name of layout ID
                R.id.list_item_stock_textview, //ID of textview in that layout to populate
                stocks //the data to populate it with


            );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_stocks);

        listView.setAdapter(mStockAdapter); //here is where the adapter is being set, could hypothetically set to custom adapter


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

             @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String stockName = mStockAdapter.getItem(position);
                //Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, stockName);
                startActivity(intent);


            }
        });

        return rootView;
    }
}
}