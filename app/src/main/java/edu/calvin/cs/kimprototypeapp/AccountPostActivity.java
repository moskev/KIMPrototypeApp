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
 * AdminActivity will allow administrators to create accounts, this function is not yet implemented
 */


public class AccountPostActivity extends Activity {

    private Button loginButton;
    private EditText usernameEnter, passwordEnter;

    /*  onCreate initializes the view
  * @param savedInstanceState receives view from parent (in this case none)
  */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_post);
        loginButton=(Button) findViewById(R.id.loginbutton);
        usernameEnter=(EditText) findViewById(R.id.editText2);
        passwordEnter=(EditText) findViewById(R.id.editText);

      /* onClick listener for individualStock button bringing to HomeActivity
         * param onClickListener initialize and declare new onClickListener in the parameter
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //execute password check in AsyncTask
                new LongRunningPostIO().execute();

            }
        });
    }

    //URI for the POST method for adding new climbs to the database
    private static String NEW_ACCOUNT_URI = "http://153.106.116.65:9998/kimSQL/accountPost";

    /**
     * LongRunningGetIO class contains the data necessary in order to do an IO task (GET, POST...).
     * Adapted from Lab09 code.
     * Adapted from TeamF
     */
    private class LongRunningPostIO extends AsyncTask<Void, Void, String> {

        String testUsername = usernameEnter.getText().toString();
        String testPassword = passwordEnter.getText().toString();

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
                String input = testUsername + ":" + testPassword;
                StringEntity data = new StringEntity(input);  //Create a StringEntity object to hold the input data

                //Set the content type
                data.setContentType("text/plain");

                //Set the entity of the POST method
                httpPost.setEntity(data);

                // Execute HTTP Post Request
                httpClient.execute(httpPost, localContext);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
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
