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

/*  @author Lydia Cupery, Beka Agava, Andrew Groenewold, Moses Mangunrahardja
*   MainActivity asks user for username & password and allows user to log in if field are correct
*/

public class MainActivity extends Activity implements View.OnClickListener{

    private Boolean isValidUsername = Boolean.FALSE;

    //declares screen components
    private Button loginButton;
    private TextView usernameField;
    private EditText usernameEnter, passwordEnter;
    private ImageView kimLogo;

   /*  onCreate initializes the view
   * @param savedInstanceState receives view from parent (in this case none)
   */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       //allows it to jump right to the next screen because the server is not working, COMMENT THESE TWO LINES OUT WHEN SERVER IS RUNNIG
        /**
        Intent home = new Intent(MainActivity.this, PortfolioActivity.class);
        startActivity(home);
         **/


        //provides internet permissions
        if (android.os.Build.VERSION.SDK_INT > 7) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            System.out.println("*** My thread is now configured to allow connection");
        }


        //initializes screen components
        loginButton=(Button) findViewById(R.id.loginbutton);
        usernameEnter=(EditText) findViewById(R.id.editText2);
        passwordEnter=(EditText) findViewById(R.id.editText);
        kimLogo = (ImageView) findViewById(R.id.kimLogo);
        kimLogo.setImageResource(R.mipmap.knight_investment_management);

        //create onClick listener for login button bringing to HomeActivity
        loginButton.setOnClickListener(this);
    }

    /** onClick responds to button by allowing user to go to HomeActivity if username and password is correct
     * @param arg0 receives view from which click originated
     */
    @Override
    public void onClick(View arg0) {
        //login button is no longer clickable
        loginButton.setClickable(false);

        //execute password check in AsyncTask
        new LongRunningGetIO().execute();
    }



   /* LongRunningGetIO is an AsyncTask that allows user to go on to next screen is username and password is correct
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

    /* onCreateOptionsMenu
     * @param menu receives the menu
     * @return always returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

}
