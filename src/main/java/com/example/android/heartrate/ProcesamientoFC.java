package com.example.android.heartrate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.heartrate.MATH.Fft;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Math.ceil;

public class ProcesamientoFC extends AppCompatActivity {

    //Inicializacion de variables
    private static final String TAG = "HeartRateMonitor";
    private static final AtomicBoolean processing = new AtomicBoolean(false);
    private static final int MY_PERMISSIONS_REQUEST_CAMERA= 1;
    private SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static WakeLock wakeLock = null;

    //Toast
    private Toast mainToast;

    //Inicializamos variables para los latidos
    public int Beats = 0;
    public double bufferAvgB = 0;

    //DataBase
    public String user;

    //Barra de progreso
    private ProgressBar ProgHeart;
    public int ProgP = 0;
    public int inc = 0;

    //Frecuencia de muestreo y contador temporal
    private static long startTime = 0;
    private double SamplingFreq;

    //Lista de promedios para canales verdes y rojos
    public ArrayList<Double> GreenAvgList = new ArrayList<Double>();
    public ArrayList<Double> RedAvgList = new ArrayList<Double>();
    public int counter = 0;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
       /*Called when the activity is first created. This is where you should do all of your normal static
       set up: create views, bind data to lists, etc. This method also provides you with a Bundle containing
       the activity's previously frozen state, if there was one. Always followed by onStart().*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procesamiento_f_c);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /*Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("Usr");
            //The key argument here must match that used in the other activity
        }*/

        // Llamando  a componendtes de xml
        preview = (SurfaceView) findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        ProgHeart = (ProgressBar) findViewById(R.id.HRPB);
        ProgHeart.setProgress(0);

        //Inicializacion de WakeLock, obliga al celular a no apagarse
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

        //Chequea que haya permiso para acceder a la camara
        if (ContextCompat.checkSelfPermission(ProcesamientoFC.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                //Aca entra si no tenemos el permiso
                //Aca podemos poner una explicacion de porque necesitamos que nos de permiso

            if (ActivityCompat.shouldShowRequestPermissionRationale(ProcesamientoFC.this,
                    Manifest.permission.CAMERA)) {
                //Aca se puede poner de nuevo la explicacion pidiendo permiso
                //No entiendo bien la diferencia con el anterior
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(ProcesamientoFC.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

            }
        } else {
                //aca se entra si si tenemos el permiso
        }

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    //Prendemos WakeLock, abrimos la camara, ponemos la orientacion en 90 grados
    //Guardo la hora a la cual arranco el proceso
    //Empiezo a interactuar con el usuario
    @Override
    public void onResume() {
        /*Called when the activity will start interacting with the user. At this point your activity is
        at the top of its activity stack, with user input going to it. Always followed by onPause().*/
        super.onResume();
        wakeLock.acquire();
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        startTime = System.currentTimeMillis();
    }

    //llama a los frames, libera la camara y a wakeLock e inicializa como nula la camara
    //Se llama como parte del el ciclo de vida de la app cuando una actividad pasa al backgrouns pero no se termina
    //Es lo opuesto a onResume()
    //Si yo desde A llamo a la actividad B, B no va a iniciar hasta no ver que pasa en el onPause de A
    //Se asegura que no haya nada que no pueda soportar
    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    //obtenemos los frames de la camara e iniciamos el proceso de deteccion de latidos
    private PreviewCallback previewCallback = new PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {

            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            //prcesing es un Booleano Atomico. si es falso lo actualiza y pone un true
            if (!processing.compareAndSet(false, true)) return;

            //Tamano de la camara
            int width = size.width;
            int height = size.height;

            double GreenAvg;
            double RedAvg;

            //Transformacion de YUV (RAW) a RGB
            // rojo: 1, verde: 2, azul: 3
            //Obtengo los promedios de cada frame de cada capa
            GreenAvg = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(), height, width, 3);
            RedAvg = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(), height, width, 1);

            //Pongo los promedios de cada frame en una lista
            GreenAvgList.add(GreenAvg);
            RedAvgList.add(RedAvg);

            //Contador de frames
            ++counter;


            //Chequea que haya suficiente rojo. Si no arranca de cero.
            //pasaria si no estoy enfocando al dedo
            if (RedAvg < 200) {
                inc = 0;
                ProgP = inc;
                counter = 0;
                ProgHeart.setProgress(ProgP);
                processing.set(false);
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d; //pasamos el tiempo a segs
            if (totalTimeInSecs >= 30) { //se toman 30 segs

                //Pasaje de listas a arreglos
                Double[] Green = GreenAvgList.toArray(new Double[GreenAvgList.size()]);
                Double[] Red = RedAvgList.toArray(new Double[RedAvgList.size()]);

                //Calculo de la frecuencia de muestreo como cnatidad de frames / tiempo
                //Esto da en Hz
                SamplingFreq = (counter / totalTimeInSecs);

                double HRFreq = Fft.FFT(Green, counter, SamplingFreq); // Calcula la FFT del arreglo verde y ya calcula su HR
                double bpm = (int) ceil(HRFreq * 60);
                double HR1Freq = Fft.FFT(Red, counter, SamplingFreq);  // Calcula la FFT del arreglo rojo y ya calcula su HR
                double bpm1 = (int) ceil(HR1Freq * 60);


                if ((bpm > 45 || bpm < 200)) {
                    if ((bpm1 > 45 || bpm1 < 200)) {
                        //Si las dos mediciones son razonables, promedio
                        bufferAvgB = (bpm + bpm1) / 2;
                    } else {
                        //si solo la verde es razonable
                        bufferAvgB = bpm;
                    }
                } else if ((bpm1 > 45 || bpm1 < 200)) {
                    //si solo la roja es razonable
                    bufferAvgB = bpm1;
                }

                if (bufferAvgB < 45 || bufferAvgB > 200) {
                    //Si la medicion no fue razonable, reseteamos barra de progreso y comenzamos de neuvo la medicion
                    //Aca solo entraria igual si bpm y bpm1 no eran razonables. Ene se caso bufferAvgB es cero pq se inicializo asi
                    inc = 0;
                    ProgP = inc;
                    ProgHeart.setProgress(ProgP);
                    mainToast = Toast.makeText(getApplicationContext(), "Tenemos que reiniciar la medición." /*+ bufferAvgB*/, Toast.LENGTH_SHORT);
                    mainToast.show();
                    startTime = System.currentTimeMillis();
                    counter = 0;
                    processing.set(false);
                    return;
                }

                Beats = (int) bufferAvgB;
            }

            if (Beats != 0) {
                //Si los latidos eran razonables, detiene el loop y envia el HR a una nueva actividad, donde se imprime
                //Finaliza esta actividad
                Intent i = new Intent(ProcesamientoFC.this, ResultadosFC.class);
                i.putExtra("bpm", Beats);
                startActivity(i);
                finish();

            }


            if (RedAvg != 0) {
                //Va incrementando la progressBar
                ProgP = inc++ / 34;
                ProgHeart.setProgress(ProgP);
            }

            processing.set(false);

        }
    };

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        @SuppressLint("LongLogTag")
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }

            camera.setParameters(parameters);
            camera.startPreview();
        }


        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea) result = size;
                }
            }
        }
        return result;
    }
}