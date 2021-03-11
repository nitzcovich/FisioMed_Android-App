package com.example.android.heartrate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ParametrosCardiacos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametros_cardiacos);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        defineButtonsCardiacos();

    }

    public void defineButtonsCardiacos(){
        findViewById(R.id.button_FC).setOnClickListener(buttonClickListenerCar);
        findViewById(R.id.button_BP).setOnClickListener(buttonClickListenerCar);
        findViewById(R.id.volver_inicio2).setOnClickListener(buttonClickListenerCar);
    }

    private View.OnClickListener buttonClickListenerCar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.button_FC:
                    Intent irInstFC=new Intent(ParametrosCardiacos.this,InstruccFC.class);
                    startActivity(irInstFC);
                    break;
                case R.id.button_BP:
                    Intent irInstBP = new Intent(ParametrosCardiacos.this,InstruccBP.class);
                    startActivity(irInstBP);
                    break;
                case R.id.volver_inicio2:
                    Intent volverini = new Intent(ParametrosCardiacos.this,Bienvenida.class);
                    startActivity(volverini);
                    break;
            }
        }
    };
}