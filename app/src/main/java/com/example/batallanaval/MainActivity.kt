package com.example.batallanaval

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {
    lateinit var tablero: TableLayout
    val filas = listOf("A", "B", "C", "D", "E", "F")
    val columnas = 6
    lateinit var botones: Array<Array<Button>>
    var intentos = 0;
    var aciertos = 0;
    var barcosADerribar = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tablero = findViewById(R.id.tablero)
        generarTablero()
    }

    fun generarTablero() {
        botones = Array(filas.size) { row ->
            Array(columnas) { col ->
                val boton = Button(this).apply {
                    text = "${filas[row]}${col+1}"
                    layoutParams = TableRow.LayoutParams(0, 70).apply {
                        weight = 1f
                    }
                    tag = "agua"
                    setOnClickListener { seleccionarCasillero(it) }
                }
                boton
            }
        }

        for (fila in botones) {
            val tableRow = TableRow(this)
            fila.forEach { boton -> tableRow.addView(boton) }
            tablero.addView(tableRow)
        }
    }

    fun seleccionarCasillero(v: View) {
        val boton = v as Button
        if (boton.tag == "barco") {
            boton.text = "X"
            aciertos++
        } else {
            boton.text = "O"
        }
        intentos++
        actualizarContadores()
    }

    fun atacar(v: View) {

    }

    fun nuevoJuego(v: View) {
        barcosADerribar = Random.nextInt(10, 16);
        colocarBarcos();
        findViewById<TextView>(R.id.cantidadBarcos).text = barcosADerribar.toString()
        intentos = 0
        aciertos = 0
        actualizarContadores()
        generarTablero()
    }

    fun colocarBarcos() {
        var colocados = 0
        while (colocados < barcosADerribar) {
            val fila = Random.nextInt(filas.size)
            val col = Random.nextInt(columnas)
            val boton = botones[fila][col]
            if (boton.tag == "agua") {
                boton.tag == "barco"
                colocados++
            }
        }
    }

    fun actualizarContadores() {
        findViewById<TextView>(R.id.cantidadMovimientos).text = intentos.toString()
        findViewById<TextView>(R.id.cantidadAciertos).text = aciertos.toString()
    }
}