package com.example.proyecto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VideojuegoEntity::class, UsuarioEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videojuegoDao(): VideojuegoDao
    abstract fun usuarioDao(): UsuarioDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "videojuegos_database"
                )
                .fallbackToDestructiveMigration() // Para desarrollo - elimina en producción
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

