package com.appacitive.adaptersample.app;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.appacitive.core.AppacitiveObject;
import com.appacitive.core.model.Callback;
import com.appacitive.core.model.PagedList;
import com.appacitive.core.query.AppacitiveQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sathley.
 */
public class AppacitiveObjectQueryAdapter extends BaseAdapter {

    public Context getContext() {
        return mContext;
    }

    public List<AppacitiveObject> getObjects() {
        return mObjects;
    }

    private Context mContext;
    private String mType;
    private List<String> mFields;
    private long mPageNumber = 1;    //  default
    private long mPageSize = 10;     //  default
    private long mTotalRecords = 0;  //  for now

    final private List<AppacitiveObject> mObjects = new ArrayList<AppacitiveObject>();

    private AppacitiveQuery query = null;

    public AppacitiveObjectQueryAdapter(Context context, String type, List<String> fields, AppacitiveQuery query) {
        this.mContext = context;
        this.mType = type;
        this.mFields = fields;
        this.query = query;
        if (query.pageNumber > 0)
            this.mPageNumber = query.pageNumber;
        if (query.pageSize > 0)
            this.mPageSize = query.pageSize;

        getCurrentPage();
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    public void add(AppacitiveObject object) {
        this.mObjects.add(object);
    }

    public void addAll(Collection<AppacitiveObject> objectCollection) {
        this.mObjects.addAll(objectCollection);
    }

    public void insert(AppacitiveObject object, int index) {
        this.mObjects.add(index, object);
    }

    public void removeObject(AppacitiveObject object) {
        for (int i = 0; i < this.mObjects.size() - 1; i++) {
            if (object.getId() == this.mObjects.get(i).getId()) {
                this.mObjects.remove(i);
                return;
            }
        }
    }

    @Override
    public AppacitiveObject getItem(int i) {
        return this.mObjects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.mObjects.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView = new TextView(getContext());
        textView.setText(getItem(i).getPropertyAsString("name"));
        return textView;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }

    public void clear() {
        this.mObjects.clear();
        notifyDataSetInvalidated();
    }

    public int getPosition(AppacitiveObject item) {
        long id = item.getId();
        for (int i = 0; i < this.mObjects.size() - 1; i++) {
            if (this.mObjects.get(i).getId() == id)
                return i;
        }
        return -1;
    }

    public void getCurrentPage() {
        this.query.pageNumber = this.mPageNumber;
        this.query.pageSize = this.mPageSize;

        //  Fetch objects from appacitive as requested by the query.
        AppacitiveObject.findInBackground(this.mType, this.query, this.mFields, new Callback<PagedList<AppacitiveObject>>() {
            @Override
            public void success(PagedList<AppacitiveObject> result) {
                //  Set the totalrecords field of the adapter for paging help.
                mTotalRecords = result.pagingInfo.totalRecords;
                //  Clear out the existing objects from adapter and add the new ones.
                mObjects.clear();
                mObjects.addAll(result.results);
                //  Notify listeners there is a fresh list of objects.
                notifyDataSetChanged();
                //  Paging utility
                Toast.makeText(getContext(), String.format("Showing page " + mPageNumber + " of " + (int) Math.ceil((double) mTotalRecords / (double) mPageSize) + "."), Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(PagedList<AppacitiveObject> result, Exception e) {
                Log.e("APPACITIVE", e.getMessage());
            }
        });
    }

    public void getNextPage() {
        if (mPageNumber == (int) Math.ceil((double) mTotalRecords / (double) mPageSize)) {
            Toast.makeText(getContext(), "Can't go any more further!", Toast.LENGTH_LONG).show();
            return;
        }
        this.mPageNumber++;
        getCurrentPage();
    }

    public void getPreviousPage() {
        if (this.mPageNumber == 1) {
            Toast.makeText(getContext(), "Can't go further back!", Toast.LENGTH_LONG).show();
            return;
        }
        this.mPageNumber--;
        getCurrentPage();
    }
}
