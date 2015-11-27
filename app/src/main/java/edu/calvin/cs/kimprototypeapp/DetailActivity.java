package edu.calvin.cs.kimprototypeapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/*
Detail activity will have information about specific stocks.
It will be accessible by clicking on a specific stock's name elsewhere in the program.
 */

public class DetailActivity extends Activity {
    private TextView currentPriceField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }


        Intent intent = getIntent();

        //get stockName from the intent
        String stockName = intent.getStringExtra(Intent.EXTRA_TEXT);

        //when we receive info from the database, this will set the arrow according to code from the database
        ImageView arrowView = (ImageView) findViewById(R.id.arrowImage);

       //makes sure get stock name correctly
        Log.i("STOCK Name:", stockName);

        //Checks if the stock contains the correct variable to make the arrow green - should change this to reflect whether stock went up or down
        boolean value = stockName.contains("BEKA");
        String truthValue;
        if (value == true){
            truthValue = "true";
        }
        else {
            truthValue = "false";
        }
        Log.i("TRUE/FALSE", truthValue);


        //if the stock contains the correct variable to make the arrow green, it makes it green
        if (value) {
            arrowView.setImageResource(R.mipmap.up_arrow);
        }
        //otherwise, it makes it red
        else {
            arrowView.setImageResource(R.mipmap.down_arrow);
        }


        //Sets up a textboxes and makes an AsyncTask to fill it with the stock name
        TextView lastTrade = (TextView) findViewById(R.id.stockText);
        TextView priceEarings = (TextView) findViewById(R.id.currentPriceField);
        //pass the textboxes and stock name as parameters to Async Task
        MyTask myTask1 = new MyTask(lastTrade, priceEarings); //send view to initialize w/ should add a 2nd view to this
        myTask1.execute(stockName); //executes Async task


       //used when connecting to the server
        new LongRunningGetIO().execute();
    }


    //Beginning of the class that will get stock information via Yahoo Finance API
    public class MyTask extends AsyncTask<String, Integer, ArrayList<String>> {
        //textviews in which data will appear
        private TextView lastTradeTextView, priceEarningsTextView;
        //ArrayList that stores strings to display in textViews
        private final ArrayList<String> valuesToBeReturned = new ArrayList<String>();
        //stores name of stock passed to Async Task
        private String passedStockName = "";

        //initialize method for Async task, receives 2 textviews
        public MyTask(final TextView lastTrade, final TextView priceEarnings){
            this.lastTradeTextView = lastTrade;
            this.priceEarningsTextView = priceEarnings;

        }

        //before executed - do nothing
        @Override
        protected void onPreExecute(){

        }


        //doInBackground is the task to do in the background, receives stock name as a param
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            String ticker = params[0]; //receives stock name to do query on



            //read xml data from yahoo finance
            final StringBuilder url = new StringBuilder("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20%28");  //creates intial url
            //constructs custom url be appending
            url.append("\"");
            url.append(ticker);
            url.append("\"");
            url.append("%29&env=store://datatables.org/alltableswithkeys");
            String urlString = (String) url.toString();
            //Log url
            Log.i("URL", "Stock url is" + urlString);


            //create variables to store url items in
            Double lastTradePriceDouble = 0.0;
            Double priceEarningsDouble = 0.0;
           try {
               final InputStream stream = new URL(url.toString()).openStream();
               final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
               documentBuilderFactory.setIgnoringComments(true);
               final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
               final Document document = documentBuilder.parse(stream);
               document.getDocumentElement().normalize();
               //elementLeg reads from results
               final Element elementLeg = (Element) document.getElementsByTagName("results").item(0);
              //get particular results, in this case LastTradePrice and PERation
               final Element lastTradeElement = (Element) elementLeg.getElementsByTagName("LastTradePriceOnly").item(0);
               final Element priceEarningElement = (Element) elementLeg.getElementsByTagName("PERatio").item(0);
               //get the content of these elements and convert them to doubles
               String lastTradePrice = lastTradeElement.getTextContent();
               String priceEarning = priceEarningElement.getTextContent();
               lastTradePriceDouble = Double.parseDouble(lastTradePrice);
               priceEarningsDouble = Double.parseDouble(priceEarning);



           }
           //catch malformedURL error
           catch (MalformedURLException e){
               Log.i("input stream error", e.getMessage());
           }
           //catch other errors
            catch (Exception e){
                Log.i("input stream error", e.getMessage() );
            }


            //put the values to be changed into a list
            valuesToBeReturned.add(lastTradePriceDouble.toString());
            valuesToBeReturned.add(priceEarningsDouble.toString());

            //return Array list with stock data
            return  valuesToBeReturned;
        }


        //for onProgressUpdate do nothing
        @Override
        protected void onProgressUpdate(Integer... params){

        }


        @Override
        protected void onPostExecute(ArrayList<String> result){
            //Sets the stock name to the textbox
            lastTradeTextView.setText(result.get(0)); //set text of first textbox to lastTradeValue
            priceEarningsTextView.setText(result.get(1)); //set text of 2nd textbox to PE ratio
            super.onPostExecute(result);
        }

    }





    //Class to run a database query to get a stock's current price
    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {

        /**
         * This method extracts text from the HTTP response entity.
         *
         * @return string, the current price
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
         * @param params and stuff
         * @return text, the price
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
            //I was having string comparison problems so currently this only returns the price of the stock with 270 shares owned
            String PEOPLE_URI = "http://10.0.2.2:9998/kimSQL/stock/270";
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
         * @param results, the price
         */
        protected void onPostExecute(String results) {
            //Prints the label and price in the textbox
            currentPriceField.setText("Current Price: " + results);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
    public static class DetailFragment extends Fragment {


        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                //adds stock name to intent
                String forecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView) rootView.findViewById(R.id.stock_name))
                        .setText(forecastStr);
            }




            return rootView;
        }
    }
}
