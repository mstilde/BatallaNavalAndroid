package com.example.batallanaval

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class CambioDeNombre(val nombreCambiado: (String) -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val campoTexto = EditText(requireContext())

        return AlertDialog.Builder(requireContext())
            .setTitle("Cambiar nombre")
            .setMessage("Ingrese un nuevo nombre del jugador")
            .setView(campoTexto)
            .setPositiveButton("Aceptar") { dialog, which ->
                val nuevoNombre = campoTexto.text.toString()
                nombreCambiado(nuevoNombre)
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                dialog.cancel()
            }
            .create()
    }
}