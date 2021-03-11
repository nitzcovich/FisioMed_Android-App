package com.example.android.heartrate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ResultadosFC extends AppCompatActivity {
    int HR;
    String FC;
    ImageButton volver_inicio;
    Button button_guardarFC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados_f_c);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView RHR = (TextView) this.findViewById(R.id.calculateText);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            HR = bundle.getInt("bpm");
            RHR.setText(String.valueOf(HR));
        }
        button_guardarFC= (Button) findViewById(R.id.button_guardarFC);
        button_guardarFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ResultadosFC.this, guardarFC.class);
                FC =  String.valueOf(HR);
                i.putExtra("frecC", FC);
                startActivity(i);
                finish();
            }
        });
    }
}