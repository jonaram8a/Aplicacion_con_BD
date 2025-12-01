package com.example.proyecto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.UsuarioDao
import com.example.proyecto.data.UsuarioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuarioViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {
    
    private val _loginResult = MutableStateFlow<UsuarioEntity?>(null)
    val loginResult: StateFlow<UsuarioEntity?> = _loginResult
    
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = usuarioDao.login(username, password)
        }
    }
    
    private val _registroResult = MutableStateFlow<Boolean?>(null)
    val registroResult: StateFlow<Boolean?> = _registroResult
    
    fun registrar(username: String, password: String) {
        viewModelScope.launch {
            val usuarioExistente = usuarioDao.getUsuarioByUsername(username)
            if (usuarioExistente == null) {
                val nuevoUsuario = UsuarioEntity(
                    username = username,
                    password = password
                )
                usuarioDao.insertUsuario(nuevoUsuario)
                _registroResult.value = true
            } else {
                _registroResult.value = false
            }
        }
    }
    
    suspend fun getUsuarioById(id: Int): UsuarioEntity? {
        return usuarioDao.getUsuarioById(id)
    }
}

class UsuarioViewModelFactory(private val usuarioDao: UsuarioDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsuarioViewModel(usuarioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

