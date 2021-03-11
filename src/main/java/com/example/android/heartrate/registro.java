package com.example.android.heartrate;

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

import static java.lang.Math.round;

public class registro extends AppCompatActivity {
    DatabaseHelper mDatabaseHelper;
    Button guardar;
    Button calcular;
    private EditText peso;
    private EditText altura;
    private TextView IMC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        guardar = (Button) findViewById(R.id.button_registrar);
        calcular = (Button) findViewById(R.id.button_calcular);
        peso = (EditText) findViewById(R.id.editText_peso);
        altura = (EditText) findViewById(R.id.editText_altura);
        IMC = (TextView) findViewById(R.id.textView15);
        mDatabaseHelper = new DatabaseHelper(this);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy hh:mm");
                String strDate = dateFormat.format(date);

                String P=String.valueOf(peso.getText());
                if(peso.length()!=0){
                    String newEntry= strDate + " || " + "PESO: " + P + " kg";
                    AddData(newEntry);
                }else{
                    toastMessage("El peso no puede estar vacío.");
                }
            }
        });

        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int P= Integer.valueOf(String.valueOf(peso.getText()));
                double A= Double.valueOf(String.valueOf(altura.getText()));
                //double A= 1.70;
                if (A>3){
                    toastMessage("La altura debe estar en metros.");
                }else{
                    double imc = P/(A*A);
                    IMC.setText(String.valueOf(round(imc)));
                    //IMC.setText("Aver");
                }
            }
        });
    }
    public void AddData(String newEntry) {
        boolean insertData = mDatabaseHelper.addData(newEntry);

        if (insertData) {
            toastMessage("Peso guardado");
        } else {
            toastMessage("Ups! Hubo un error");
        }
    }
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}