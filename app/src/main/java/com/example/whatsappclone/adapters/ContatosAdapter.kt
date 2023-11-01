package com.example.whatsappclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.whatsappclone.databinding.ItemContatosBinding
import com.example.whatsappclone.model.Usuario
import com.squareup.picasso.Picasso

class ContatosAdapter(
    private val onCLick: (Usuario) -> Unit
): Adapter<ContatosAdapter.ContatosViewHolder>() {

    private var listaContatos = emptyList<Usuario>()
    fun adicionarLista( lista: List<Usuario> ){
        listaContatos = lista
        notifyDataSetChanged()
    }

    inner class ContatosViewHolder(
        private val binding: ItemContatosBinding
    ) : ViewHolder( binding.root ){

        fun bind( usuario: Usuario ){

            binding.textContatoNome.text = usuario.nome
            Picasso.get()
                .load( usuario.foto )
                .into( binding.imageContatoFoto )

            //evento de click
            binding.clItemContato.setOnClickListener{
                onCLick(usuario)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatosViewHolder {

        val inflater = LayoutInflater.from( parent.context )
        val itemView = ItemContatosBinding.inflate(
            inflater, parent, false
        )
        return ContatosViewHolder( itemView )

    }

    override fun onBindViewHolder(holder: ContatosViewHolder, position: Int) {
        val usuario = listaContatos[position]
        holder.bind( usuario )
    }

    override fun getItemCount(): Int {
        return listaContatos.size
    }

}