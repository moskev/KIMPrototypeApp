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
        String stockName = intent.getStringExtra(Intent.EXTRA_TEXT);

        //when we receive info from the database, this will set the arrow according to code from the database
        ImageView arrowView = (ImageView) findViewById(R.id.arrowImage);

       //makes sure get stock name correctly
        Log.i("STOCK Name:", stockName);

        //Checks if the stock contains the correct variable to make the arrow green
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
        //Sets up a textbox and makes an AsyncTask to fill it with the stock name
        //This is part of our efforts to use the YahooFinance API
        TextView lastTrade = (TextView) findViewById(R.id.stockText);
        TextView priceEarings = (TextView) findViewById(R.id.currentPriceField);
        MyTask myTask1 = new MyTask(lastTrade, priceEarings); //send view to initialize w/ should add a 2nd view to this
        myTask1.execute(stockName);
        //These next comment blocks are more attempts to use the YahooFinance API
        /*try {
            Stock stock = YahooFinance.get(stockName);

            BigDecimal price = stock.getQuote().getPrice();
            Log.i("Beka",price.toString());
            BigDecimal change = stock.getQuote().getChangeInPercent();
            BigDecimal peg = stock.getStats().getPeg();
            BigDecimal dividend = stock.getDividend().getAnnualYieldPercent();
            //stock.print();

        } catch (Exception e){
            Log.i("Andrew:",e.toString());
        };
        //Moses helped. #pairProgramming
        /*try {
            Stock stock = YahooFinance.get(stockName);
            BigDecimal price = stock.getQuote(true).getPrice();
            System.out.println(price);
        } catch (Exception e) {}*/
        //Creates a text box and runs an Async task to fill it with the current price of a stock
        currentPriceField=(TextView) findViewById(R.id.currentPriceField);
        new LongRunningGetIO().execute();
    }


    //Beginning of the class that will get stock information via Yahoo Finance API
    public class MyTask extends AsyncTask<String, Integer, ArrayList<String>> {
        private TextView lastTradeTextView, priceEarningsTextView;
        private final ArrayList<String> valuesToBeReturned = new ArrayList<String>();

        public MyTask(final TextView lastTrade, final TextView priceEarnings){
            this.lastTradeTextView = lastTrade;
            this.priceEarningsTextView = priceEarnings;

        }

        @Override
        protected void onPreExecute(){

        }



        @Override
        protected ArrayList<String> doInBackground(String... params) {
            String myString = params[0];


            int i=0;
            //publishProgress(i);

            //return "Asnyc task currently returns THIS data";

            String ticker = "GOOG";

            //read xml data from yahoo finance
            final StringBuilder url = new StringBuilder("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20%28");
            url.append("\"");
            url.append(ticker);
            url.append("\"");
            url.append("%29&env=store://datatables.org/alltableswithkeys");
            String urlString = (String) url.toString();
            Log.i("URL", "Stock url is" + urlString);



            Double lastTradePriceDouble = 0.0;
            Double priceEarningsDouble = 0.0;
           try {
               final InputStream stream = new URL(url.toString()).openStream();
               final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
               documentBuilderFactory.setIgnoringComments(true);
               final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
               final Document document = documentBuilder.parse(stream);
               document.getDocumentElement().normalize();
               final Element elementLeg = (Element) document.getElementsByTagName("results").item(0);
             //  resultElement = elementLeg.getTextContent();
               final Element lastTradeElement = (Element) elementLeg.getElementsByTagName("LastTradePriceOnly").item(0);
               final Element priceEarningElement = (Element) elementLeg.getElementsByTagName("PERatio").item(0);
               String lastTradePrice = lastTradeElement.getTextContent();
               String priceEarning = priceEarningElement.getTextContent();
               lastTradePriceDouble = Double.parseDouble(lastTradePrice);
               priceEarningsDouble = Double.parseDouble(priceEarning);



           }
           catch (MalformedURLException e){
               Log.i("input stream error", e.getMessage());
           }
            catch (Exception e){
                Log.i("input stream error", e.getMessage() );
            }




            //put the values to be changed into a list
            valuesToBeReturned.add(lastTradePriceDouble.toString());
            valuesToBeReturned.add(priceEarningsDouble.toString());




            return  valuesToBeReturned;


        }

        @Override
        protected void onProgressUpdate(Integer... params){

        }


        @Override
        protected void onPostExecute(ArrayList<String> result){
            //Sets the stock name to the textbox
            lastTradeTextView.setText(result.get(0));
            priceEarningsTextView.setText(result.get(1));
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
