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
    //Variables globales

    // Construir un tablero dentro de la logica kotlin declarando un lateinit para inicializarlo en el onCreate usando un layout TableLayout
    lateinit var tablero: TableLayout
    // Dimensiones del tablero.
    val filas = arrayOf("A", "B", "C", "D", "E", "F")
    val columnas = 6
    // Casilleros de tablero en una matriz 6x6
    lateinit var casilleros: Array<Array<Button>>
    // Usamos con "var" una variable para poder guardar el casillero seleccionado. Usamos un null como valor inicial ya que al iniciar el juego no tiene valor seleccionado.
    var casilleroSeleccionado : Button? = null;

    // Almacenamiento interno de las estadisticas.
    var intentos = 0;
    var aciertos = 0;
    var barcosADerribar = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Tomamos en layout del xml por el id y lo almacenamos en la variable. Luego generamos el tablero.
        tablero = findViewById(R.id.tablero)
        generarTablero()
    }

    fun generarTablero() {
        // Con removeAllViews limpiamos los casilleros con todas las configuraciones previas en caso de estar iniciado el juego de nuevo
        tablero.removeAllViews()
        // Se genera el tablero limpio de 0
        casilleros = Array(filas.size) { fil -> // Creamos un array para cada fila (fil) según el tamaño del array (6) y lo almacenamos en la matriz "casilleros"
            Array(columnas) { col -> // Hacemos lo mismo creando un array para cada columna. Cada valor col indicará el número de la columna.
                val boton = Button(this).apply { // En cada coordenada (fila y columna) de la matriz, creamos un botón en el contexto "this" y le aplicamos los sigientes datos:
                    text = "${filas[fil]}${col+1}" // Como texto, el valor actual de la letra de la fila y el de la columna + 1 (ya que los indices del array de dimension 6, van del 0 al 5)
                    layoutParams = TableRow.LayoutParams(0, 70).apply { // Agregar dimensiones de ancho, alto y aplicar weight para que los botones ocupen el mismo espacio dentro de la fila
                        weight = 1f
                    }
                    tag = "agua" //Agregarle un tag para identificar de que casillero se trata, si de agua o barco. En este caso como inicializa de 0 el tablero, se coloca en "agua"
                    setOnClickListener { seleccionarCasillero(this) } // Le damos también un listener para que al clickarlo se active la función seleccionarCasillero
                }
                boton // Acá se devuelve la variable que creamos recién para almacenarlo en el array de la columna
            }
        }

        // Acá agregamos la estructura de matriz generada anteriormente a la interfaz de usuario
        for (fila in casilleros) { // Hacemos un for por cada fila en la tabla de casilleros
            val tableRow = TableRow(this) // Creamos una variable TableRow en donde almacenaremos los botones
            fila.forEach { boton -> tableRow.addView(boton) } // Con el forEach vamos cada botón en el tableRow que creamos para la interfaz con addView
            tablero.addView(tableRow) // Finalmente, al TableLayout tablero vamos agregandole su respectiva fila para mostrar en la interfaz
        }
    }

    fun seleccionarCasillero(boton: Button) {
        casilleroSeleccionado?.setBackgroundResource(android.R.drawable.btn_default)

        casilleroSeleccionado = boton
        boton.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light, theme))
    }

    fun atacar(v: View) {
        val boton = casilleroSeleccionado ?: return

        if (boton.text == "X" || boton.text == "O") return

        if (boton.tag == "barco") {
            boton.text = "X"
            aciertos++
        } else {
            boton.text = "O"
        }

        intentos++
        actualizarContadores()

        boton.setBackgroundResource(android.R.drawable.btn_default)
        casilleroSeleccionado = null
    }

    fun nuevoJuego(v: View) {
        intentos = 0
        aciertos = 0
        barcosADerribar = Random.nextInt(10, 16);

        generarTablero()
        colocarBarcos();

        findViewById<TextView>(R.id.cantidadBarcos).text = barcosADerribar.toString()
        actualizarContadores()

    }

    fun colocarBarcos() {
        var colocados = 0
        while (colocados < barcosADerribar) {
            val fila = Random.nextInt(filas.size)
            val col = Random.nextInt(columnas)
            val boton = casilleros[fila][col]
            if (boton.tag == "agua") {
                boton.tag = "barco"
                colocados++
            }
        }
    }

    fun actualizarContadores() {
        findViewById<TextView>(R.id.cantidadMovimientos).text = intentos.toString()
        findViewById<TextView>(R.id.cantidadAciertos).text = aciertos.toString()
    }
}
