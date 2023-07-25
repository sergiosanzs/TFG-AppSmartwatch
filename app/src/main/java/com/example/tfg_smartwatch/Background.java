package com.example.tfg_smartwatch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.tfg_smartwatch.Data.Persistencia.Servidor.Configuracion;
import com.example.tfg_smartwatch.Domain.Sistema.PhoneClass;
import com.example.tfg_smartwatch.Domain.Sistema.ReceptorEmergencia;
import com.example.tfg_smartwatch.UI.EmergenciaActivity;
import com.example.tfg_smartwatch.Domain.Sensores.Acelerometro.AcelerometroClass;
import com.example.tfg_smartwatch.Domain.Sensores.Proximidad.ProximidadClass;
import com.example.tfg_smartwatch.Domain.Sensores.Ubicacion.UbicacionClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.sql.SQLException;

/**
 * Clase encargada de controlar toda la logica de la aplicacion. Se ejecuta en segundo plano.
 */
public class Background extends Service {
    private NotificationManager notificationManager;
    private static Background instance;
    private UbicacionClass monitorUbicacion;
    private boolean tieneProximidad = false;
    LocationRequest monitorizadorUbicacion;
    AcelerometroClass monitorAcelerometro;
    ProximidadClass monitorProximidad;
    private static final int ID = 1;
    private static final String CANAL = "canal_notificaciones";

    private Configuracion configuracion;

    /**
     * Metodo que se ejecuta al iniciar la clase. Se encarga de crear una notificacion persistente donde se encuentra el boton de emergencia.
     * @param intent The Intent supplied to {@link android.content.Context#startService},
     * as given.  This may be null if the service is being restarted after
     * its process has gone away, and it had previously returned anything
     * except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to
     * start.  Use with {@link #stopSelfResult(int)}.
     *
     * @return int
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        configuracion = Configuracion.getInstance();
        notificationManager = getSystemService(NotificationManager.class);
        Notification notification = crearNotification();
        if(!gpsIncorporado()){
            Log.d("AVISO", "No se ha detectado GPS, se usará otro proveedor de ubicación.");
        }
        startForeground(ID, notification);

        instance = this;
        if (intent != null){
            tieneProximidad = intent.getBooleanExtra("proximidad", false);
            try {
                startBg();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Metodo que se encarga de iniciar la monitorizacion de todos los sensores y receptores.
     * @throws SQLException Excepcion
     */
    public void startBg() throws SQLException {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.
                Builder().permitNetwork().build());
        monitorizadorUbicacion = new LocationRequest.Builder(configuracion.getIntervaloMonitorizacionUbicacion())
                .setPriority(configuracion.getPrioridad())
                .build();

        FusedLocationProviderClient ubicacion = LocationServices.getFusedLocationProviderClient(this);

        monitorUbicacion = new UbicacionClass(this, ubicacion, monitorizadorUbicacion);
        monitorUbicacion.monitorizaUbicacion();
        monitorAcelerometro = new AcelerometroClass(this);
        if(tieneProximidad){
            monitorProximidad = new ProximidadClass(this);
            monitorProximidad.startProximidad();
        }else{
            monitorAcelerometro.startAcelerometro();
        }
    }

    /**
     * Metodo encargado de crear la notificacion persistente.
     * @return Notificacion
     */
    private Notification crearNotification() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notificacion);
        Intent actionIntent = new Intent(this, ReceptorEmergencia.class);
        actionIntent.setAction("com.example.tfg_smartwatch.ACTION");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action botonPersonalizado = new NotificationCompat.Action.Builder(
                R.drawable.ic_launcher_foreground,
                "EMERGENCIA",
                pendingIntent
        ).build();
        // Crear el canal de notificación
        String channelId = "canal_notificaciones";
        String channelName = "Canal de Notificaciones";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CANAL)
                .setContentTitle("TFG_Smartwatch")
                .setContentText("App segundo plano")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("ID: " + Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID)))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setCustomContentView(remoteViews)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .addAction(botonPersonalizado)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);

        builder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE);
        return builder.build();
    }

    /**
     * Consulta que comprueba si se tiene GPS incorporado.
     * @return true o false si incorpora gps o no
     */
    private boolean gpsIncorporado(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    /**
     * Metodo para gestionar la retirada en funcion de los sensores que se usen para detectar la misma.
     * @param sensor Tipo de sensor que ha llamado al metodo.
     */
    public void gestionaRetirada(String sensor){
        if(tieneProximidad){
            switch (sensor){
                case "PROXIMIDAD":
                    monitorAcelerometro.startAcelerometro();
                    break;
                case "ACELEROMETRO":
                    enviaNotificacionRetirada();
                    setComprobacionesDefecto();
                    break;
                default:
                    break;
            }
        }else{
            switch (sensor){
                case "ACELEROMETRO":

                    enviaNotificacionRetirada();
                    setComprobacionesDefecto();
                    break;
                case "LUZAMBIENTE":
                    setComprobacionesDefecto();
                    enviaNotificacionRetirada();
                    break;
                case "UBICACION":
                    monitorAcelerometro = new AcelerometroClass(this);
                    monitorAcelerometro.startAcelerometro();
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * Metodo que establece el valor de las variables de comprobacion en los valores por defecto.
     */
    public void  setComprobacionesDefecto(){
        monitorUbicacion.setRetiradoFalse();
        monitorAcelerometro.stopAcelerometro();
        monitorAcelerometro.setComprobacionesDefecto();
        monitorAcelerometro.setEnComprobacionFalse();
        monitorAcelerometro.startAcelerometro();
    }


    /**
     * Metodo para enviar la notificacion de retirada
     */
    public void enviaNotificacionRetirada(){
        monitorUbicacion.enviaNotificacion("TAKEOFF");
    }

    /**
     * Metodo encargado de mostrar la vista de confirmacion de emergencia.
     * @param context Contexto de la aplicacion
     */
    public void mostrarMensajeEmergencia(Context context){
        Intent intent = new Intent(context, EmergenciaActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Metodo encargado de notificar el envio de notificacion de emergencia y la correspondiente llamada.
     */
    public void enviaNotificacionEmergencia() throws InterruptedException {
        monitorAcelerometro.stopAcelerometro();
        monitorUbicacion.enviaNotificacion("EMERGENCY");
        PhoneClass call = new PhoneClass(this);
        call.llamar(configuracion.getTELEFONO());
        setComprobacionesDefecto();
    }

    /**
     * Clase utilizada para comprobar si hay conexion a internet disponible
     * LLamarla de la siguiente manera isNetOk(MainActivity.this)
     * @return Boolean true/false si la red esta disponible.
     */
    public boolean isNetOk(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     * Consulta que retorna el porcentaje de bateria del dispositivo.
     * @param context Contexto de la actividad que realiza la llamada.
     * @return Bateria Entero que muestra el porcentaje de bateria.
     */
    public int getBattery(Context context){
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent estadoBateria = context.registerReceiver(null, intentFilter);
        int level = estadoBateria != null ? estadoBateria.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = estadoBateria != null ? estadoBateria.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        float batteryPct = level / (float) scale;
        return (int) (batteryPct * 100);
    }

    /**
     * Metodo que devuelve la instancia de la clase.
     * @return Instancia de la clase.
     */
    public static Background getInstance(){
        return instance;
    }

}
