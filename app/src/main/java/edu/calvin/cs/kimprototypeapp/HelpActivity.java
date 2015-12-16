package edu.calvin.cs.kimprototypeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/* @author Lydia Cupery, Beka Agava, Andrew Groenewold, Moses Mangunrahardja
 * HelpActivity provides users with scrollable help on how to use the application
 */

public class HelpActivity extends Activity {
    /*  onCreate initializes the view
    * @param savedInstanceState receives view from parent (in this case none)
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set defaul view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

       //create textview and enable it to scroll
        TextView helpTextView = (TextView) findViewById(R.id.helpText);
        helpTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    /* onCreateOptionsMenu
   * @param menu receives the menu
   * @return always returns true
   */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
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
        } else if (id == R.id.action_adminTools){
            startActivity(new Intent(this, AdminActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item); //added git
    }
}
