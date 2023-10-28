package com.example.whatsappclone

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.example.whatsappclone.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToolbar()
        inicializarNavegacaoAbas()
    }

    private fun inicializarNavegacaoAbas() {
        val  tabLayout = binding.tabLayoutPrincipal
        val  viewPager = binding.viewPagerPrincipal

        //adapter


        TabLayoutMediator(tabLayout,viewPager) { aba, posicao ->
            aba.text = "Conversas"
        }
    }


    private fun inicializarToolbar() {
        val toolbar = binding.includeMainToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "WhatsApp"
        }
        addMenuProvider(
         object : MenuProvider{
             override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                 menuInflater.inflate(R.menu.menu_principal, menu)
             }

             override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                 when(menuItem.itemId){
                     R.id.item_perfil -> {
                         startActivity(
                             Intent(applicationContext, PerfilActivity::class.java)
                         )
                     }
                     R.id.item_sair -> {
                        deslogarUsuario()
                     }
                 }
                 return true
             }

         }
        )
    }

    private fun deslogarUsuario() {
        AlertDialog.Builder(this)
            .setTitle("Deslogar")
            .setMessage("Deseja realmente sair")
            .setNegativeButton("Cancelar"){dialog, posicao ->}
            .setPositiveButton("Sim"){dialog, posicao ->
                FirebaseAuth.getInstance().signOut()
                startActivity(
                    Intent(applicationContext, LoginActivity::class.java )
                )
            }
            .create()
            .show()
    }


}