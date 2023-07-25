package com.example.tfg_smartwatch.Data.Persistencia.Servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Clase utilizada para establecer conexion con la API del servidor realizando solicitudes HTTP.
 */
public class APIServidor {

    /**
     * Metodo de que realiza una solicitud HTTP de tipo POST al servidor.
     * @param json JSON que se envia al servidor
     */
    public void post(String json){
        try{
            Configuracion configuracion = Configuracion.getInstance();
            URL url = new URL(configuracion.getUrlServidor());
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Content-Type", "application/json");
            conexion.setRequestProperty("Accept", "application/json");
            conexion.setDoOutput(true);
            conexion.setDoInput(true);


            //Escribe json en el cuerpo de la solicitud
            OutputStreamWriter writer = new OutputStreamWriter(conexion.getOutputStream());
            writer.write(json);
            writer.flush();

            //Leer respuesta del servidor
            BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String response;
             while((response = reader.readLine()) != null){
                 stringBuilder.append(response.trim());
             }
             //Cerrar conexiones
             writer.close();
             reader.close();
             conexion.disconnect();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
