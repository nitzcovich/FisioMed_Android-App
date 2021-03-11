package com.example.android.heartrate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class InstruccFC extends AppCompatActivity {
Button button_iniciar_FC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrucc_f_c);
       //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        button_iniciar_FC=(Button) findViewById(R.id.button_iniciar_FC);
        button_iniciar_FC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(InstruccFC.this);
                builder.setTitle("Atencion");
                builder.setMessage("La siguiente medición es solo una estimación de tu frecuencia cardíaca. \nNo tiene valor de dianóstico y no reemplaza una consulta médica.");
                builder.setCancelable(true);
                builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent irCameraActivity=new Intent(InstruccFC.this, ProcesamientoFC.class);
                        startActivity(irCameraActivity);
                    }
                });
                AlertDialog resultad = builder.create();
                resultad.show();


            }
        });
    }
}