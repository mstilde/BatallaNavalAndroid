package com.example.batallanaval

import android.widget.TableLayout
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.TableRow
import kotlin.random.Random
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class ActivityJuego : AppCompatActivity() {
    lateinit var tableroJuego : TableLayout // Tablero en donde se va a desarrollar el juego en el xml
    lateinit var viewMovimientos: TextView // Variable para almacenar el View de los movimientos del usuario
    lateinit var viewBarcosRestantes: TextView // Variable para almacenar el View de los barcos restantes
    lateinit var viewAcertados: TextView // Variable para almacenar el view de los barcos acertados

    val tamanio = 6 // Constante de dimensión física para el tablero
    var tableroBooleanos = Array(tamanio){Array(tamanio){false}} // Matriz de booleanos para los valores barco/agua (inicializado tdo en false, osea, agua)
    var tableroBotones = Array(tamanio){arrayOfNulls<Button>(tamanio)} // Matriz de memoria para botones, inicializados en null
    var cantidadAciertos = 0 // Variable interna de aciertos
    var cantidadMovimientos = 0 // Variable interna de cantidad de movimientos
    var barcos = 0 // Variable interna de barcos a hundir

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        // Asignamos a las variables declaradas sus respectivos elementos xml
        tableroJuego = findViewById(R.id.tablero)
        viewMovimientos = findViewById<TextView>(R.id.contador_movimientos)
        viewBarcosRestantes = findViewById<TextView>(R.id.barcos_Restantes)
        viewAcertados = findViewById<TextView>(R.id.contador_aciertos)

        // Inicializamos la cantidad de barcos y el tablero
        colocarBarcos()
        iniciarTablero()

        viewBarcosRestantes.text= "Barcos restantes: ${barcos}"
    }

    // Función para resetear los contadores locales
    fun reiniciarContadores(){
        cantidadMovimientos = 0
        cantidadAciertos = 0
    }

    // Función para setear el juego de 0
    fun reiniciarJuego(v: View){
        // Resetear el tablero
        tableroJuego.removeAllViews() // Limpiar parte gráfica del tablero
        tableroBotones = Array(tamanio){arrayOfNulls<Button>(tamanio)} // Limpiar la parte de lógica interna del tablero
        tableroBooleanos = Array(tamanio){Array(tamanio){false}} // El tablero de booleanos lo seteamos completo en false
        // Ya con el juego limpio, llamamos a las funciones que construyen un nuevo espacio de juego
        reiniciarContadores()
        colocarBarcos()
        iniciarTablero()
        // Resetear las estadisticas
        viewMovimientos.text = "Movimientos: ${cantidadMovimientos}"
        viewAcertados.text = "Acertados: ${cantidadAciertos}"
        viewBarcosRestantes.text= "Barcos restantes: ${barcos}"
    }

    // Genero un random entre un rango delimitado
    fun generarRandom(num1:Int,num2:Int):Int{
        return Random.nextInt(num1,num2+1)
    }

    // Genero los barcos
    fun colocarBarcos(){
        var barcosColocados = 0
        barcos = generarRandom(10,15)
        // Vamos colocando de a uno los barcos y verificando si ya hay uno colocado
        while(barcosColocados < barcos) {
            var fila = generarRandom(0, (tamanio - 1))
            var columna = generarRandom(0, (tamanio - 1))
            if (!tableroBooleanos[fila][columna]) {
                tableroBooleanos[fila][columna] = true
                barcosColocados++
            }
        }
    }

    // Función que se le asigna a cada botón con su coordenada correspondiente
    fun botonClik(i : Int, j : Int){
        var casillero = tableroBotones[i][j] // Almacenamos el botón actual en una variable

        if(casillero?.isEnabled == false) return // isEnabled verifica si el boton esta disponible o no para interactuar, es decir, si ya se jugó en la partida

        // Verificamos el contenido del casillero
        if (tableroBooleanos[i][j]) { // Si tiene un barco, osea, si está en true
            // Modificamos el estilo del casillero en función de que encontró un barco y se hundió
            casillero?.textSize = 10f
            casillero?.text = "BARCO"
            casillero?.setBackgroundColor(Color.RED)
            // Actualizamos las estadisticas
            cantidadAciertos++
            barcos--
            viewAcertados.text= "Acertados: ${cantidadAciertos}"
            viewBarcosRestantes.text= "Barcos restantes: ${barcos}"
        } else { // Si el casillero tiene agua, lo modificamos de forma distinta
            casillero?.textSize = 10f
            casillero?.text = "AGUA"
            casillero?.setBackgroundColor(Color.CYAN)
        }
        // Actualizamos los movimientos
        cantidadMovimientos++
        viewMovimientos.text="Movimientos: ${cantidadMovimientos}"

        casillero?.isEnabled = false // Deshabilitamos el botón

        if (barcos==0) { // Verificamos si quedan barcos
            mostrarDialogoVictoria() // Se muestra el dialogo al ganar la partida
        }
    }
    // Se crea la interfáz gráfica del tablero, con sus botones en el TableLayout del xml
    fun iniciarTablero(){
        val dpBoton = (40 * resources.displayMetrics.density).toInt() // Tamaño del boton a usar. Se mutiplica el dp actual de la pantalla por 40 (Osea, tamaño de 40dp)
        for (i in 0 until tamanio) {
            val fila = TableRow(this) // Creamos una fila en el contexto de la main activity
            for (j in 0 until tamanio) {
                val letra = 'A' + j // Vamos actualizando las letra de la coordenada correspondiente en cada columna usando ASCII y la almacenamos
                val boton = Button(this)
                // Creamos los valores xml del botón
                boton.text = "${letra}${i+1}" // Le damos al botón creado su letra y número correspondiente de so coordenada
                boton.layoutParams = TableRow.LayoutParams(dpBoton, dpBoton) // Asignamos las dimensiones correspondientes
                boton.textSize = 18f
                // Le asignamos a cada botón una función que mande como parámetros sus coordenadas
                boton.setOnClickListener {
                    botonClik(i,j)
                }
                fila.addView(boton) // Agregamos el boton a la interfaz grafica de la fila
                tableroBotones[i][j] = boton // Agregamos el boton a la matriz global de botónes en su coordenada correspondiente
            }
            tableroJuego.addView(fila) // Finalmente, agregamos la fila en la interfaz gráfica de la matríz
        }
    }

    // Salvamos la información cuando se cambia de pantalla
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Para almacenar el array, necesitamos aplanarlo en una sola dimensión y almacenarlo en un array booleano
        val arrayPlano = BooleanArray(tamanio * tamanio) { i ->
            val fila = i / tamanio
            val col = i % tamanio
            tableroBooleanos[fila][col]
        }
        // También necesitamos almacenar un array para saber cuales botones fueron presionados y cuales no
        val botonesPresionados = BooleanArray(tamanio * tamanio) { i ->
            val fila = i / tamanio
            val col = i % tamanio
            !tableroBotones[fila][col]?.isEnabled!! // True si fue presionado
        }

        // Guardamos los datos
        outState.putBooleanArray("arrayBooleano", arrayPlano)
        outState.putBooleanArray("botonesPresionados", botonesPresionados)
        outState.putInt("cantidadAciertos", cantidadAciertos)
        outState.putInt("cantidadMovimientos", cantidadMovimientos)
        outState.putInt("barcos", barcos)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Recuperamos los datos
        cantidadAciertos = savedInstanceState.getInt("cantidadAciertos")
        cantidadMovimientos = savedInstanceState.getInt("cantidadMovimientos")
        barcos = savedInstanceState.getInt("barcos")
        val arrayPlano = savedInstanceState.getBooleanArray("arrayBooleano")
        val arrayBotonesPresionados = savedInstanceState.getBooleanArray("botonesPresionados")

        if (arrayPlano != null && arrayBotonesPresionados != null) { // Verificación de si los arrays se restauran correctamente. Sin esto, Kotlin no deja recuperar los indices de los arrays
            for (i in arrayPlano.indices) {
                val fila = i / tamanio
                val col = i % tamanio
                tableroBooleanos[fila][col] = arrayPlano[i] // Restauramos cada valor booleano como estaba

                // Ahora restauramos el valor de cada botón en el array de botones
                val boton = tableroBotones[fila][col]

                if (arrayBotonesPresionados[i]) { // Verificamos si el botón fué presionado previamente antes de revelarlo
                    if (tableroBooleanos[fila][col]) {
                        boton?.textSize = 10f
                        boton?.text = "BARCO"
                        boton?.setBackgroundColor(Color.RED)
                    } else {
                        boton?.textSize = 10f
                        boton?.text = "AGUA"
                        boton?.setBackgroundColor(Color.CYAN)
                    }
                    boton?.isEnabled = false
                }
            }
        }

        // Restauramos los views
        viewAcertados.text = "Acertados: ${cantidadAciertos}"
        viewMovimientos.text = "Movimientos: ${cantidadMovimientos}"
        viewBarcosRestantes.text= "Barcos restantes: ${barcos}"
    }

    fun mostrarDialogoVictoria() {
        deshabilitarBotones()
        val builder = AlertDialog.Builder(this) // Dialogo de alerta/victoria

        builder.setTitle("¡Victoria!")
        builder.setMessage("¡Hundiste todos los barcos!\nBarcos: ${cantidadAciertos} - Agua: ${cantidadMovimientos-cantidadAciertos}")
        builder.setNegativeButton("Reiniciar") { dialog,which ->
            reiniciarJuego(View(this))
        }
        builder.setPositiveButton("Salir") { dialog, which ->
            // Futuro botón para volver al menú principal
        }

        builder.show()
    }

    fun deshabilitarBotones() {
        for (i in 0 until tamanio) {
            for (j in 0 until tamanio) {
                tableroBotones[i][j]?.isEnabled = false
            }
        }
    }
}