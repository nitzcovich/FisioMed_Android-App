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

public class GuardarFR extends AppCompatActivity {
    DatabaseHelper mDatabaseHelper;
    Button btn_guardarFR;
    private EditText editText;
    public String FR;
    private static TextView frec_resp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardar_f_r);
        btn_guardarFR=(Button) findViewById(R.id.btn_guadarFR);
        editText = (EditText) findViewById(R.id.comentFR);
        frec_resp = (TextView) findViewById(R.id.frec_resp);
        mDatabaseHelper = new DatabaseHelper(this);


        btn_guardarFR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy hh:mm");
                String strDate = dateFormat.format(date);

                String newCom = editText.getText().toString();
                FR=frec_resp.getText().toString();

                if (editText.length() != 0) {
                    String newEntry= strDate + " || FREC. RESPIRATORIA:" + FR + " || " + newCom;
                    AddData(newEntry);
                    //editText.setText("");
                    Intent volverini = new Intent(GuardarFR.this,Bienvenida.class);
                    startActivity(volverini);
                }else{
                    String newEntry= strDate + " || FREC. RESPIRATORIA:" + FR;
                    toastMessage("Guardado sin comentario");
                    AddData(newEntry);
                    //editText.setText("");
                    Intent volverini = new Intent(GuardarFR.this,Bienvenida.class);
                    startActivity(volverini);
                }


            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            FR = bundle.getString("frecR");
            frec_resp.setText(FR);
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