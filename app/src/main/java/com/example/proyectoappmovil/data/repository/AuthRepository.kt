package com.example.proyectoappmovil.data.repository

import android.text.LoginFilter
import com.example.proyectoappmovil.data.model.Credential

class AuthRepository (
    private val validCredential: Credential = Credential.Admin
){
    fun login(username: String, password:String): Boolean{
        return username == validCredential.username && password==validCredential.password

    }

}
