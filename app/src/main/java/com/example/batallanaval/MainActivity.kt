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
    lateinit var textoDificultad : TextView
    var nombreJugador = "Nuevo Jugador"
    var dificultad = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bienvenida = findViewById<TextView>(R.id.saludoJugador)
        textoDificultad = findViewById<TextView>(R.id.viewDificultad)
    }

    fun btnCambiarNombre(v: View) {
        val dialogoNombre = CambioDeNombre { nuevoNombre ->
            nombreJugador = nuevoNombre;
            bienvenida.setText("Bienvenido: " + nombreJugador);
        }

        dialogoNombre.show(supportFragmentManager, "CambioDeNombre")
    }

    fun btnCambiarDificultad(v: View) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Dificultad")
        builder.setMessage("Selecciona una dificultad de juego")
        builder.setPositiveButton("Fácil [6x6]") { dialog, which ->
            dificultad = 6
            textoDificultad.text = "Fácil [6x6]"
        }
        builder.setNeutralButton("Normal [7x7]") { dialog, which ->
            dificultad = 7
            textoDificultad.text = "Normal [7x7]"
        }
        builder.setNegativeButton("Difícil [8x8]") { dialog,which ->
            dificultad = 8
            textoDificultad.text = "Difícil [8x8]"
        }

        builder.show()
    }
}