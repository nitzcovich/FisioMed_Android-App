package com.example.android.heartrate;


import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ResultadosFR extends AppCompatActivity {
    ImageButton volver_inicio3;
    private static final int SAMPLING_RATE_IN_HZ = 44100;

    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;

    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private Button calcButton;

    private static TextView calcText;

    private Button playButton;

    private Button graphButton;

    private LineGraphSeries<DataPoint> series1;

    private GraphView graphView;
    private Button button_guardarFR;
    public String FR;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados_f_r);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        calcText = (TextView) findViewById(R.id.calculateText);

        calculateRR();


        playButton = (Button) findViewById(R.id.btnPlay);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecord();
            }
        });

        graphView = (GraphView) findViewById(R.id.grafico);
        graphAudio();
        button_guardarFR= (Button) findViewById(R.id.button_guardarFR);
        button_guardarFR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ResultadosFR.this, GuardarFR.class);
                i.putExtra("frecR", FR);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

      private void calculateRR() {
        File file = new File(Environment.getExternalStorageDirectory(), "recording.pcm");
        int shortSizeInBytes = Short.SIZE / Byte.SIZE;
        int bufferSizeInBytes = (int) (file.length() / shortSizeInBytes);

        byte[] audioData = new byte[(int) file.length()];
        try {
            InputStream inputStream = new FileInputStream(file);
            inputStream.read(audioData);
            inputStream.close();

            short[] shorts = new short[audioData.length / 2];
            ByteBuffer.wrap(audioData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

            double[] absshorts = new double[shorts.length];

            for (int l = 0;l<shorts.length;l++){
                absshorts[l] = Math.abs(shorts[l]);
            }

            double[] filtrada = movingAverage(absshorts,10000);
            double max = getMaxValue(filtrada); //normalizar la grafica

            double umbral = 0.2;
            int RR = 0;
            double t = 0;
            double t2;
            int maxRRpm = 25;
            double ref = 60/maxRRpm;

            for(int m = 0;m<filtrada.length;m++){
                filtrada[m] = filtrada[m]/max;
            }

            for(int k =0;k<filtrada.length;k++){
                t2 = t*10/filtrada.length;
                if(Double.compare(filtrada[k],umbral)>0){
                    if(t2>ref){
                        RR++;
                        t=0;
                    }
                    if(t2==ref || t2 ==0){
                        RR++;
                        t=0;
                    }
                }
                t++;
            }
            calcText.setText(String.valueOf(RR*6));
            FR=String.valueOf(RR*6);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void graphAudio() {
        graphView.removeAllSeries();
        File file = new File(Environment.getExternalStorageDirectory(), "recording.pcm");
        int shortSizeInBytes = Short.SIZE / Byte.SIZE;
        int bufferSizeInBytes = (int) (file.length() / shortSizeInBytes);

        byte[] audioData = new byte[(int) file.length()];
        try {
            InputStream inputStream = new FileInputStream(file);
            inputStream.read(audioData);
            inputStream.close();

            short[] shorts = new short[audioData.length / 2];
            ByteBuffer.wrap(audioData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

            series1 = new LineGraphSeries<>();

            double x, y, x2,p;
            x = 0;
            int numDataPoints = shorts.length;
            int i = 0;

            double[] absshorts = new double[shorts.length];

            for (int l = 0;l<shorts.length;l++){
                absshorts[l] = Math.abs(shorts[l]);
            }

            double[] filtrada = movingAverage(absshorts,8000);
            double max = getMaxValue(filtrada); //normalizar la grafica

            while (x < numDataPoints) {
                y = filtrada[i]/max;
                x2 = x * 10 / numDataPoints;
                x++;
                series1.appendData(new DataPoint(x2, y), true, numDataPoints);
                i++;
            }

            graphView.addSeries(series1);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void playRecord() {
        File file = new File(Environment.getExternalStorageDirectory(), "recording.pcm");
        int shortSizeInBytes = Short.SIZE / Byte.SIZE;
        int bufferSizeInBytes = (int) (file.length() / shortSizeInBytes);

        byte[] audioData = new byte[(int) file.length()];

        try {
            InputStream inputStream = new FileInputStream(file);
            inputStream.read(audioData);
            inputStream.close();

            short[] shorts = new short[audioData.length / 2];
            ByteBuffer.wrap(audioData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

            //final String promptPlayRecord = "PlayRecord()\n" + file.getAbsolutePath() + "\n" + "44100";
            //Toast.makeText(Main5Activity.this, promptPlayRecord, Toast.LENGTH_LONG).show();

            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    44100, AudioFormat.CHANNEL_OUT_MONO, AUDIO_FORMAT,
                    AudioRecord.getMinBufferSize(SAMPLING_RATE_IN_HZ,
                            AudioFormat.CHANNEL_IN_MONO, AUDIO_FORMAT), AudioTrack.MODE_STREAM);

            audioTrack.play();
            //audioTrack.write(audioData, 0, audioData.length);
            audioTrack.write(shorts, 0, shorts.length);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double[] movingAverage(double[] signal, int order) {

        int[] b = new int[order + 1];
        b[0] = 1;
        int[] a = new int[2];
        a[0] = order;
        a[1] = -order;

        for (int i = 1; i < (order - 1); i++) {
            b[i] = 0;
        }
        b[order] = -1;

        double[] filtrada = new double[signal.length];
        for (int j = 0; j < filtrada.length; j++) {
            filtrada[0] = 0;
        }
        for (int n = order; n < signal.length; n++) {
            filtrada[n] = b[0] * signal[n] + b[order] * signal[n - order] - a[1] * filtrada[n - 1];
            filtrada[n] = filtrada[n] / order;
        }

        return filtrada;

    }
    public static double getMaxValue(double[] numbers){
        double maxValue = numbers[0];
        for(int i=1;i < numbers.length;i++){
            if(numbers[i] > maxValue){
                maxValue = numbers[i];
            }
        }
        return maxValue;
    }

}
