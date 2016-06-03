package techkids.mad3.contactphone;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private EditText editTextName, editTextPhone;
    private Button btnAdd;
    private final static int[] TO_IDS = {
        android.R.id.text1
    };
    ListView mContactsList;
    long mContactId;
    String mContactKey;
    Uri mContactUri;
    private SimpleCursorAdapter mCursorAdapter;
    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ?
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                            ContactsContract.Contacts.DISPLAY_NAME

            };

    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME
    };

    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadListContact();
    }

    private void initComponents()
    {
        editTextName = (EditText) this.findViewById(R.id.editTextName);
        editTextPhone = (EditText) this.findViewById(R.id.editTextPhone);
        btnAdd = (Button) this.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        mContactsList = (ListView) this.findViewById(R.id.lvContacts);
        mContactsList.setOnItemClickListener(this);

        // Initializes the loader
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id==R.id.btnAdd)
        {

        }
    }

    private void loadListContact()
    {
        mContactsList = (ListView) this.findViewById(R.id.lvContacts);
        // Gets a CursorAdapter
        mCursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.contacts_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        // Sets the adapter for the ListView
        mContactsList.setAdapter(mCursorAdapter);
    }


    /*
     * Constructs search criteria from the search string
     * and email MIME type
     */
    private static final String SELECTION =
            /*
             * Searches for an email address
             * that matches the search string
             */
            ContactsContract.CommonDataKinds.Email.ADDRESS + " LIKE ? " + "AND " +
            /*
             * Searches for a MIME type that matches
             * the value of the constant
             * Email.CONTENT_ITEM_TYPE. Note the
             * single quotes surrounding Email.CONTENT_ITEM_TYPE.
             */
                    ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'";
    String mSearchString;
    String[] mSelectionArgs = { mSearchString };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("ID", String.valueOf(position));
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // OPTIONAL: Makes search string into pattern
        mSearchString = "%" + mSearchString + "%";
        // Puts the search string into the selection criteria
        mSelectionArgs[0] = mSearchString;
        // Starts the query
        return new CursorLoader(
                this,
                ContactsContract.Data.CONTENT_URI,
                PROJECTION,
                SELECTION,
                mSelectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Put the result Cursor in the adapter for the ListView
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Delete the reference to the existing Cursor
        mCursorAdapter.swapCursor(null);
    }
}
