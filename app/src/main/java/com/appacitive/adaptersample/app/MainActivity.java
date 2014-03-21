package com.appacitive.adaptersample.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.appacitive.android.AppacitiveContext;
import com.appacitive.core.model.Environment;
import com.appacitive.core.query.AppacitiveQuery;
import com.appacitive.core.query.PropertyFilter;

import java.util.List;


public class MainActivity extends Activity {

    ListView mListView;
    AppacitiveObjectQueryAdapter mAdapter;
    Button mNextButton;
    Button mPreviousButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //  Initialize appacitive context.
        AppacitiveContext.initialize("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX", Environment.sandbox, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView);
        mNextButton = (Button) findViewById(R.id.next);
        mPreviousButton = (Button) findViewById(R.id.previous);

        //  In the listview, we will show all objects of schema type 'player' whose team is 'India'.
        //  Create the query accordingly
        AppacitiveQuery query = new AppacitiveQuery();
        query.pageNumber = 1;
        query.pageSize = 5;
        query.filter = new PropertyFilter("team").isEqualTo("India");
        List<String> fields = null;
        //  Tell the adapter to fire this query on the 'player' schema type.
        mAdapter = new AppacitiveObjectQueryAdapter(this, "player", fields, query);
        mListView.setAdapter(mAdapter);

        //  attach next page button listener
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.getNextPage();
            }
        });

        //  attach previous page button listener
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.getPreviousPage();
            }
        });
    }
}
