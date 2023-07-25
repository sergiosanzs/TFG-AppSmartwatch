package com.example.tfg_smartwatch.Domain.Sensores.Proximidad;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Esta clase se encarga de gestionar el sensor de proximidad y registrar los listeners para su monitorizacion.
 */
public class ProximidadClass {

    private static SensorManager sensorManager;
    private static Sensor sensorProximidad;
    private static ListenerProximidad listenerProximidad ;

    /**
     * Constructor para crear una instancia de la clase.
     * @param context Contexto de la aplicacion.
     */
    public ProximidadClass(Context context){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorProximidad = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        listenerProximidad = new ListenerProximidad();
    }

    /**
     * Metodo que inicia la monitorizacion del sensor de proximidad.
     */
    public void startProximidad(){
        sensorManager.registerListener(listenerProximidad, sensorProximidad, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * Metodo que detiene la monitorizacion del sensor de proximidad.
     */
    public void stopProximidad(){
        sensorManager.unregisterListener(listenerProximidad);
    }

}

