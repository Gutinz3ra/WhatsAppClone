package com.example.whatsappclone.utils

import android.app.Activity
import android.widget.Toast

fun Activity.exibirmensagem(mensagem:String){
    Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()

}