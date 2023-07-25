package com.example.tfg_smartwatch.Data.Persistencia.Local;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.tfg_smartwatch.Data.Persistencia.MensajeJSON;
import com.example.tfg_smartwatch.Data.Persistencia.MensajeJsonDAO;

/**
 * Clase que representa la base de datos local del dispositivo utilizando Room.
 */
@Database(entities = {MensajeJSON.class}, version = 2)
public abstract class DatabaseLocal extends RoomDatabase {

    private static DatabaseLocal INSTANCE;
    public abstract MensajeJsonDAO mensajeJsonDAO();

    /**
     * Metodo para obtener una instancia unica de la clase en toda la aplicacion.
     * @param context Contexto de la aplicacion
     * @return instancia de la base de datos de la aplicacion
     */
    public static synchronized DatabaseLocal getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DatabaseLocal.class, "tfgsmartw")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    /**
     * Metodo encargado de eliminar la instancia actual.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }


    /**
     * Metodo encargado de eliminar el contenido de todas las tablas de la base de datos.
     */
    @Override
    public void clearAllTables() {

    }


    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(@NonNull DatabaseConfiguration databaseConfiguration) {
        return null;
    }
}
