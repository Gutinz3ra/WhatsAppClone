package com.example.whatsappclone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappclone.databinding.ActivityPerfilBinding

class PerfilActivity : AppCompatActivity() {


    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToolbar()

    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbarPerfil.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Editar Perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }

}