package com.example.batallanaval

import android.widget.TableLayout
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TableRow
import kotlin.random.Random
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    lateinit var bienvenida : TextView
    var nombreJugador = "Nuevo Jugador"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bienvenida = findViewById<TextView>(R.id.saludoJugador)
    }

    fun btnCambiarNombre(v: View) {

        val dialogo = CambioDeNombre { nuevoNombre ->
            nombreJugador = nuevoNombre;
            bienvenida.setText("Bienvenido: " + nombreJugador);
        }

        dialogo.show(supportFragmentManager, "CambioDeNombre")
    }


}