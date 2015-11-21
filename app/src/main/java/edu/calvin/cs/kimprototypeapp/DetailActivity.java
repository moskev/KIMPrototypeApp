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

import java.math.BigDecimal;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/*
Detail activity will have information about specific stocks.
It will be accessible by clicking on a specific stock's name elsewhere in the program.
 */

public class DetailActivity extends Activity {

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

        //when receive info from database would set arrow accordin to code from database
        ImageView arrowView = (ImageView) findViewById(R.id.arrowImage);

       //make sure get stock name correctly
        Log.i("STOCK Name:", stockName);

        //does stock contain correct variable to make arrow green?
        boolean value = stockName.contains("BEKA");
        String truthValue;
        if (value == true){
            truthValue = "true";
        }
        else {
            truthValue = "false";
        }
        Log.i("TRUE/FALSE", truthValue);


        //if stock contains correct variable to make arrow green, make it green
        if (value) {
            arrowView.setImageResource(R.mipmap.up_arrow);
        }
        //otherwise, make it red
        else {
            arrowView.setImageResource(R.mipmap.down_arrow);
        }
        TextView stockText = (TextView) findViewById(R.id.stockText);
        MyTask myTask1 = new MyTask(stockText);
        myTask1.execute(stockName);
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
    }

    public class MyTask extends AsyncTask<String, Integer, String> {
        private TextView myTextview;
        public MyTask(final TextView textView){
            this.myTextview = textView;

        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(String... params) {
            String myString = params[0];

            int i=0;
            //publishProgress(i);

            return myString;
        }

        @Override
        protected void onProgressUpdate(Integer... values){

        }

        @Override
        protected void onPostExecute(String result){
            myTextview.setText(result);
            super.onPostExecute(result);
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
