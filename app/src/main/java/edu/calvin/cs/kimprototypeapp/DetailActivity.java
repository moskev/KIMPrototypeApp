package edu.calvin.cs.kimprototypeapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/* @author Lydia Cupery, Beka Agava, Andrew Groenewold, Moses Mangunrahardja
 * DetailActivity retrieves details about th specific stock from the database and yahooAPI
 */

public class DetailActivity extends Activity {

    //global variables
    private TextView currentPriceField;
    String stockName;

    /*  onCreate initializes the view
    * @param savedInstanceState receives view from parent (in this case none)
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initializes view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }

        //looks at intent that called DetailActivity
        Intent intent = getIntent();
        //gets stockName from that intent
        stockName = intent.getStringExtra(Intent.EXTRA_TEXT);

        //when we receive info from the database, this will set the arrow according to code from the database
        ImageView arrowView = (ImageView) findViewById(R.id.arrowImage);


        //Declare textViews and set them to the correct place on the screen
        TextView lastTrade = (TextView) findViewById(R.id.CurrentPriceDisplay);
        TextView priceEarings = (TextView) findViewById(R.id.PETextDisplay);
        TextView companyName = (TextView) findViewById(R.id.stock_name);

        //pass the textboxes and stock name as parameters to Async Task
        MyTask myTask1 = new MyTask(lastTrade, priceEarings, arrowView, companyName);
        //execute Async task
        myTask1.execute(stockName);

       new LongRunningGetIO().execute();
    }


    /* the class that will get stock information via Yahoo Finance API
     */
    public class MyTask extends AsyncTask<String, Integer, ArrayList<String>> {
        //textviews in which data will appear
        private TextView lastTradeTextView, priceEarningsTextView, stockNameTextView;
        //imageView for the up and down arrow
        private ImageView arrowView;
        //ArrayList that stores strings to display in textViews
        private final ArrayList<String> valuesToBeReturned = new ArrayList<String>();

        /*  MyTask is the constructor, receives textViews and imageView
         *  @param lastTrade TextView to store lastTrade from parent
         *  @param priceEarning TextView to stoe priceEarnings from parent
         *  @param arrowImage ImageView to store arrowImage from parent
         *  @param companyName TextView to store companyName from parent
         */
        public MyTask(final TextView lastTrade, final TextView priceEarnings, final ImageView arrowImage, final TextView companyName){
            this.lastTradeTextView = lastTrade;
            this.priceEarningsTextView = priceEarnings;
            this.arrowView = arrowImage;
            this.stockNameTextView = companyName;

        }

        //do nothing before executing
        @Override
        protected void onPreExecute(){

        }

        /* doInBackground is the task to execute in the background, in this case query Yahoo FinanceAPI for specific data
         * @parameter params receives the ticker of the stock
         * @return ArrayList
         */
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            //receives stock name to do query on
            String ticker = params[0];

            //read xml data from yahoo finance
            final StringBuilder url = new StringBuilder("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20%28");  //creates intial url
            //constructs custom url be appending
            url.append("\"");
            url.append(ticker);
            url.append("\"");
            url.append("%29&env=store://datatables.org/alltableswithkeys");
            //convert url into string
            String urlString = (String) url.toString();


            //create variables to store url items in and intialize to 0/null
            Double lastTradePriceDouble = 0.0;
            Double priceEarningsDouble = 0.0;
            Double amountChangeDouble = 0.0;
            String upOrDown = "";
            String companyName = "";

            try {
                //convert the input stream from the particular url into a Document
               final InputStream stream = new URL(url.toString()).openStream();
               final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
               documentBuilderFactory.setIgnoringComments(true);
               final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
               final Document document = documentBuilder.parse(stream);
               document.getDocumentElement().normalize();

               //elementLeg reads from results
               final Element elementLeg = (Element) document.getElementsByTagName("results").item(0);

              //get particular results, in this case LastTradePrice, PERation, Change and Name
               final Element lastTradeElement = (Element) elementLeg.getElementsByTagName("LastTradePriceOnly").item(0);
               final Element priceEarningElement = (Element) elementLeg.getElementsByTagName("PERatio").item(0);
               final Element changeElement = (Element) elementLeg.getElementsByTagName("Change").item(0);
               final Element nameElement = (Element) elementLeg.getElementsByTagName("Name").item(0);

               //get the content of these elements and convert them to doubles
               String lastTradePrice = lastTradeElement.getTextContent();
               String priceEarning = priceEarningElement.getTextContent();
               String amountChanged = changeElement.getTextContent();
               companyName = nameElement.getTextContent();
               lastTradePriceDouble = Double.parseDouble(lastTradePrice);
               priceEarningsDouble = Double.parseDouble(priceEarning);
               amountChangeDouble = Double.parseDouble(amountChanged);

               //look to see if the stock went up or down
               if (amountChangeDouble>=0){
                   upOrDown = "up";
               }
               else {
                   upOrDown = "down";
               }
          }
           //catch malformedURL error
           catch (MalformedURLException e){
               Log.i("input stream error", e.getMessage());
           }
           //catch other errors
            catch (Exception e){
                Log.i("input stream error", e.getMessage() );
            }

            //put the values received from Yahoo API in the valuesToBeReturnedLIst
            valuesToBeReturned.add(lastTradePriceDouble.toString());
            valuesToBeReturned.add(priceEarningsDouble.toString());
            valuesToBeReturned.add(upOrDown); //add string saying whether it went up or down
            valuesToBeReturned.add(companyName);

            //return Array list with stock data
            return  valuesToBeReturned;
        }


        //for onProgressUpdate do nothing
        @Override
        protected void onProgressUpdate(Integer... params){

        }


        //after have executed
        @Override
        protected void onPostExecute(ArrayList<String> result){
            lastTradeTextView.setText(result.get(0)); //set text of first textbox to lastTradeValue
            priceEarningsTextView.setText(result.get(1)); //set text of 2nd textbox to PE ratio
            stockNameTextView.setText(result.get(3)); //set the name of the stock

            //look to see if the stock went up or down
            String upOrDown = result.get(2);

            //if it went up
            if (upOrDown.contains("up")){
                arrowView.setImageResource(R.mipmap.up_arrow); //se the arrow to the up arrow
            }
            else { //if it went down
                arrowView.setImageResource(R.mipmap.down_arrow); //set the arrow to the down arrow
            }
            super.onPostExecute(result);
        }

    }





    //Class to run a database query to get a stock's current price
    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
        int stockID = -1;

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
            String PEOPLE_URI = "http://10.0.2.2:9998/kimSQL/stocksIds";
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
            String[] stockNameList = results.split("\\n");
            for(int i=0; i<stockNameList.length; i+=2) {
                if (stockNameList[i].equals(stockName)) {
                    stockID = Integer.parseInt(stockNameList[i+1]);
                }
            }
            //Prints the label and price in the textbox
            currentPriceField.setText("Database Price: " + stockID);
            new InnerLongRunningGetIO().execute();
        }

        //Class to run a database query to get a stock's current price
        private class InnerLongRunningGetIO extends AsyncTask<Void, Void, String> {

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
                String PEOPLE_URI = "http://10.0.2.2:9998/kimSQL/stock/" + stockID;
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
                /*int stockID = -1;
                String[] stockNameList = results.split("\\n");
                for(int i=0; i<stockNameList.length; i+=2) {
                    if (stockNameList[i].equals(stockName)) {
                        stockID = Integer.parseInt(stockNameList[i+1]);
                    }
                }*/
                //Prints the label and price in the textbox
                currentPriceField.setText("Database Price: " + results);
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
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
        } else if (id == R.id.action_about){
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
        }

        return super.onOptionsItemSelected(item);
    }


    /* DetailFragment creates a View     *
     */
    public static class DetailFragment extends Fragment {


        //default constructor
        public DetailFragment() {
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

            //initialize rootView
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // The detail Activity called via intent.  Inspect the intent for stock data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                //adds stock name to intent
                String forecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            }

            //return rootView
            return rootView;
        }
    }
}
