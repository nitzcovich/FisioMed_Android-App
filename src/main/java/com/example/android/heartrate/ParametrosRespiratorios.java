package com.example.android.heartrate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ParametrosRespiratorios extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametros_respiratorios);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Button button_FR;
        ImageButton volver_inicio4;
        button_FR=(Button) findViewById(R.id.button_FR);
        button_FR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pasarinstru = new Intent(ParametrosRespiratorios.this, InstruccFR.class);
                startActivity(pasarinstru);
            }
        });

        volver_inicio4=(ImageButton) findViewById(R.id.volver_inicio4);
        volver_inicio4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent volverinic = new Intent(ParametrosRespiratorios.this,Bienvenida.class);
                startActivity(volverinic);
            }
        });

    }
}