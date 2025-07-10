package com.example.batallanaval

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class MainActivity : AppCompatActivity() {

    // Componentes de la interfaz
    private lateinit var inputJugador: EditText
    private lateinit var spinnerDificultad: Spinner
    private lateinit var botonIniciar: Button
    private lateinit var botonAyuda: Button
    private lateinit var botonRanking: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Asignamos vistas del XML
        inputJugador = findViewById(R.id.nombre_jugador)
        spinnerDificultad = findViewById(R.id.dificultad)
        botonIniciar = findViewById(R.id.btn_jugar)
        botonAyuda = findViewById(R.id.btn_Ayuda)
        botonRanking=findViewById(R.id.btn_ranking)

        // Cargamos el array de dificultades desde strings.xml
        val opciones = resources.getStringArray(R.array.select)
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDificultad.adapter = adaptador

        // Acción al hacer clic en "Iniciar Juego"
        botonIniciar.setOnClickListener {
            val nombre = inputJugador.text.toString().trim()
            if (nombre.isEmpty()) {
                Toast.makeText(this, getString(R.string.sinNombre), Toast.LENGTH_SHORT).show()
            } else {
                iniciarJuego()
            }
        }

        // Acción al hacer clic en "Ayuda"
        botonAyuda.setOnClickListener {
            mostrarAyuda()
        }
        botonRanking.setOnClickListener {
            botonRanking()
        }
    }

    // Lanza la pantalla de ayuda
    private fun mostrarAyuda() {
        val i = Intent(this, ActivityMenuAyuda::class.java)
        startActivity(i)
    }

    // Determina la dificultad seleccionada
    private fun seleccionarDificultad(): Int {
        return when (spinnerDificultad.selectedItemPosition) {
            0 -> 6
            1 -> 8
            2 -> 10
            else -> 6
        }
    }

    // Seleccionar tiempo
    private fun obtenerTiempoLimite(): Int {
        return when (seleccionarDificultad()) {
            6 -> 20
            8 -> 25
            10 -> 30
            else -> 20
        }
    }

    // Devuelve el nombre del jugador (o uno por defecto)
    private fun obtenerNombreJugador(): String {
        val nombre = inputJugador.text.toString().trim()
        return nombre
    }

    // Lanza la pantalla de juego con los datos elegidos
    private fun iniciarJuego() {
        val intent = Intent(this, ActivityJuego::class.java)
        intent.putExtra("dato1", seleccionarDificultad())
        intent.putExtra("dato2", obtenerNombreJugador())
        intent.putExtra("datoTiempo", obtenerTiempoLimite())
        startActivity(intent)
    }

    private fun botonRanking(){
        val i = Intent(this, ActivityRanking::class.java)
        startActivity(i)
    }
}

