package com.example.proyecto.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videojuegos")
data class VideojuegoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val posterResId: Int = 0, // ID del recurso drawable
    val bannerResId: Int = 0  // ID del recurso drawable
)

