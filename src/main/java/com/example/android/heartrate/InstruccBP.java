package com.example.android.heartrate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class InstruccBP extends AppCompatActivity {
    ImageButton volver_inicio5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrucc_b_p);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        volver_inicio5=(ImageButton) findViewById(R.id.volver_inicio5);
        volver_inicio5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent volverinicio = new Intent(InstruccBP.this,Bienvenida.class);
                startActivity(volverinicio);
            }
        });
    }
}