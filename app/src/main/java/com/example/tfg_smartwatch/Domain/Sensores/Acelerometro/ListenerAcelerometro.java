package com.example.tfg_smartwatch.Domain.Sensores.Acelerometro;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.example.tfg_smartwatch.Data.Persistencia.Servidor.Configuracion;
import com.example.tfg_smartwatch.Background;

/**
 * Clase que representa el listener que detecta cambios en el sensor acelerometro
 */
public class ListenerAcelerometro implements SensorEventListener {
    private int comprobaciones;
    private float[] valoresAnteriores = new float[3];
    private long tiempoUltimo;
    private boolean enComprobacion;
    private final Background context;
    private final Configuracion configuracion;

    /**
     * Constructor para crear una instancia de la clase.
     * @param context Contexto de la aplicacion
     */
    public ListenerAcelerometro(Background context) {
        this.context = context;
        configuracion = Configuracion.getInstance();
        comprobaciones = configuracion.getComprobacionesDefectoAcelerometro();
        enComprobacion=false;
    }

    /**
     * Metodo que establece el indicador de estar en comprobacion a false.
     */
    public void setEnComprobacionFalse(){
        enComprobacion=false;
    }

    /**
     * Metodo que detecta un cambio en los valores del sensor. Cuando este se detecta se realiza la comprobacion correspondiente.
     * Si no es significativamente diferente con el anterior medido comprandolo con un umbral se detecta la retirada del dispositivo.
     * @param sensorEvent the {@link android.hardware.SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] valoresActuales = sensorEvent.values.clone();
        long tiempoAhora = System.currentTimeMillis();

        if((tiempoAhora-tiempoUltimo) > configuracion.getIntervaloComprobacionAcelerometro()){
            if(!enComprobacion){
                float calculo = Math.abs(valoresActuales[0]-valoresAnteriores[0]) + Math.abs(valoresActuales[1]-valoresAnteriores[1]) +
                        Math.abs(valoresActuales[2]-valoresAnteriores[2]);

                if(calculo < configuracion.getUmbralValoresAcelerometro()){
                    if (comprobaciones == 0){
                        enComprobacion=true;
                        context.gestionaRetirada("ACELEROMETRO");
                    }
                    comprobaciones --;
                }else if(valoresAnteriores[0] > 0 || valoresAnteriores[0] < 0){
                    context.setComprobacionesDefecto();
                }
            }
            valoresAnteriores = valoresActuales.clone();
            tiempoUltimo = tiempoAhora;
        }
    }

    /**
     * Metodo que establece el numero de comprobaciones por las de defecto configuradas.
     */
    public void setComprobacionesDefecto(){
        comprobaciones = configuracion.getComprobacionesDefectoAcelerometro();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /**
     * Metodo que establece el tiempo actual como el ultimo tiempo registrado para la siguiente iteracion.
     */
    public void setTiempo(){
        tiempoUltimo = System.currentTimeMillis();
    }
}
