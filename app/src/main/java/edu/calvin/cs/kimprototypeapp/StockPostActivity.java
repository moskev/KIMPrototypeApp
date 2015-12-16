package edu.calvin.cs.kimprototypeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/* @author Lydia Cupery, Beka Agava, Andrew Groenewold, Moses Mangunrahardja
 * StockPostActivity will allow administrators to post new stocks
 */


public class StockPostActivity extends Activity {

    private Button postButton;
    private EditText tickerEnter, sectorEnter, buyPriceEnter, sellPriceEnter, sharesOwnedEnter;

    /*  onCreate initializes the view
  * @param savedInstanceState receives view from parent (in this case none)
  */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_post);
        postButton=(Button) findViewById(R.id.postbutton);
        tickerEnter=(EditText) findViewById(R.id.editText);
        sectorEnter=(EditText) findViewById(R.id.editText4);
        buyPriceEnter=(EditText) findViewById(R.id.editText5);
        sellPriceEnter=(EditText) findViewById(R.id.editText2);
        sharesOwnedEnter=(EditText) findViewById(R.id.editText3);

      /* onClick listener for individualStock button bringing to HomeActivity
         * param onClickListener initialize and declare new onClickListener in the parameter
         */
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //execute password check in AsyncTask
                new LongRunningPostIO().execute();

            }
        });
    }

    //URI for the POST method for adding new stocks to the database
    private static String NEW_ACCOUNT_URI = "http://153.106.116.65:9998/kimSQL/stockPost";

    /**
     * LongRunningPostIO class contains the data necessary in order to POST a new stock.
     * Adapted from Lab09 code.
     * Adapted from TeamF
     */
    private class LongRunningPostIO extends AsyncTask<Void, Void, String> {

        String testTicker = tickerEnter.getText().toString();
        String testSector = sectorEnter.getText().toString();
        String testBuy = buyPriceEnter.getText().toString();
        String testSell = sellPriceEnter.getText().toString();
        String testShares = sharesOwnedEnter.getText().toString();

        /**
         * This method issues the HTTP POST request.
         * Adapted from Lab09 code.
         */
        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();  //Create the HTTP Client
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(NEW_ACCOUNT_URI);  //Create the POST

            try {
                //Get the data from the user
                String input = testTicker + ":" + testSector + ":" + testBuy + ":" + testSell + ":" + testShares;
                StringEntity data = new StringEntity(input);  //Create a StringEntity object to hold the input data

                //Set the content type
                data.setContentType("text/plain");

                //Set the entity of the POST method
                httpPost.setEntity(data);

                // Execute HTTP Post Request
                httpClient.execute(httpPost, localContext);

            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return "YAY";
        }

    }

    /* onCreateOptionsMenu
  * @param menu receives the menu
  * @return always returns true
  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin, menu);
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
}
