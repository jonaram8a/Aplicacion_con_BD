package com.example.proyecto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.VideojuegoDao
import com.example.proyecto.data.VideojuegoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VideojuegoViewModel(private val videojuegoDao: VideojuegoDao) : ViewModel() {
    
    val videojuegos: StateFlow<List<VideojuegoEntity>> = videojuegoDao.getAllVideojuegos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun agregarVideojuego(titulo: String, descripcion: String, posterResId: Int = 0, bannerResId: Int = 0) {
        viewModelScope.launch {
            val nuevoVideojuego = VideojuegoEntity(
                titulo = titulo,
                descripcion = descripcion,
                posterResId = posterResId,
                bannerResId = bannerResId
            )
            videojuegoDao.insertVideojuego(nuevoVideojuego)
        }
    }
    
    fun actualizarVideojuego(videojuego: VideojuegoEntity) {
        viewModelScope.launch {
            videojuegoDao.updateVideojuego(videojuego)
        }
    }
    
    fun eliminarVideojuego(id: Int) {
        viewModelScope.launch {
            videojuegoDao.deleteVideojuego(id)
        }
    }
}

class VideojuegoViewModelFactory(private val videojuegoDao: VideojuegoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideojuegoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VideojuegoViewModel(videojuegoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

