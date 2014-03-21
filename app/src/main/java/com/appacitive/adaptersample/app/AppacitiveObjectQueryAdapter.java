package com.appacitive.adaptersample.app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appacitive.core.AppacitiveObject;
import com.appacitive.core.model.Callback;
import com.appacitive.core.model.PagedList;
import com.appacitive.core.query.AppacitiveQuery;
import com.squareup.picasso.Picasso;

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
    private String mType;            //  schema type
    private List<String> mFields;    //  fields to fetch from the server. use this to reduce network payloads
    private long mPageNumber = 1;    //  default
    private long mPageSize = 10;     //  default
    private long mTotalRecords = 0;  //  for now
    final private List<AppacitiveObject> mObjects = new ArrayList<AppacitiveObject>();  //  the data source for this adapter
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

        //  Populate the data source
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
        //  put your app specific implementation here
        LayoutInflater li = LayoutInflater.from(getContext());
        View playerItem = li.inflate(R.layout.player_item, null, false);
        ImageView photoView = (ImageView) playerItem.findViewById(R.id.imageView);
        TextView nameView = (TextView) playerItem.findViewById(R.id.name);

        AppacitiveObject player = getItem(i);
        String playerName = player.getPropertyAsString("name");
        String photoUrl = player.getPropertyAsString("photo_url");

        nameView.setText(playerName);
        Picasso.with(getContext())
                .load(photoUrl)
                .placeholder(R.drawable.placeholder)
                .into(photoView);

        return playerItem;
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
                //  Set the totalrecords field of the adapter for paging help
                mTotalRecords = result.pagingInfo.totalRecords;
                //  Clear out the existing objects from adapter and add the new objects.
                mObjects.clear();
                mObjects.addAll(result.results);
                //  Notify listeners there is a fresh list of objects.
                notifyDataSetChanged();
                //  Paging utility toast
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
            Toast.makeText(getContext(), "Can't go further forward!", Toast.LENGTH_LONG).show();
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
