package com.example.batallanaval

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.view.View
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlin.random.Random

class ActivityJuego : AppCompatActivity() {

    // Referencias a vistas del XML
    private lateinit var tableroXml: TableLayout
    private lateinit var textJugador: TextView
    private lateinit var textMovimientos: TextView
    private lateinit var textBarcosRestantes: TextView
    private lateinit var textAciertos: TextView
    private lateinit var botonMenuPopup: Button
    private lateinit var botonReiniciar: Button
    private lateinit var visorDeTiempo: TextView

    // Variables internas del juego
    private var barcos = 0
    private var movimientos = 0
    private var aciertos = 0
    private var dificultad = 6
    private var tiempoDificultad = 0
    private var tiempo = 0
    private var handlerTiempo = Handler(Looper.getMainLooper())
    private var puntajeFinal = 0
    private var nombreJugador = ""

    // Tablero lógico y visual
    private lateinit var matrizBarcos: Array<BooleanArray>
    private lateinit var matrizBotones: Array<Array<Button>>

    private val runnableTiempo = object : Runnable {
        override fun run() {
            tiempo--
            visorDeTiempo.text = tiempo.toString()
            if (tiempo <= 0) {
                detenerTimer()
                mostrarDialogoDerrota()
            } else if (tiempo <= 10) {
                visorDeTiempo.setTextColor(getColor(android.R.color.holo_red_light))
            }
            handlerTiempo.postDelayed(this, 1000)
        }
    }

    // onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        // Asociamos vistas XML
        textAciertos = findViewById(R.id.contador_aciertos)
        textMovimientos = findViewById(R.id.contador_movimientos)
        textBarcosRestantes = findViewById(R.id.barcos_Restantes)
        textJugador = findViewById(R.id.view_jugador)
        tableroXml = findViewById(R.id.tablero)
        botonReiniciar = findViewById(R.id.reinicio)
        botonMenuPopup = findViewById(R.id.opciones)
        visorDeTiempo = findViewById(R.id.timer)

        // Recuperamos datos del Intent
        nombreJugador = intent.getStringExtra("dato2") ?: "Jugador" // En caso de no recibir un nombre, por defecto asignamos "Jugador"
        dificultad = intent.getIntExtra("dato1", 6) // Si, por si acaso, no recibe nada, siempre la pantalla empieza en 6 casilleros
        textJugador.text = getString(R.string.jugador) + nombreJugador // Mostramos el nombre del jugador en pantalla
        tiempoDificultad = intent.getIntExtra("datoTiempo", 0)
        tiempo = tiempoDificultad
        visorDeTiempo.text = tiempo.toString()

        // Si la abrimos otra activity, restauramos el estado
        if (savedInstanceState != null) {
            movimientos = savedInstanceState.getInt("movimientos")
            aciertos = savedInstanceState.getInt("aciertos")
            barcos = savedInstanceState.getInt("barcos")
            dificultad = savedInstanceState.getInt("dificultad")

            val plano = savedInstanceState.getBooleanArray("estadoTablero")
            val presionados = savedInstanceState.getBooleanArray("botonesPresionados")

            matrizBarcos = Array(dificultad) { BooleanArray(dificultad) }
            matrizBotones = Array(dificultad) { Array(dificultad) { Button(this) } }

            // Reconstruimos el tablero
            for (i in 0 until dificultad) {
                val fila = TableRow(this) // Creamos una fila del tipo TableRow
                for (j in 0 until dificultad) {
                    // Reconstruimos el xml del botón
                    val boton = Button(this)
                    val ancho = calcularDimensionBoton()
                    boton.layoutParams = TableRow.LayoutParams(ancho, ancho)
                    boton.text = "${'A' + j}${i + 1}"
                    boton.setOnClickListener { botonClick(i, j) }
                    // Calculamos la ubicación en el arreglo unidimensional del boton que queremos restaurar
                    val index = i * dificultad + j
                    // Preguntamos si ya fué presionado en base a como guardamos el valor booleano
                    if (presionados?.get(index) == false) {
                        // Si fue presionado, restauramos su estado presionado
                        if (plano?.get(index) == true) {
                            boton.text = getString(R.string.barco)
                            boton.setBackgroundColor(Color.RED)
                        } else {
                            boton.text = getString(R.string.agua)
                            boton.setBackgroundColor(Color.CYAN)
                        }
                        boton.isEnabled = false
                        boton.textSize = 10f
                    }
                    // De no haber sido presionados, simplemente lo agregamos a la fila y a las matrices internas
                    fila.addView(boton)
                    matrizBotones[i][j] = boton
                    matrizBarcos[i][j] = plano?.get(index) ?: false
                }
                tableroXml.addView(fila)
            }
            actualizarContadores()
        } else {
            // Juego nuevo
            reiniciarJuego()
        }

        // Asignamos acciones para los botones
        botonReiniciar.setOnClickListener { reiniciarJuego() }
        botonMenuPopup.setOnClickListener { mostrarMenuPopup() }
    }

    // Calcula el tamaño dinámico de los botones
    private fun calcularDimensionBoton(): Int {
        // Tomamos en una variable con el ancho de pixeles de la pantalla
        val screenWidth = resources.displayMetrics.widthPixels
        // Tomamos en una variable el resultado de multiplicar la densidad de pixeles por 16 y lo pasamos a un int
        // Tomamos el numero 16 (de 16dp) como un estandar mas o menos arbitrario de margen para elementos visuales
        val margen = (16 * resources.displayMetrics.density).toInt()
        // Al ancho que calculamos, le restamos los dos margenes a la derecha e izquierda
        val anchoDisponible = screenWidth - (2 * margen)
        // Devolvemos el ancho disponible, achicandolo un poco con una constante, y lo dividimos
        // por la dificultad para acomodar los botones según la cantidad de casilleros
        return ((anchoDisponible * 0.95f) / dificultad).toInt()
    }

    // Muestra el menú contextual
    private fun mostrarMenuPopup() {
        // Creamos una instancia de PopupMenu en este contexto y anclado en el botón de menú
        val popup = PopupMenu(this, botonMenuPopup)
        // Usamos el iflador de menú en la variable popup y cargamos el xml menu
        popup.menuInflater.inflate(R.menu.menu, popup.menu)

        // Seteamos en la variable distintos items clickeables con un switch
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                // Al item1 del menú que cargamos del inflador, le asignamos la funcionalidad de empezar la main activity
                R.id.item1 -> {
                    val i = Intent(this, MainActivity::class.java)
                    // Esto sirve para verificar si la activity ya está abierta, traerla adelante en vez de crear una nueva
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(i)
                    true
                }
                // Al item2 le cargamos la activity del menú de ayuda.
                R.id.item2 -> {
                    val i = Intent(this, ActivityMenuAyuda::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(i)
                    true
                }
                else -> false
            }
        }
        // Se muestra finalmente el botón cuando se clickea
        popup.show()
    }

    // Actualiza los contadores en pantalla
    private fun actualizarContadores() {
        textAciertos.text = getString(R.string.aciertos) + aciertos
        textMovimientos.text = getString(R.string.movimientos) + movimientos
        textBarcosRestantes.text = getString(R.string.barcosRestantes) + (barcos - aciertos)
    }

    // Guarda el estado actual
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Salvamos las variables actuales
        outState.putInt("barcos", barcos)
        outState.putInt("movimientos", movimientos)
        outState.putInt("aciertos", aciertos)
        outState.putInt("dificultad", dificultad)

        // Para salvar una matriz, la convertimos en un array
        // Primero creamos un array con el tamaño de las dimensiones del array
        val plano = BooleanArray(dificultad * dificultad)
        // Lo mismo hacemos para almacenar que botones fueron ya presionados
        val presionados = BooleanArray(dificultad * dificultad)


        for (i in 0 until dificultad) {
            for (j in 0 until dificultad) {
                // El indice en cuestión de cada botón, será establecido en partes equivalentes a la matriz
                // Por ejemplo, de 0 a 5, la primera fila, de 6 a 11, la segunda, y así.
                // Eso lo determinamos multiplicando la dificultad actual, por el indice de fila.
                // Esto va a dar un numero en un rango de n posibles indices, dependiendo de la dificultad.
                // En dificultad normal, el rango sería de 0 a 6 para la primera fila. Y para determinar
                // Cual será el indice en ese rango, se le suma la coordenada j, que indica la columna.
                val index = i * dificultad + j
                // En el indice resultante se almacena el botón de la coordenada
                plano[index] = matrizBarcos[i][j]
                // Guardamos cada estado del botón, para saber cual ya fué presionado y cual no
                presionados[index] = matrizBotones[i][j].isEnabled
            }
        }

        // Almacenamos los arrays
        outState.putBooleanArray("estadoTablero", plano)
        outState.putBooleanArray("botonesPresionados", presionados)
    }

    // Coloca los barcos aleatoriamente en el tablero
    private fun colocarBarcos() {
        // Elegimos un número entre 10 y 15 de barcos a colocar e inicializamos un contador
        barcos = Random.nextInt(10, 15)
        var colocados = 0

        // Vamos colocando los barcos de a uno en coordenadas aleatorias
        while (colocados < barcos) {
            val i = Random.nextInt(dificultad)
            val j = Random.nextInt(dificultad)
            // Si en la matriz ese casillero está en false, quiere decir que no tiene barco, asi que lo actualizamos a true y aumentamos el contador
            if (!matrizBarcos[i][j]) {
                matrizBarcos[i][j] = true
                colocados++
            }
        }
    }

    // Crea los botones del tablero
    private fun crearTablero() {
        // Limpiamos el tablero para construirlo de 0
        tableroXml.removeAllViews()
        // Almacenamos un número resultado de calcular las dimensiones que va a tener el botón en base a la dificultad
        val dimensionBoton = calcularDimensionBoton()

        // En esta estructura construimos las dimensiones del botón junto a sus atributos xml. La cantidad y sus coordenadas dependen de la dificultad seleccionada
        for (i in 0 until dificultad) {
            val fila = TableRow(this) // Fila es una variable del tipo TableRow, es decir, una estructura lineal de elementos xml
            for (j in 0 until dificultad) { // Seguimos construyendo ahora las filas de la matriz
                val boton = Button(this) // La variable botón de tipo Button será en donde asignemos los valores xml
                boton.text = "${'A' + j}${i + 1}" // El texto será 1ro el valor ASCII correspondiente empezando desde A. 2do el valor de fila correspiendiente
                boton.layoutParams = TableRow.LayoutParams(dimensionBoton, dimensionBoton) // Le asignamos las dimensiones calculadas previamente
                boton.textSize = 10f // Tamaño del texto
                boton.setOnClickListener { botonClick(i, j) } // Le asignamos una función que reciba 2 parametros. Esos dos parametros vas a ser las coordenadas de dicho botón
                fila.addView(boton) // En la TableRow creada, agregamos el botón recién creado como View
                matrizBotones[i][j] = boton // En nuestra matriz interna de botones, almacenamos el botón creado
            }
            tableroXml.addView(fila) // Al tablero en nuestro xml, le agregamos la fila de botones creada
        }
    }

    // Acción al hacer clic en un botón
    private fun botonClick(i: Int, j: Int) {
        // La variable botón recibe un botón de la matriz con las coordenadas indicadas que recibe la función
        val boton = matrizBotones[i][j]

        // Si el botón ya fué presionado y está deshabilitado, la función termina y no hace nada
        if (!boton.isEnabled) return

        // Se actualizan la cantidad de movimientos
        movimientos++

        if (matrizBarcos[i][j]) { // Si en la matriz de booleanos, el casillero resulta ser true, se incrementan los aciertos y se setea el casillero como un barco hundido
            aciertos++
            boton.text = getString(R.string.barco)
            boton.setBackgroundColor(Color.RED)
        } else { // En caso de haber fallado, se setea el casillero como agua
            boton.text = getString(R.string.agua)
            boton.setBackgroundColor(Color.CYAN)
        }

        // Se cambia el tamaño del texto para que se acomode al casillero, se deshabilita el botón y se actualizan contadores del xml
        boton.textSize = 10f
        boton.isEnabled = false
        actualizarContadores()

        // Verificamos luego de clickear el botón si ya se eliminaron todos los barcos
        if (aciertos == barcos) {
            puntajeFinal = ((aciertos.toDouble() / movimientos.toDouble()) * 1000).toInt()
            val recordBatido = nuevoRecord()
            if (recordBatido) {
                almacenarJugador()
            }
            mostrarDialogoVictoria(recordBatido)
        }
    }

    // Reinicia completamente el juego
    private fun reiniciarJuego() {
        // Seteamos las estadisticas a 0 de nuevo
        movimientos = 0
        aciertos = 0
        barcos = 0
        iniciarTimer()
        // Volvemos a inicializar las matrices con botones vacios y sin barcos colocados (en false)
        matrizBotones = Array(dificultad) { Array(dificultad) { Button(this) } }
        matrizBarcos = Array(dificultad) { BooleanArray(dificultad) }
        // Llamamos a las funciones para crear de nuevo el tablero de juego
        crearTablero()
        colocarBarcos()
        actualizarContadores()
    }

    private fun llevarARanking() {
        val i = Intent(this, ActivityRanking::class.java)
        startActivity(i)
    }

    // Muestra el cuadro de diálogo al ganar
    private fun mostrarDialogoVictoria(record: Boolean) {
        deshabilitarBotones() // Llamamos a la funcion para deshabilitarlos
        detenerTimer() // Detenemos el tiempo cuando se gana la partida

        // En builder asignamos una instanciación de AlertDialog con el constructor Builder en este contexto (this)
        val builder = AlertDialog.Builder(this)
        // Seteamos el texto del titulo y del mensaje
        builder.setTitle(getString(R.string.tituloGanaste))
        if (record) {
            builder.setMessage("${getString(R.string.ganaste)}\n${getString(R.string.aciertos)} $aciertos - ${getString(R.string.fallos)} ${movimientos - aciertos}\n\n${getString(R.string.mensajeNuevoRecord)}")
            builder.setNeutralButton(getString(R.string.compartirResultado)) { _, _ -> compartirRecord() }
        } else {
            builder.setMessage("${getString(R.string.ganaste)}\n${getString(R.string.aciertos)} $aciertos - ${getString(R.string.fallos)} ${movimientos - aciertos}")
        }

        // Seteamos los botones y su funcion
        builder.setPositiveButton(getString(R.string.btnAceptar)) { _, _ -> llevarARanking() }
        builder.setNegativeButton(getString(R.string.btnReiniciar)) { _, _ -> reiniciarJuego() }

        builder.setCancelable(false)

        // Construimos el alert dialog final
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    // Muestra el cuadro de diálogo al perder
    private fun mostrarDialogoDerrota() {
        deshabilitarBotones() // Llamamos a la funcion para deshabilitarlos
        detenerTimer() // Detenemos el tiempo cuando se pierde la partida

        // En builder asignamos una instanciación de AlertDialog con el constructor Builder en este contexto (this)
        val builder = AlertDialog.Builder(this)
        // Seteamos el texto del titulo y del mensaje
        builder.setTitle(getString(R.string.tituloPerdiste))
        builder.setMessage(getString(R.string.perdiste))
        // Seteamos los botones y su funcion
        builder.setPositiveButton(getString(R.string.btnSalir)) { _, _ -> finish() }
        builder.setNegativeButton(getString(R.string.btnReiniciar)) { _, _ -> reiniciarJuego() }

        builder.setCancelable(false)

        // Construimos el alert dialog final
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    // Desactiva todos los botones (fin del juego)
    private fun deshabilitarBotones() {
        for (i in 0 until dificultad) {
            for (j in 0 until dificultad) {
                matrizBotones[i][j].isEnabled = false // Recorremos la matriz, elemento por elemento, y desactivamos los botones
            }
        }
    }

    // Detener timer
    private fun detenerTimer() {
        handlerTiempo.removeCallbacks(runnableTiempo)
    }

    // Iniciar timer
    private fun iniciarTimer() {
        tiempo = tiempoDificultad + 1
        visorDeTiempo.setTextColor(getColor(R.color.white))
        handlerTiempo.removeCallbacks(runnableTiempo) // Por seguridad, limpiamos antes
        handlerTiempo.postDelayed(runnableTiempo, 1000)
    }

    // Busca según la dificultad que archivo retornar
    private fun retornarArchivoDificultad(): File {
        if (tiempoDificultad == 20) {
            return File(filesDir, "rankingModoFacil.txt")
        } else if (tiempoDificultad == 25) {
            return File(filesDir, "rankingModoNormal.txt")
        } else {
            return File(filesDir, "rankingModoDificil.txt")
        }
    }

    // Almacenar jugador
    private fun almacenarJugador() {
        val archivo = retornarArchivoDificultad() // Buscamos el archivo

        val jugador = JSONObject().apply { // Creamos un objeto JSON con el nombre del jugador
            put("nombre", nombreJugador)
            put("puntos", puntajeFinal)
        }

        val jugadoresArray = try {
            if (!archivo.exists() || archivo.readText().isBlank()) {
                JSONArray()
            } else {
                JSONArray(archivo.readText())
            }
        } catch (e: Exception) {
            JSONArray() // Si hay un error de parsing, empezamos con array vacío
        }

        // Si no hay jugadores en el array, ponemos al jugador en el array. Sino buscamos el indice en donde ubicarlo
        if (jugadoresArray.length() == 0) {
            jugadoresArray.put(jugador)
        } else {
            var posicionEncontrada = false
            var jugadorEvaluado: JSONObject
            var index = 0

            while (!posicionEncontrada && index < jugadoresArray.length()) {
                jugadorEvaluado = jugadoresArray.getJSONObject(index)
                if (jugadorEvaluado.getInt("puntos") > jugador.getInt("puntos")) {
                    index++
                } else {
                    posicionEncontrada = true
                }
            }

            // Eliminamos al ultimo jugador una vez que encontramos donde ubicar al nuevo para hacerle espacio
            if (jugadoresArray.length() == 5) {
                jugadoresArray.remove(jugadoresArray.length() - 1)
            }

            for (i in jugadoresArray.length() downTo index + 1) {
                jugadoresArray.put(i, jugadoresArray.getJSONObject(i - 1))
            }

            jugadoresArray.put(index, jugador)
        }

        archivo.writeText(jugadoresArray.toString()) // Sobrescribimos el archivo
    }

    // Evaluamos si re marcó un nuevo record según la dificultad
    private fun nuevoRecord(): Boolean {
        // Buscamos con la función el archivo según la dificultad
        val archivo = retornarArchivoDificultad()

        // Si el archivo de texto esta vacio o no existe, jugadoresArray es un array nuevo
        // Sino, la variable se transforma en el array ya existente en el archivo
        val jugadoresArray = try {
            if (!archivo.exists() || archivo.readText().isBlank()) {
                JSONArray()
            } else {
                JSONArray(archivo.readText())
            }
        } catch (e: Exception) {
            JSONArray()
        }

        // Lo que retorna es true si hay espacio en el ranking o si el puntaje final del jugador es mayor al del ultimo jugador del array
        return if (jugadoresArray.length() < 5) {
            true // Aún hay espacio en el ranking
        } else {
            val ultimoJugador = jugadoresArray.getJSONObject(jugadoresArray.length() - 1)
            puntajeFinal > ultimoJugador.getInt("puntos")
        }
    }

    // Funcion para compartir record
    private fun compartirRecord() {
        // Creamos la variable Intent
        val i = Intent()

        // Según la dificultad establecida, tomamos el texto pertinente para el mensaje
        val dificultadJuego =
            when (tiempoDificultad) {
                20 -> getString(R.string.modoFacil)
                25 -> getString(R.string.modoNormal)
                else -> getString(R.string.modoDificil)
            }

        // Mensaje compartido
        val mensajeJugador = "${textJugador.text.toString()}${getString(R.string.puntuacionLograda1)}\n" +
                "${getString(R.string.puntuacionLograda2)}$dificultadJuego${getString(R.string.puntuacionLograda3)}" +
                "$puntajeFinal${getString(R.string.puntuacionLograda4)}"

        // Configuramos en la variable Intent la accion, el tipo y el mensaje
        i.action = Intent.ACTION_SEND
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_TEXT, mensajeJugador)

        // Verificamos que el celular disponga de apps que puedan recibir el intent
        if (i.resolveActivity(packageManager) != null) {
            startActivity(i)
        }
    }
}
