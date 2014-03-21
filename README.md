AdapterSample
=============

A sample android app to demonstrate a pattern of using __AppacitiveQueryAdapter__ with _ListView_ or _GridView_.

This example demonstrates how you can create a _AppacitiveObjectQueryAdapter_ by extending the _BaseAdapter_ to feed a _ListView_ or _GridView_ with [appacitive][4] entities like objects, connections, users or devices.

[4]: <http://appacitive.com/>

This example app creates an adapter for cricket players which are objects of schema type _player_.

To know more about schema types on appacitive, check [this][3] out.

[3]: <http://help.appacitive.com/v1.0/index.html#android/data_objects>

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
public class AppacitiveObjectQueryAdapter extends BaseAdapter { 

    private Context mContext;
    private String mType;            //  schema type
    private List<String> mFields;    //  fields to fetch from the server. use this to reduce network payloads
    private long mPageNumber = 1;    //  default
    private long mPageSize = 10;     //  default
    private long mTotalRecords = 0;  //  for now
    final private List<AppacitiveObject> mObjects = new ArrayList<AppacitiveObject>();  //  the data source for this adapter
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

    //  Populate the data source
    getCurrentPage(); 
}
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

And now we have overridden `BaseAdapters `methods like `getItem(int position)`, `getCount()`, `isEmpty()` and `getItemId(int position)` for them to use the internal array `mObjects` as the data source.

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
@Override 
public AppacitiveObject getItem(int i) { return this.mObjects.get(i); }

@Override 
public int getCount() { return mObjects.size(); }

@Override 
public boolean isEmpty() { return getCount() == 0; }

...
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Some additional helper methods are provided like `clear()`, `addItem(AppacitiveObject player)`, 

`addAll(Collection<AppacitiveObject> playerCollection)`, `removeObject(AppacitiveObject player) `etc. to easily manipulate the data recieved from appacitive.

The `getCurrentPage()` fetches the players from _appacitive_ and notifies the ListView when new data is available by calling the `notifyDataSetChanged()`. The `getNextPage()` and `getPreviousPage()` rely on this method internally.

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public void getCurrentPage() {
        this.query.pageNumber = this.mPageNumber;
        this.query.pageSize = this.mPageSize;
        //  Fetch objects from appacitive as requested by the query.
        AppacitiveObject.findInBackground(this.mType, this.query, this.mFields, new Callback<PagedList<AppacitiveObject>>() {
            @Override
            public void success(PagedList<AppacitiveObject> result) {

                //  Set the totalrecords field of the adapter for paging help
                mTotalRecords = result.pagingInfo.totalRecords;

                //  Clear out the existing objects from adapter and add the new ones.
                mObjects.clear();
                mObjects.addAll(result.results);

                //  Notify listeners there is a fresh list of objects.
                notifyDataSetChanged();

                //  Paging utility toast
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

And now you can override `getView()` to return a view of your choice.

In the example, we are returning an _imageView_ with the player's head shot and a _textView_ with the player's name.

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

And finally just attach your _AppacitiveObjectQueryAdapter_ to your _listView_ with your desired _AppacitiveQuery_.

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    AppacitiveQuery query = new AppacitiveQuery();
    query.pageNumber = 1;
    query.pageSize = 5;
    query.filter = new PropertyFilter("team").isEqualTo("India");
    List<String> fields = null;
    //  Tell the adapter to fire this query on the 'player' schema type.
    AppacitiveObjectQueryAdapter mAdapter = new AppacitiveObjectQueryAdapter(this, "player", fields, query);
    mListView.setAdapter(mAdapter);
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The next and previous button click listeners are programmed to instruct the adapter to fetch the next of previous batch of players respectively.

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.getNextPage();
            }
        });
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

## Screenshot

![screenshot](https://raw.github.com/sathley/AdapterSample/master/screenshot.png)

__Footnote__ :
This example depends on [appacitive android sdk][2] and the amazing image downloading and caching library [picasso][1] by Square.
[2]: <https://github.com/appacitive/appacitive-sdk-core>
[1]: <http://square.github.io/picasso/>









