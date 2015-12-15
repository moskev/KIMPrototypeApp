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

/* @author Lydia Cupery, Beka Agava, Andrew Groenewold, Moses Mangunrahardja
 * HomeActivity has a list of the current stocks which, when clicked on, lead to DetailActivity specific to that stock
 */
public class HomeActivity extends Activity {

    //declare public variables
    String dbStocks;
    Bundle instanceState;

    /*  onCreate initializes the view
    * @param savedInstanceState receives view from parent (in this case none)
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initialize instanceState to view passed from parent
        instanceState = savedInstanceState;

        new LongRunningGetIO().execute();

        //initialize view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }


    /* LongRunningGetIO receives the stock list from the server and sends it to a FragmetnPlaceholder to print out in a list format
     */
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
            String PEOPLE_URI = "http://10.0.0.2:9998/kimSQL/stocks";
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
            //put the string list of stock into global variable
            dbStocks = results;

            //create a Bundle to store string of stocks in
            Bundle bundle = new Bundle();
            bundle.putString("STOCKS", dbStocks);

            //create a fragment of type PlaceholderFragment and pass budle into it
            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.setArguments(bundle);


            if (instanceState == null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
            }
        }
    }

    /* onCreateOptionsMenu
    * @param menu receives the menu
    * @return always returns true
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }


    /* Opens the appropriate activity when a menu button is pushed.
     * @param item the specific item that was pushed
     * @return always returns true
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
        } else if (id == R.id.action_help){
            startActivity(new Intent(this, HelpActivity.class));
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
        } else if (id == R.id.action_stocks){
            startActivity(new Intent(this, HomeActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    /* PlaceholderFragment receives stock list via a bundle and displays those lists as a ListView
     */
    public static class PlaceholderFragment extends Fragment {

        ArrayAdapter<String> mStockAdapter;

        /* PlaceholderFragment is the default constructor
         */
        public PlaceholderFragment() {
        }

        /* onCreateView creates the view w/ the list on it
         * @return View the view with the stock list on it
         * @param inflater is the xml layout
         * @param container is the the ViewGroup
         * @param savedInsanceState is the Bundle that stores View from parent
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            //declare & initialize stockString which will store the stocks from the server
            String stockString="";

            ///get string from server passed to Placeholder Fragment as an argument
            Bundle bundle = this.getArguments();

            if (bundle != null) {
                stockString = bundle.getString("STOCKS", null); //the 2nd param is the default, i.e. if it cannot get the stocks from the server
            }


            //parse the string of stocks into an array of string
            String[] stockData = stockString.split("\\n");

            //takes the array stockData and stores it as a list
            List<String> stocks = new ArrayList<String>(Arrays.asList(stockData));

            //Adapter takes data from the source and uses it to populate ListView
            mStockAdapter = new ArrayAdapter<String>(
                    getActivity(), //fragment's parent activity
                    R.layout.list_item_stock, //name of layout ID
                    R.id.list_item_stock_textview, //ID of textview in that layout to populate
                    stocks //the data to populate it with
            );

            //initialize view
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Get a reference to the ListView, and attach this adapter to it.
            ListView listView = (ListView) rootView.findViewById(R.id.listview_stocks);

            //set listView's adapter as custom created adapter
            listView.setAdapter(mStockAdapter);

            /* setOnClickListener that responds when user clicks on a specific stock
             * @param create a new onItemClickListener as the parameter
             */
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                /* onItemClick receives position of current item in list and loads activity for that specific item
                 * @param adapterView the AdapterView format list is loaded from
                 * @param view from which user clicked
                 * @param position is the position in the list
                 * @param l use default value
                 */
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String stockName = mStockAdapter.getItem(position);
                    Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, stockName);
                    startActivity(intent);
                }
            });

            //return the rootView with the list on it
            return rootView;
        }
    }
}