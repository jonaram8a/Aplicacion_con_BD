package com.example.proyecto.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VideojuegoDao {
    @Query("SELECT * FROM videojuegos ORDER BY id ASC")
    fun getAllVideojuegos(): Flow<List<VideojuegoEntity>>
    
    @Query("SELECT * FROM videojuegos WHERE id = :id")
    suspend fun getVideojuegoById(id: Int): VideojuegoEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideojuego(videojuego: VideojuegoEntity): Long
    
    @Update
    suspend fun updateVideojuego(videojuego: VideojuegoEntity)
    
    @Query("DELETE FROM videojuegos WHERE id = :id")
    suspend fun deleteVideojuego(id: Int)
    
    @Query("DELETE FROM videojuegos")
    suspend fun deleteAllVideojuegos()
    
    @Query("SELECT COUNT(*) FROM videojuegos")
    suspend fun getCount(): Int
}

