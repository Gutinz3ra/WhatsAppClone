package com.example.whatsappclone.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappclone.databinding.ActivityLoginBinding
import com.example.whatsappclone.utils.exibirmensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var email: String
    private lateinit var senha: String


    //Firebase Auth
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarEventosClique()
        FirebaseAuth.getInstance().signOut()
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        val usuarioAtual = firebaseAuth.currentUser
        if (usuarioAtual != null){
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }

    }

    private fun inicializarEventosClique() {
        binding.TextCadastro.setOnClickListener {
            startActivity(
                Intent(this, CadastroActivity::class.java)
            )
        }
        binding.btnLogar.setOnClickListener {
            if (validarCampos()) {
                logarUsuario()
            }
        }
    }

    private fun logarUsuario() {
        firebaseAuth.signInWithEmailAndPassword(
            email, senha
        ).addOnSuccessListener {
            exibirmensagem("Logado Com sucesso")
            startActivity(
                Intent(
                    this, MainActivity::class.java
                )
            )
        }
            .addOnFailureListener { erro ->
                try {
                    throw erro
                } catch (erroUsuarioInvalido: FirebaseAuthInvalidUserException) {
                    erroUsuarioInvalido.printStackTrace()
                    exibirmensagem("Email não cadastrado!")
                } catch (erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException) {
                    erroCredenciaisInvalidas.printStackTrace()
                    exibirmensagem("Email ou senha incorretos!")
                } catch (excecaoNaoIdentificada: Exception) {
                    excecaoNaoIdentificada.printStackTrace()
                    exibirmensagem("Erro no login: " + excecaoNaoIdentificada.message)
                }
            }

    }



    private fun validarCampos(): Boolean {
        email = binding.editLoginEmail.text.toString()
        senha = binding.editLoginSenha.text.toString()

        if (email.isNotEmpty()) {
            binding.LoginEmail.error = null
            if (senha.isNotEmpty()) {
                binding.LoginSenha.error = null
                return true
            } else {
                binding.LoginSenha.error = "Preencha a senha!"
                return false
            }
        } else {
            binding.LoginEmail.error = "Preencha o email!"
            return false
        }
    }



}