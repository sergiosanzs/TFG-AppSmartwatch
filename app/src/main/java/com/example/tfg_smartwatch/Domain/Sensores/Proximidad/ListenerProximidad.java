package com.example.tfg_smartwatch.Domain.Sensores.Proximidad;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.example.tfg_smartwatch.Data.Persistencia.Servidor.Configuracion;
import com.example.tfg_smartwatch.Background;

/**
 * Clase que representa el listener que detecta cambios en el sensor de proximidad
 */
public class ListenerProximidad implements SensorEventListener {
    Configuracion configuracion = Configuracion.getInstance();

    /**
     * Metodo que detecta un cambio en los valores del sensor. Cuando este se detecta se realiza la comprobacion correspondiente.
     * Si la distancia medida es mayor que el umbral establecido se detecta una retirada.
     * @param sensorEvent the {@link android.hardware.SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float distancia = sensorEvent.values[0];
        if (distancia > configuracion.getUmbralValoresProximidad()){
            Background activityPrincipal = Background.getInstance();
            activityPrincipal.gestionaRetirada("PROXIMIDAD");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

