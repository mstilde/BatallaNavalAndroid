package com.example.batallanaval


import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ActivityMenuAyuda : AppCompatActivity() {

    private lateinit var botonAtras: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_ayuda)

        // Botón para cerrar la pantalla de ayuda
        botonAtras = findViewById(R.id.atras)
        botonAtras.setOnClickListener {
            finish() // Cierra la activity actual y vuelve a la anterior
        }
    }
}
