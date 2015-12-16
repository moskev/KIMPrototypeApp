package edu.calvin.cs.kimprototypeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/* @author Lydia Cupery, Beka Agava, Andrew Groenewold, Moses Mangunrahardja
 * This activity has information about KIM's portfolio as a whole.
 * It is accessible through the home menu button and the login button and lead directly to HomeActivity.
 */

public class AdminActivity extends Activity {

    //declares screen components
    private Button individualStockButton;
    private Button stockPitchButton;
    private Button trainingGuideButton;
    private ImageView kimLogo;



    /*  onCreate initializes the view
    * @param savedInstanceState receives view from parent (in this case none)
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initialize view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        //initializes screen components
        individualStockButton =(Button)  findViewById(R.id.individualStockButton);
        stockPitchButton =(Button)  findViewById(R.id.stockPitch);
        trainingGuideButton =(Button)  findViewById(R.id.trainingGuide);
        kimLogo = (ImageView) findViewById(R.id.kimLogo);
        kimLogo.setImageResource(R.mipmap.knight_investment_management);

        /* onClick listener for individualStock button bringing to HomeActivity
         * param onClickListener initialize and declare new onClickListener in the parameter
         */
        individualStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create and start new Intent
                Intent home = new Intent(AdminActivity.this, AccountPostActivity.class);
                startActivity(home);
            }
        });

        /* onClick listener for stockPitch button bringing to HomeActivity
         * param onClickListener initialize and declare new onClickListener in the parameter
         */
        stockPitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create and start new Intent
                Intent home = new Intent(AdminActivity.this, StockPitchActivity.class);
                startActivity(home);
            }
        });

        /* onClick listener for stockPitch button bringing to HomeActivity
         * param onClickListener initialize and declare new onClickListener in the parameter
         */
        trainingGuideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create and start new Intent
                Intent home = new Intent(AdminActivity.this, TrainingActivity.class);
                startActivity(home);
            }
        });
    }


    /* onCreateOptionsMenu
   * @param menu receives the menu
   * @return always returns true
   */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_portfolio, menu);
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
        } else if (id == R.id.action_stocks){
            startActivity(new Intent(this, HomeActivity.class));
            return true;
        } else if (id == R.id.action_adminTools){
            startActivity(new Intent(this, AdminActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
