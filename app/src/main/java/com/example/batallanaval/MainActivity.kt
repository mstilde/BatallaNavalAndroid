package com.example.batallanaval

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {
    var intentos = 0;
    var aciertos = 0;
    var barcosADerribar = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun seleccionarCasillero(v: View) {

    }

    fun atacar(v: View) {

    }

    fun nuevoJuego(v: View) {
        barcosADerribar = Random.nextInt(10, 16);
        colocarBarcos();
        var contadorDeBarcos = findViewById<TextView>(R.id.cantidadBarcos);
        contadorDeBarcos.setText(barcosADerribar.toString());
    }

    fun colocarBarcos() {
        var columna = 0;
        var fila = 0;
        var coordenada = "";

        for (b in 1..barcosADerribar) {
            var colocando = true;
            while (colocando) {
                columna = Random.nextInt(6) + 1 * 10;
                fila = Random.nextInt(6) + 1;
                coordenada = (columna + fila).toString();
                val idRecurso = resources.getIdentifier(coordenada, "id", packageName);
                val casillero = findViewById<Button>(idRecurso);
            }

        }

    }


}