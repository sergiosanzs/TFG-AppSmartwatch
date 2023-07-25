package com.example.tfg_smartwatch.Domain.Sensores.FrecuenciaCardiaca;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Esta clase se encarga de controlar el sensor de frecuencia cardiaca del dispositivo asi como de agregar listener para obtener sus valores.
 * Esta clase no tiene funcionalidad en la aplicacion, se deja estructurada como se comenta en la memoria para trabajos futuros.
 */
public class FrecuenciaCardiacaClass{

    private static SensorManager sensorManager;
    private static Sensor sensorFrecCardiaca;
    private static ListenerFrecuenciaCardiaca frecCardListener;


    /**
     * Constructor para crear una instancia de la clase
     * @param context Contexto de la aplicacion
     */
    public FrecuenciaCardiacaClass(Context context){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorFrecCardiaca = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        frecCardListener = new ListenerFrecuenciaCardiaca();
    }

    /**
     * Metodo que inicia la monitorizacion de los valores del sensor.
     */
    public static void startFrecCard(){
        frecCardListener.startContador();
        sensorManager.registerListener(frecCardListener, sensorFrecCardiaca, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Metodo que detiene la monitorizacion de los valores del sensor.
     */
    public static void stopFrecCard(){
        sensorManager.unregisterListener(frecCardListener);

    }
}
