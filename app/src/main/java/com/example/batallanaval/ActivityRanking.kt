package com.example.batallanaval

import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONArray
import java.io.File

class ActivityRanking : AppCompatActivity() {
    private lateinit var rankingLista: List<TextView>
    private lateinit var btn6: Button
    private lateinit var btn8: Button
    private lateinit var btn10: Button
    private lateinit var btn_atras : Button
    private lateinit var textoDificultad : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ranking)
        rankingLista = listOf(
            findViewById(R.id.ranking1),
            findViewById(R.id.ranking2),
            findViewById(R.id.ranking3),
            findViewById(R.id.ranking4),
            findViewById(R.id.ranking5)
        )
        btn6 = findViewById(R.id.btn1)
        btn8 = findViewById(R.id.btn2)
        btn10 = findViewById(R.id.btn3)
        btn_atras = findViewById(R.id.btnAtrasRanking)
        textoDificultad = findViewById(R.id.dificultadRanking)

        btn6.setOnClickListener {
            mostrarRanking(6)
        }
        btn8.setOnClickListener {
            mostrarRanking(8)
        }
        btn10.setOnClickListener {
            mostrarRanking(10)
        }
        btn_atras.setOnClickListener {
            finish()
        }

        mostrarRanking(6)
    }

    private fun mostrarRanking(num : Int){

        val nombreArchivo = when (num) {
            6 -> "rankingModoFacil.txt"
            8 -> "rankingModoNormal.txt"
            10 -> "rankingModoDificil.txt"
            else -> return}

        rankingLista.forEach { it.text = "" } //Limpia los textos para evitar errores de carga

        try {
            val archivo = File(filesDir, nombreArchivo)
            val texto = if (archivo.exists()) archivo.readText() else ""

            val jugadoresArray = if (texto.isBlank()) JSONArray() else JSONArray(texto)

            for (i in 0 until 5) {
                if (i < jugadoresArray.length()) {
                    val jugador = jugadoresArray.getJSONObject(i)
                    val nombre = jugador.getString("nombre")
                    val puntos = jugador.getInt("puntos")
                    rankingLista[i].text = "${i + 1}. ${getString(R.string.nombre) + nombre} - ${getString(R.string.puntos) + puntos}"
                } else {
                    rankingLista[i].text = "${i + 1}. " + getString(R.string.vacio)
                }
            }
        } catch (e: Exception) {
            for (i in rankingLista.indices) {
                rankingLista[i].text = "${i + 1}. " + getString(R.string.vacio)
            }
        }

        textoDificultad.setText(getString(R.string.dificultad) + (num.toString() + "x" + num.toString()))
    }
}