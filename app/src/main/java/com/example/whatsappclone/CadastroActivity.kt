package com.example.whatsappclone

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappclone.databinding.ActivityCadastroBinding
import com.example.whatsappclone.model.Usuario
import com.example.whatsappclone.utils.exibirmensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String

    //Firebase Auth
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        inicializarToolbar()
        inicializarEventosClique()
    }

    private fun inicializarEventosClique(){
        binding.btnCadastrar.setOnClickListener {
            if (validarCampos()){
                cadastrarUsuario(nome, email, senha)
            }
        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {

        firebaseAuth.createUserWithEmailAndPassword(
            email, senha
        ).addOnCompleteListener{ resultado ->

            if (resultado.isSuccessful){

                //SAVED DATA FIRESTORE

                val idUsuario = resultado.result.user?.uid
                if (idUsuario != null){
                    val usuario = Usuario(
                        idUsuario, nome, email
                    )
                    salvarUsuarioFirestore(usuario)
                }
            }

        }.addOnFailureListener { erro ->
            try {
                throw erro

            } catch (erroSenhaFraca: FirebaseAuthWeakPasswordException) {
                erroSenhaFraca.printStackTrace()
                exibirmensagem("Senha fraca!")

            } catch (erroUsuarioExistente: FirebaseAuthUserCollisionException) {
                erroUsuarioExistente.printStackTrace()
                exibirmensagem("Este Email já existe!")

            } catch (erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException) {
                erroCredenciaisInvalidas.printStackTrace()
                exibirmensagem("Email Inválido, digite um outro Email!")
            }
        }
    }

    private fun salvarUsuarioFirestore(usuario: Usuario) {

        firestore
            .collection("usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener {
                exibirmensagem("Sucesso ao fazer o seu cadastro")
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }.addOnFailureListener {
                exibirmensagem("Erro ao fazer o seu cadastro")

            }


    }

    private fun validarCampos(): Boolean{

        nome = binding.editNome.text.toString()
        email = binding.editEmail.text.toString()
        senha = binding.editSenha.text.toString()


        if (nome.isNotEmpty()){

            //Necessario este codigo para se for colocado texto mesmo assim continuará o aviso

            binding.textInputName.error = null

            if (email.isNotEmpty()){
                binding.textInputEmail.error = null

                if (senha.isNotEmpty()){
                    binding.textInputSenha.error = null
                    return true
                }else{
                    binding.textInputSenha.error = "Preencha a sua Senha!"
                    return false
                }
            }else{
                binding.textInputEmail.error = "Preencha o seu Email!"
                return false
            }
            return true
        }else{
            binding.textInputName.error = "Preencha o seu nome!"
            return false
        }
    }



    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}