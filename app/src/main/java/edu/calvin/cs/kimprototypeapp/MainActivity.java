package edu.calvin.cs.kimprototypeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;

/***
 * MainActivity is the first activity to appear on the screen.  It prompts the user for a username and password.
 * Need to know: read in username & password
 */
public class MainActivity extends Activity implements View.OnClickListener{



    private Boolean isValidUsername = Boolean.FALSE;

    //create loginButton and text fields
    private Button loginButton;
    private TextView usernameField;
    private EditText usernameEnter, passwordEnter;

    //get ImageView to add kimLogo in
    private ImageView kimLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

       //allows it to jump right to the next screen because the server is not working, COMMENT THESE TWO LINES OUT WHEN SERVER IS RUNNIG
        /**
        Intent home = new Intent(MainActivity.this, PortfolioActivity.class);
        startActivity(home);
         **/

        super.onCreate(savedInstanceState);

        //needed for our app to talk to the server

        if (android.os.Build.VERSION.SDK_INT > 7) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            System.out.println("*** My thread is now configured to allow connection");
        }

        setContentView(R.layout.activity_main);
        //assign values to buttons, images, and fields
        loginButton=(Button) findViewById(R.id.loginbutton);
        //usernameField=(TextView) findViewById(R.id.editText2);
        usernameEnter=(EditText) findViewById(R.id.editText2);
        passwordEnter=(EditText) findViewById(R.id.editText);
        kimLogo = (ImageView) findViewById(R.id.kimLogo);
        kimLogo.setImageResource(R.mipmap.knight_investment_management);

        //create onClick listener for login button bringing to HomeActivity
        loginButton.setOnClickListener(this);
        
    }

    //onClick listener for the login button
    @Override
    public void onClick(View arg0) {
        loginButton.setClickable(false);
        //starts a server query to verify the username and password

        //am executing this because server is NOT running
        //Intent home = new Intent(MainActivity.this, PortfolioActivity.class);
        //startActivity(home);


        //If SERVER running would execute this:
        new LongRunningGetIO().execute();


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
            String PEOPLE_URI = "http://10.0.2.2:9998/kimSQL/accounts";
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
            //Checks if the username is the first username in the database
            //I was having trouble with string comparisons, so
            //the ability to use other usernames will be added later
            String testUsername = usernameEnter.getText().toString();
            String testPassword = passwordEnter.getText().toString();
            //usernameField.setText(results);
            //if(results.contains(testUsername)) {
            String[] usernameList = results.split("\\n");
            for(int i=0; i<usernameList.length; i+=2) {
                if (usernameList[i].equals(testUsername)) {
                    if(usernameList[i+1].equals(testPassword)) {
                        isValidUsername = true;
                    }
                }
            }
            //create and start new Intent
            if (isValidUsername) {
                Intent home = new Intent(MainActivity.this, PortfolioActivity.class);
                startActivity(home);
            }
            /*else{
             //print out toast if is INVALID username
                //however, since server does not function on laptop, in this case also allow user to continue
                Intent home = new Intent(MainActivity.this, PortfolioActivity.class);
                startActivity(home);
            }*/
            isValidUsername = false;
            loginButton.setClickable(true);
            usernameEnter.setText("");
            passwordEnter.setText("");
        }

    }

    @Override
    protected void onResume() {
        usernameEnter.setText("");
        passwordEnter.setText("");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }

        return super.onOptionsItemSelected(item); //added git
    }

}
