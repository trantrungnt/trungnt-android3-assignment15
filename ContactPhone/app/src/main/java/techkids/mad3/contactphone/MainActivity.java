package techkids.mad3.contactphone;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private EditText editTextName, editTextPhone;
    private Button btnAdd;
    ListView mContactsList;
    private SimpleCursorAdapter mCursorAdapter;
    private Cursor contactsCursor;

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
        // Initialize Content Resolver object to work with content Provider
        ContentResolver cr = getContentResolver();

        // Read Contacts
        Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI,
                new String[] { ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME }, null, null,
                null);

        // Attached with cursor with Adapter
        mCursorAdapter = new SimpleCursorAdapter(this, R.layout.contacts_list_item, c,
                new String[] { ContactsContract.Contacts.DISPLAY_NAME },
                new int[] { R.id.tvContactItem });

        mContactsList.setAdapter(mCursorAdapter);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("ID", String.valueOf(position));
        Log.d("Phone", String.valueOf(ContactsContract.Contacts.DISPLAY_NAME));

    }

}
