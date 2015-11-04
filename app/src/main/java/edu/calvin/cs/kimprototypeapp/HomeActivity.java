package edu.calvin.cs.kimprototypeapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * HomeActivity displays the stocks in a list format
 */
public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()) //starts placeholder fragment
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
 * A placeholder fragment containing a simple view.
 */
public static class PlaceholderFragment extends Fragment {

    ArrayAdapter<String> mStockAdapter; //create the ArrayAdapter, responsible for populating ListView

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] data = {
                "CHE - Servies Sector", "THOR", "CNS", "LBY", "EXLS", "ANIK", "MOV", "MTX", "CJES", "KWR", "ALG", "EGBN", "EFII", "BEKA'S STOCK!!!"
        };

        //takes array of data and stores it as a list
        List<String> stocks = new ArrayList<String>(Arrays.asList(data));

        //Adapter takes data from the source and uses it to populate ListView
        mStockAdapter = new ArrayAdapter<String>(
                getActivity(), //fragment's parent activity
                R.layout.list_item_stock, //name of layout ID
                R.id.list_item_stock_textview, //ID of textview in that layout to populate
                stocks //the data to populate it with
        );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_stocks);
        listView.setAdapter(mStockAdapter); //supply list item layouts to list view based on the forecast data

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String stockName = mStockAdapter.getItem(position);
                //Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, stockName);
               // intent.putExtra(Intent.EXTRA_TEXT, stockIsUp);
                startActivity(intent);


            }
        });

        return rootView;
    }
}
}