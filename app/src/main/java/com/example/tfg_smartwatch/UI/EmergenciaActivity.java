package com.example.tfg_smartwatch.UI;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tfg_smartwatch.R;
import com.example.tfg_smartwatch.Background;

/**
 * Activity que representa la vista de confirmacion de la emergencia. Esta vista se compone de un texto y dos botones.
 */
public class EmergenciaActivity extends AppCompatActivity {
    Button botonAceptar;
    Button botonCancelar;
    Background background;

    private boolean clickAceptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boton_emergencia);
        botonAceptar = findViewById(R.id.buttonAceptar);
        botonCancelar = findViewById(R.id.buttonCancelar);
        background = Background.getInstance();
        clickAceptar = false;
        startContador();

    }

    /**
     * Metodo encargado de iniciar el contador y mostrar en el boton de cancelar. Si el contador llega a cero se cancela la actividad.
     */
    public void startContador(){
        CountDownTimer contador = new CountDownTimer(7000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int segundos = (int) (millisUntilFinished / 1000);
                String textoBoton = "Cancelar (" + segundos + ")";
                botonCancelar.setText(textoBoton);
            }

            @Override
            public void onFinish() {
                if (!clickAceptar) {
                    logicaCancelar();
                }
            }
        };
        contador.start();
    }

    /**
     * Metodo encargado de gestionar la pulsacion en el boton aceptar.
     * @param v Vista
     */
    public void clickOnBotonAceptar(View v) throws InterruptedException {
        clickAceptar = true;
        background.enviaNotificacionEmergencia();
        finish();
    }

    /**
     * Metodo encargado de gestionar la pulsacion en el boton cancelar.
     * @param v Vista
     */
    public void clickOnBotonCancelar(View v){
        logicaCancelar();
    }

    /**
     * Metodo encargado de finalizar la vista cuando se ejecuta la accion de cancelar.
     */
    public void logicaCancelar(){
        finish();
    }
}
