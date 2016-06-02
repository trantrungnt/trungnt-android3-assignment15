package techkids.mad3.contactphone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextName, editTextPhone;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }

    private void initComponents()
    {
        editTextName = (EditText) this.findViewById(R.id.editTextName);
        editTextPhone = (EditText) this.findViewById(R.id.editTextPhone);
        btnAdd = (Button) this.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id==R.id.btnAdd)
        {
            
        }
    }
}
