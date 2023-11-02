package com.example.whatsappclone.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.adapters.MensagensAdapter
import com.example.whatsappclone.databinding.ActivityMensagensBinding
import com.example.whatsappclone.model.Conversa
import com.example.whatsappclone.model.Mensagem
import com.example.whatsappclone.model.Usuario
import com.example.whatsappclone.utils.Constantes
import com.example.whatsappclone.utils.exibirmensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class MensagensActivity : AppCompatActivity() {

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val binding by lazy {
        ActivityMensagensBinding.inflate( layoutInflater )
    }
    private lateinit var listenerRegistration: ListenerRegistration
    private var dadosDestinatario: Usuario? = null
    private var dadosUsuarioRementente: Usuario? = null
    private lateinit var conversasAdapter: MensagensAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )
        recuperarDadosUsuarios()
        inicializarToolbar()
        inicializarEventoClique()
        inicializarRecyclerview()
        inicializarListeners()
    }

    private fun inicializarRecyclerview() {

        with(binding){
            conversasAdapter = MensagensAdapter()
            rvMensagens.adapter = conversasAdapter
            rvMensagens.layoutManager = LinearLayoutManager(applicationContext)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    private fun inicializarListeners() {

        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        val idUsuarioDestinatario = dadosDestinatario?.id
        if( idUsuarioRemetente != null && idUsuarioDestinatario != null ){

            listenerRegistration = firestore
                .collection(Constantes.MENSAGENS)
                .document( idUsuarioRemetente )
                .collection( idUsuarioDestinatario )
                .orderBy("data", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, erro ->

                    if( erro != null ){
                        exibirmensagem("Erro ao recuperar mensagens")
                    }

                    val listaMensagens = mutableListOf<Mensagem>()
                    val documentos = querySnapshot?.documents

                    documentos?.forEach { documentSnapshot ->
                        val mensagem = documentSnapshot.toObject( Mensagem::class.java )
                        if( mensagem != null ){
                            listaMensagens.add( mensagem )
                            Log.i("exibicao_mensagens", mensagem.mensagem)
                        }
                    }

                    //Lista
                    if( listaMensagens.isNotEmpty() ){
                        //Carregar os dados Adapter
                        conversasAdapter.adicionarLista( listaMensagens )
                    }

                }

        }

    }

    private fun inicializarEventoClique() {

        binding.fabEnviar.setOnClickListener {
            val mensagem = binding.editMensagem.text.toString()
            salvarMensagem( mensagem )
        }

    }

    private fun salvarMensagem( textoMensagem: String ) {
        if( textoMensagem.isNotEmpty() ){

            val idUsuarioRemetente = firebaseAuth.currentUser?.uid
            val idUsuarioDestinatario = dadosDestinatario?.id
            if( idUsuarioRemetente != null && idUsuarioDestinatario != null ){
                val mensagem = Mensagem(
                    idUsuarioRemetente, textoMensagem
                )

                //Salvar para o Remetente
                salvarMensagemFirestore(
                    idUsuarioRemetente, idUsuarioDestinatario, mensagem
                )
                //Jamilton -> Foto e nome Destinatario (ana)
                val conversaRemetente = Conversa(
                    idUsuarioRemetente, idUsuarioDestinatario,
                    dadosDestinatario!!.foto, dadosDestinatario!!.nome,
                    textoMensagem
                )
                salvarConversaFirestore( conversaRemetente )

                //Salvar mesma mensagem para o destinatario
                salvarMensagemFirestore(
                    idUsuarioDestinatario, idUsuarioRemetente, mensagem
                )
                //Ana -> Foto e nome Remente (jamilton)
                val conversaDestinatario = Conversa(
                    idUsuarioDestinatario, idUsuarioRemetente,
                    dadosUsuarioRementente!!.foto, dadosUsuarioRementente!!.nome,
                    textoMensagem
                )
                salvarConversaFirestore( conversaDestinatario )

                binding.editMensagem.setText("")

            }

        }
    }

    private fun salvarConversaFirestore(conversa: Conversa) {
        firestore
            .collection(Constantes.CONVERSAS)
            .document( conversa.idUsuarioRementente )
            .collection(Constantes.ULTIMAS_CONVERSAS)
            .document( conversa.idUsuarioDestinatario )
            .set( conversa )
            .addOnFailureListener {
                exibirmensagem("Erro ao salvar conversa")
            }

    }

    private fun salvarMensagemFirestore(
        idUsuarioRemetente: String,
        idUsuarioDestinatario: String,
        mensagem: Mensagem
    ) {

        firestore
            .collection(Constantes.MENSAGENS)
            .document( idUsuarioRemetente )
            .collection( idUsuarioDestinatario )
            .add( mensagem )
            .addOnFailureListener {
                exibirmensagem("Erro ao enviar mensagem")
            }

    }

    private fun inicializarToolbar() {
        val toolbar = binding.tbMensagens
        setSupportActionBar( toolbar )
        supportActionBar?.apply {
            title = ""
            if( dadosDestinatario != null ){
                binding.textNome.text = dadosDestinatario!!.nome
                Picasso.get()
                    .load(dadosDestinatario!!.foto)
                    .into( binding.imageFotoPerfil )
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun recuperarDadosUsuarios() {

        //Dados do usuário logado
        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        if( idUsuarioRemetente != null ){
            firestore
                .collection(Constantes.USUARIOS)
                .document( idUsuarioRemetente )
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    val usuario = documentSnapshot.toObject(Usuario::class.java)
                    if( usuario != null ){
                        dadosUsuarioRementente = usuario
                    }

                }
        }

        //Recuperando dados destinatário
        val extras = intent.extras
        if( extras != null ){
            dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable(
                    "dadosDestinatario",
                    Usuario::class.java
                )
            }else{
                extras.getParcelable(
                    "dadosDestinatario"
                )
            }
        }

    }
}