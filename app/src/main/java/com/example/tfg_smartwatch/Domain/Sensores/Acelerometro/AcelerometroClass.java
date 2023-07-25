package com.example.tfg_smartwatch.Domain.Sensores.Acelerometro;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.example.tfg_smartwatch.Background;

/**
 * Esta clase se encarga de controlar el sensor acelerometro del dispositivo asi como de agregar listener para obtener sus valores.
 */
public class AcelerometroClass {

    private static SensorManager sensorManager;
    private static Sensor sensorAcelerometro;
    private static ListenerAcelerometro listenerAcelerometro ;
    private boolean iniciado = false;
    Background background;

    /**
     * Constructor para crear una instancia de la clase.
     * @param context Contexto de la aplicacion.
     */
    public AcelerometroClass(Background context){
        background = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorAcelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listenerAcelerometro = new ListenerAcelerometro(context);
        listenerAcelerometro.setTiempo();
    }

    /**
     * Metodo que establece el indicador de comprobaciones a false en el listener.
     */
    public void setEnComprobacionFalse(){
        listenerAcelerometro.setEnComprobacionFalse();
    }

    /**
     * Metodo que inicia la monitorizacion de valores del acelerometro.
     */
    public void startAcelerometro(){
        if(!iniciado){
            sensorManager.registerListener(listenerAcelerometro, sensorAcelerometro, SensorManager.SENSOR_DELAY_NORMAL);
            iniciado = true;
        }

    }

    /**
     * Metodo que detiene la monitorizacion del acelerometro.
     */
    public void stopAcelerometro(){
        if(iniciado){
            iniciado = false;
            sensorManager.unregisterListener(listenerAcelerometro);
        }
    }


    /**
     * Metodo que establece el numero de comprobaciones por defecto en el listener.
     */
    public void setComprobacionesDefecto(){
        listenerAcelerometro.setComprobacionesDefecto();
    }

    public void gestionarRetirada() {
        background.gestionaRetirada("ACELEROMETRO");
    }

}
