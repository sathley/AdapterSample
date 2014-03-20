package com.appacitive.adaptersample.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.appacitive.android.AppacitiveContext;
import com.appacitive.core.model.Environment;
import com.appacitive.core.query.AppacitiveQuery;


public class MainActivity extends Activity {

    ListView mListView;
    AppacitiveObjectQueryAdapter mAdapter;
    Button mNext;
    Button mPrevious;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //  Initialize appacitive context.
        AppacitiveContext.initialize("up8+oWrzVTVIxl9ZiKtyamVKgBnV5xvmV95u1mEVRrM=", Environment.sandbox, this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView);
        mNext = (Button) findViewById(R.id.next);
        mPrevious = (Button) findViewById(R.id.previous);

        //  In the listview, we will show all objects of schema type 'player' whose team is 'India'.
        //  Create the query accordingly
        AppacitiveQuery query = new AppacitiveQuery();
        query.pageNumber = 1;
        query.pageSize = 5;
//        query.filter = new PropertyFilter("team").isEqualTo("India");

        //  Tell the adapter to fire this query on the 'player' schema type.
        mAdapter = new AppacitiveObjectQueryAdapter(this, "player", null, query);
        mListView.setAdapter(mAdapter);

        //  attach next page button listener
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.getNextPage();
            }
        });

        //  attach previous page button listener
        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.getPreviousPage();
            }
        });
    }
}
