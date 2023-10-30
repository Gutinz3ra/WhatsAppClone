package com.example.whatsappclone

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.whatsappclone.databinding.ActivityPerfilBinding
import com.example.whatsappclone.utils.exibirmensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
class PerfilActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPerfilBinding.inflate( layoutInflater )
    }
    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val gerenciadorGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){ uri ->
        if( uri != null ){
            binding.imagePerfil.setImageURI( uri )
            uploadImagemStorage( uri )
        }else{
            exibirmensagem("Nenhuma imagem selecionada")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )
        inicializarToolbar()
        solicitarPermissoes()
        inicializarEventosClique()
    }

    override fun onStart() {
        super.onStart()
        recuperarDadosIniciaisUsuarios()
    }

    private fun recuperarDadosIniciaisUsuarios() {
        val idUsuario = firebaseAuth.currentUser?.uid
        if( idUsuario != null ){

            firestore
                .collection("usuarios")
                .document( idUsuario )
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    val dadosUsuarios = documentSnapshot.data
                    if( dadosUsuarios != null ){

                        val nome = dadosUsuarios["nome"] as String
                        val foto = dadosUsuarios["foto"] as String

                        binding.editNomePerfil.setText( nome )
                        if( foto.isNotEmpty() ){
                            Picasso.get()
                                .load( foto )
                                .into( binding.imagePerfil )
                        }

                    }

                }

        }

    }

    private fun uploadImagemStorage(uri: Uri) {

        // fotos -> usuarios -> idUsuario -> perfil.jpg
        val idUsuario = firebaseAuth.currentUser?.uid
        if( idUsuario != null ){

            storage
                .getReference("fotos")
                .child("usuarios")
                .child(idUsuario)
                .child("perfil.jpg")
                .putFile( uri )
                .addOnSuccessListener { task ->

                    exibirmensagem("Sucesso ao fazer upload da imagem")
                    task.metadata
                        ?.reference
                        ?.downloadUrl
                        ?.addOnSuccessListener { url ->

                            val dados = mapOf(
                                "foto" to url.toString()
                            )
                            atualizarDadosPerfil( idUsuario, dados )

                        }

                }.addOnFailureListener {
                    exibirmensagem("Erro ao fazer upload da imagem")
                }

        }


    }

    private fun atualizarDadosPerfil(idUsuario: String, dados: Map<String, String>) {

        firestore
            .collection("usuarios")
            .document( idUsuario )
            .update( dados )
            .addOnSuccessListener {
                exibirmensagem("Sucesso ao atualizar perfil!")
            }
            .addOnFailureListener {
                exibirmensagem("Erro ao atualizar perfil")
            }

    }

    private fun inicializarEventosClique() {

        binding.fabSelecionar.setOnClickListener {
            if( temPermissaoGaleria ){
                gerenciadorGaleria.launch("image/*")
            }else{
                exibirmensagem("Não tem permissão para acessar galeria")
                solicitarPermissoes()
            }
        }

        binding.btnAtualizarPerfil.setOnClickListener {

            val nomeUsuario = binding.editNomePerfil.text.toString()
            if( nomeUsuario.isNotEmpty() ){

                val idUsuario = firebaseAuth.currentUser?.uid
                if( idUsuario != null ){
                    val dados = mapOf(
                        "nome" to nomeUsuario
                    )
                    atualizarDadosPerfil( idUsuario, dados )
                }

            }else{
                exibirmensagem("Preencha o nome para atualizar")
            }

        }

    }

    private fun solicitarPermissoes() {

        //Verifico se usuário já tem permissão
        temPermissaoCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        temPermissaoGaleria = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        //LISTA DE PERMISSÕES NEGADAS
        val listaPermissoesNegadas = mutableListOf<String>()
        if( !temPermissaoCamera )
            listaPermissoesNegadas.add( Manifest.permission.CAMERA )
        if( !temPermissaoGaleria )
            listaPermissoesNegadas.add( Manifest.permission.READ_MEDIA_IMAGES )

        if( listaPermissoesNegadas.isNotEmpty() ){

            //Solicitar multiplas permissões
            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ){ permissoes ->

                temPermissaoCamera = permissoes[Manifest.permission.CAMERA]
                    ?: temPermissaoCamera

                temPermissaoGaleria = permissoes[Manifest.permission.READ_MEDIA_IMAGES]
                    ?: temPermissaoGaleria

            }
            gerenciadorPermissoes.launch( listaPermissoesNegadas.toTypedArray() )

        }

    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbarPerfil.tbPrincipal
        setSupportActionBar( toolbar )
        supportActionBar?.apply {
            title = "Editar perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }

}