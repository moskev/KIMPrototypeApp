package edu.calvin.cs.kimprototypeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class PortfolioActivity extends Activity {

    //create loginButton
    private Button individualStockButton;

    //get ImageView to add kimLogo in
    //private ImageView kimLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        individualStockButton =(Button)  findViewById(R.id.individualStockButton); //assign individual button
        //kimLogo = (ImageView) findViewById(R.id.kimLogo);
        //kimLogo.setImageResource(R.mipmap.knight_investment_management);

        //create onClick listener for login button bringing to HomeActivity
        individualStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create and start new Intent
                Intent home = new Intent(PortfolioActivity.this, HomeActivity.class);
                startActivity(home);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_portfolio, menu);
        return true;
    }

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
