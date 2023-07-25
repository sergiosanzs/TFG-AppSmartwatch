package com.example.tfg_smartwatch.Data.Persistencia.Servidor;

import com.google.android.gms.location.Priority;

/**
 * Clase encargada de realizar la configuración de la app.
 * La app va a tener dos modos de monitorizar la ubicacion:
 * Modo 1: Alta prioridad, el tiempo que se establece en el intervalo es el que se cumple en la monitorizacon.
 * Modo 2: Ahorro de bateria, se prioriza el ahorro de batería pero se pierde precision en la ubicacion y en el intervalo de monitorizacion.
 * 15 min = 50-70 min
 */
public class Configuracion {
    //Variables con valores por defecto
    public static String URL_SERVIDOR = "https://trackcare.app.bluece.eu/api/positions";
    public static String TELEFONO = "638749180";
    public static long INTERVALO_MONITORIZACION_UBICACION = 120000; //En milisegundos
    public static int MODO_USO = 1; // 1 = Modo alta prioridad se respeta el intervalo. 2= Modo ahorro bateria, no se respeta el intervalo

    public static long INTERVALO_COMPROBACION_ACELEROMETRO = 4000; //En milisegundos
    public static int COMPROBACIONES_DEFECTO_ACELEROMETRO = 1;
    public static float UMBRAL_VALORES_ACELEROMETRO = 0.2f;

    public static float UMBRAL_VALORES_PROXIMIDAD = 3.0f;


    private static Configuracion instance;
    //Constructor privado que evita la creación de instancias directas.
    private Configuracion(){

    }

    /**
     * Clase encargada de descargar la configuracion del usuario correspondiente del servidor.
     * No implementada ya que el servidor no tiene esta capacidad cuando se desarrolla este proyecto.
     */
    public void descargaConfiguracion(){

    }

    public static synchronized Configuracion getInstance(){
        if(instance == null){
            instance = new Configuracion();
        }
        return instance;
    }

    public String getUrlServidor() {
        return URL_SERVIDOR;
    }

    private void setUrlServidor(String urlServidor) {
        URL_SERVIDOR = urlServidor;
    }

    public String getTELEFONO() {
        return TELEFONO;
    }

    private void setTELEFONO(String TELEFONO) {
        Configuracion.TELEFONO = TELEFONO;
    }

    public long getIntervaloMonitorizacionUbicacion() {
        return INTERVALO_MONITORIZACION_UBICACION;
    }

    private void setIntervaloMonitorizacionUbicacion(long intervaloMonitorizacionUbicacion) {
        INTERVALO_MONITORIZACION_UBICACION = intervaloMonitorizacionUbicacion;
    }

    public long getIntervaloComprobacionAcelerometro() {
        return INTERVALO_COMPROBACION_ACELEROMETRO;
    }

    private void setIntervaloComprobacionAcelerometro(long intervaloComprobacionAcelerometro) {
        INTERVALO_COMPROBACION_ACELEROMETRO = intervaloComprobacionAcelerometro;
    }

    public int getComprobacionesDefectoAcelerometro() {
        return COMPROBACIONES_DEFECTO_ACELEROMETRO;
    }

    private void setComprobacionesDefectoAcelerometro(int comprobacionesDefectoAcelerometro) {
        COMPROBACIONES_DEFECTO_ACELEROMETRO = comprobacionesDefectoAcelerometro;
    }

    public float getUmbralValoresAcelerometro() {
        return UMBRAL_VALORES_ACELEROMETRO;
    }

    private void setUmbralValoresAcelerometro(float umbralValoresAcelerometro) {
        UMBRAL_VALORES_ACELEROMETRO = umbralValoresAcelerometro;
    }

    public float getUmbralValoresProximidad() {
        return UMBRAL_VALORES_PROXIMIDAD;
    }

    private void setUmbralValoresProximidad(float umbralValoresProximidad) {
        UMBRAL_VALORES_PROXIMIDAD = umbralValoresProximidad;
    }

    public int getPrioridad(){
        if (MODO_USO == 1){
            return Priority.PRIORITY_HIGH_ACCURACY;
        } else {
            return Priority.PRIORITY_BALANCED_POWER_ACCURACY;
        }
    }
}
