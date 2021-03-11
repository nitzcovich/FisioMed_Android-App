package com.example.android.heartrate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class guardarFC extends AppCompatActivity {
    DatabaseHelper mDatabaseHelper;
    Button btn_guardarFC;
    private EditText editText;
    public String FC;
    private static TextView frec_car;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardar_f_c);
        btn_guardarFC=(Button) findViewById(R.id.btn_guardarFC);
        editText=(EditText) findViewById(R.id.comentFC);
        frec_car = (TextView) findViewById(R.id.frec_car);
        mDatabaseHelper = new DatabaseHelper(this);

        btn_guardarFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy hh:mm");
                String strDate = dateFormat.format(date);

                String newCom = editText.getText().toString();
                FC=frec_car.getText().toString();
                if (editText.length()!=0){
                    String newEntry = strDate + " || FREC. CARDÍACA:" + FC + " || " + newCom;
                    AddData(newEntry);
                }else{
                    String newEntry = strDate + " || FREC. CARDÍACA:" + FC;
                    AddData(newEntry);
                }
                Intent volverini = new Intent(guardarFC.this,Bienvenida.class);
                startActivity(volverini);
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            FC = bundle.getString("frecC");
            frec_car.setText(FC);
        }
    }
    public void AddData(String newEntry) {
        boolean insertData = mDatabaseHelper.addData(newEntry);

        if (insertData) {
            toastMessage("Medicion guardada");
        } else {
            toastMessage("Ups! Hubo un error");
        }
    }
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}