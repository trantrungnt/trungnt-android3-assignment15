package techkids.mad3.contactphone;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private EditText editTextName, editTextPhone;
    private Button btnAdd;
    ListView mContactsList;
    private SimpleCursorAdapter mCursorAdapter;
    private ArrayList<ContentProviderOperation> ops;
    private ContentResolver cr;
    private String displayName, phone, displayNameDialog, phoneDialog;
    private Context ctx;
    private TextView tvName, tvDisplayName, tvDisplayPhone;
    private Dialog dialogDetail;
    private Button btnOK;
    private String contactID;


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
            if(TextUtils.isEmpty(displayName) || TextUtils.isEmpty(phone)) {
                    showAlertDialog(Helper.TITLE_DIALOG, Helper.CONTENT_DIALOG);
            }
            else
                {
                    //insertContact(displayName, phone, ctx);
                    insertContact(cr, displayName, phone);
                    loadListContact();
                    Log.d("Click me", "ok");
                }
        }
    }

    private void loadListContact()
    {
        // Initialize Content Resolver object to work with content Provider
        cr = getContentResolver();

        // Read Contacts
        Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI,
                new String[] { ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY}, null, null,
                null);

        // Attached with cursor with Adapter
        mCursorAdapter = new SimpleCursorAdapter(this, R.layout.contacts_list_item, c,
                new String[] { ContactsContract.Contacts.DISPLAY_NAME_PRIMARY },
                new int[] { R.id.tvContactItem });

        mContactsList.setAdapter(mCursorAdapter);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dialogDetail = new Dialog((Context)this);
        dialogDetail.setContentView(R.layout.detail_alert);
        dialogDetail.setTitle(Helper.TITLE_DIALOG_DETAIL);
        tvDisplayName = (TextView) dialogDetail.findViewById(R.id.tvDisplayName);
        tvDisplayPhone = (TextView) dialogDetail.findViewById(R.id.tvDisplayPhone);

        Cursor cursorID = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToPosition(position)) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            Log.d("query", contactID);
        }

        cursorID.close();

        Cursor cr = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cr.moveToPosition(position))
        {
            displayNameDialog = cr.getString(cr.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            tvDisplayName.setText(displayNameDialog);
            Log.d("query", displayNameDialog);
        }
        cr.close();

        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                null,
                null,
                null);

        if (cursorPhone.moveToPosition(position)) {
            phoneDialog = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            tvDisplayPhone.setText(phoneDialog);
        }
        cursorPhone.close();
        Log.d("query", "Contact Phone Number: " + phoneDialog);



        btnOK = (Button) dialogDetail.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDetail.dismiss();
            }
        });
        dialogDetail.show();
    }

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


    private void showAlertDialog (String titleDialog, String contentDialog)
    {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(titleDialog);
        builder.setMessage(contentDialog);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public static Cursor getContactCursor(ContentResolver contactHelper, String name) {
        String[] projection = { ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY, ContactsContract.CommonDataKinds.Phone.NUMBER };
        Cursor cur = null;
        try {
            if (name != null && !name.equals("")) {
                cur = contactHelper.query (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " like \"" + name + "\"", null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            }

            cur.moveToFirst();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return cur;
    }



}
