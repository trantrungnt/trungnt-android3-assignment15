package techkids.mad3.contactphone;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private EditText editTextName, editTextPhone;
    private Button btnAdd;
    ListView mContactsList;
    private SimpleCursorAdapter mCursorAdapter;
    private ArrayList<ContentProviderOperation> ops;
    private ContentResolver cr;
    private String displayName, phone;
    private Context ctx;
    private ContentResolver contactAdder;

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
        displayName = editTextName.getText().toString();
        phone = editTextPhone.getText().toString();
        cr = getContentResolver();

        if (id==R.id.btnAdd)
        {
            //insertContact(displayName, phone, ctx);
            insertContact(cr, displayName, phone);
            loadListContact();
            Log.d("Click me", "ok");
        }
    }

    private void loadListContact()
    {
        // Initialize Content Resolver object to work with content Provider
        cr = getContentResolver();

        // Read Contacts
        Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI,
                new String[] { ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY }, null, null,
                null);

        // Attached with cursor with Adapter
        mCursorAdapter = new SimpleCursorAdapter(this, R.layout.contacts_list_item, c,
                new String[] { ContactsContract.Contacts.DISPLAY_NAME_PRIMARY },
                new int[] { R.id.tvContactItem });

        mContactsList.setAdapter(mCursorAdapter);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

//    private void insertContact(String displayName, String phone, ContentResolver contactAdder)
//    {
//        Context contetx = ctx; //Application's context or Activity's context
//
//        ArrayList<ContentProviderOperation> cntProOper = new ArrayList<ContentProviderOperation>();
//        int contactIndex = cntProOper.size();//ContactSize
//
//        //Newly Inserted contact
    // A raw contact will be inserted ContactsContract.RawContacts table in contacts database.
//    cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)//Step1
//            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
//    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
//        // A raw contact will be inserted ContactsContract.RawContacts table in contacts database.
//        //Display name will be inserted in ContactsContract.Data table
//        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step2
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,contactIndex)
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME_PRIMARY, displayName) // Name of the contact
//                .build());
//        //Mobile number will be inserted in ContactsContract.Data table
//        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step 3
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,contactIndex)
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone) // Number to be added
//                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); //Type like HOME, MOBILE etc
//        try
//        {
//            // We will do batch operation to insert all above data
//            //Contains the output of the app of a ContentProviderOperation.
//            //It is sure to have exactly one of uri or count set
//            //ContentProviderResult[] contentProresult = null;
//            contactAdder.applyBatch(ContactsContract.AUTHORITY, cntProOper); //apply above data insertion into contacts list
//        }
//        catch (RemoteException exp)
//        {
//            //logs;
//        }
//        catch (OperationApplicationException exp)
//        {
//            //logs
//        }
//    }

    //Phuong thuc them moi du lieu vao Contact Provider
    private void insertContact(ContentResolver contactAdder, String displayName, String mobileNumber) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,displayName).build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,mobileNumber).withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        try {
            contactAdder.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {

        }
    }

}
