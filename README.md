AdapterSample
=============



A sample android app to demonstrate a pattern of using __AppacitiveQueryAdapter__ with _ListView_ or _GridView_.



This example demonstrates how you can create a _AppacitiveObjectQueryAdapter_ by extending the _BaseAdapter_ to feed a _ListView_ or _GridView_ with [Appacitive ][4]entities like objects, connections, users or devices.

[4]: <http://appacitive.com/>

This example app creates an adapter for cricket players which are objects of schema type _player_.

To know more about schema types on appacitive, go [here][3].

[3]: <http://help.appacitive.com/v1.0/index.html#android/data_objects>



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
public class AppacitiveObjectQueryAdapter extends BaseAdapter { 

    public Context getContext() { return mContext; } 
    public ListAppacitiveObject getObjects() { return mObjects; } 
    private Context mContext; 
    private String mType; 
    private ListString mFields; 
    private long mPageNumber = 1;    //  default 
    private long mPageSize = 10;     //  default 
    private long mTotalRecords = 0;  //  for now 

    final private ListAppacitiveObject mObjects = new ArrayListAppacitiveObject(); 

    private AppacitiveQuery query = null;

    ...
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



The constructor sets the internal fields of the adapter.



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
public AppacitiveObjectQueryAdapter(Context context, String type, ListString fields, AppacitiveQuery query) { 
    this.mContext = context; 
    this.mType = type; 
    this.mFields = fields; 
    this.query = query; 
    if (query.pageNumber > 0) 
        this.mPageNumber = query.pageNumber; 
    if (query.pageSize > 0) this.mPageSize = query.pageSize;

    getCurrentPage(); 
}
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



And now we have overriden `BaseAdapters `methods like `getItem(int position)`, `getCount()`, `isEmpty()` and `getItemId(int position)`.

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

@Override 
public AppacitiveObject getItem(int i) { return this.mObjects.get(i); }

@Override 
public int getCount() { return mObjects.size(); }

@Override 
public boolean isEmpty() { return getCount() == 0; }

...
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    



And now you can override `getView() `to return a view of your choice.

In the example, we are returning an *imageView *with the players headshot and a *textView *with the players name.



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
@Override 
public View getView(int i, View view, ViewGroup viewGroup) { 
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
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



Some additional helper methods are provided like `clear()`, `addItem(AppacitiveObject player)`, 

`addAll(Collection<AppacitiveObject> playerCollection)`, `removeObject(AppacitiveObject player) `etc.



The `getCurrentPage()` fetches the players from *appacitive *and notifies the ListView when new data is available by calling the `notifyDataSetChanged()`.



The `getNextPage()` and `getPreviousPage()` rely on this method internally.



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public void getCurrentPage() {
        this.query.pageNumber = this.mPageNumber;
        this.query.pageSize = this.mPageSize;
        //  Fetch objects from appacitive as requested by the query.
        AppacitiveObject.findInBackground(this.mType, this.query, this.mFields, new Callback<PagedList<AppacitiveObject>>() {
            @Override
            public void success(PagedList<AppacitiveObject> result) {

                //  Set the totalrecords field of the adapter for paging help.getItem
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
            public void failure(PagedList<AppacitiveObject> result, Exception e) 
            {
                Log.e("APPACITIVE", e.getMessage());
            }
        });
    }
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


## Screenshot

![screenshot](https://raw.github.com/sathley/AdapterSample/master/screenshot.png)

This example depends on [appacitive android sdk][2] and the amazing image downloading and caching library [picasso][1] by Square.

[1]: <http://square.github.io/picasso/> [2]: <https://github.com/appacitive/appacitive-sdk-core>








