package com.pargar.kidsevolution.view

import android.content.res.Configuration
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.get
import com.pargar.kidsevolution.databinding.FragmentJuegoObjectsBinding
import com.pargar.kidsevolution.databinding.FichasBinding
import com.pargar.kidsevolution.R
import android.view.View

class JuegoObjects : AppCompatActivity() {
    /*
     * TODO cancion cuando ganas
     * registrar actividad en el manifest
     */

    private lateinit var binding: FragmentJuegoObjectsBinding
    private lateinit var bindingFichas: FichasBinding
    //cada vez que se inicie la app, la variable que refleja la cantidad de clicks que se han hecho vuelve a 0
    private val CANTIDAD_COLUMNAS:Int = 4

    private val nombreFrutasMap = mapOf(
        R.drawable.balon to "balon",
        R.drawable.globo to "globo",
        R.drawable.mesa to "mesa",
        R.drawable.carro to "carro",
        R.drawable.silla to "silla",
        R.drawable.celular to "celular"
    )

    private var imageList = listOf(
        R.drawable.balon,
        R.drawable.globo,
        R.drawable.mesa,
        R.drawable.carro,
        R.drawable.silla,
        R.drawable.celular,
        R.drawable.balon,
        R.drawable.globo,
        R.drawable.mesa,
        R.drawable.carro,
        R.drawable.silla,
        R.drawable.celular
    ).shuffled()

    //Separo la lista de imagenes en listas que contengan la misma cantidad de fotos que de columnas haya
    //cada sublista representa una fila en la interfaz
    private var listOfLists: List<List<Int>> = imageList.chunked(CANTIDAD_COLUMNAS)

    private lateinit var contenedorImagenes: LinearLayout
    private lateinit var imagenesClickadas: ArrayList<ImageView>
    private lateinit var btnBack:ImageButton
    private lateinit var btnReset:Button
    private lateinit var btnMusica: ImageButton
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var bgSong:MediaPlayer
    private lateinit var imagen : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //al empezar la aplicacion pongo la música a sonar
        bgSong = MediaPlayer.create(this,R.raw.main_song)
        bgSong.isLooping = true
        bgSong.setVolume(0.1F,0.1F)

        binding = FragmentJuegoObjectsBinding.inflate(layoutInflater)
        bindingFichas = FichasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contenedorImagenes = findViewById(R.id.imageContainer)

        imagenesClickadas = ArrayList()

        btnBack = findViewById(R.id.bBack)
        btnBack.setOnClickListener{
            bgSong.pause()
            finish()
        }

        imagen = findViewById(R.id.congratulation)
        btnReset = findViewById(R.id.btnReset)
        btnReset.setOnClickListener{resetear()}
        btnMusica = findViewById(R.id.btnSonido)
        btnMusica.setOnClickListener{gestionarMusicaFondo()}
        gestionarMusicaFondo()

        //Recorro el contenedor de LinearLayouts
        for (i in 0 until contenedorImagenes.childCount){
            //obtengo el hijo i del contenedor de LinearLayouts
            val linearLayout:LinearLayout = contenedorImagenes[i] as LinearLayout
            //recorro los hijos del linearlayout (las fichas)
            for (j in 0 until linearLayout.childCount){
                val currentImage:ImageView = (linearLayout[j] as ImageView)
                //le añado como tag la foto que se le va a poner cuando reciba un click
                currentImage.tag=listOfLists[i][j]
                //añado evento on click
                currentImage.setOnClickListener {handleClick(currentImage)}
            }
        }

    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        gestionarMusicaFondo()
    }

    private fun gestionarMusicaFondo() {
            if (!bgSong.isPlaying){
                btnMusica.setImageResource(R.drawable.sound_on)
                bgSong.start()
            }else{
                btnMusica.setImageResource(R.drawable.sound_off)
                bgSong.pause()
            }
    }

    private fun handleClick(image:ImageView){
        val resourceId = image.tag as Int
        image.setImageResource(resourceId)

        val nombreFruta = nombreFrutasMap[resourceId]
        if (nombreFruta != null) {
            sonido(nombreFruta)
        }

        // lo pongo a false para que al clickar otra vez sobre la misma imagen no cuente
        image.isEnabled = false
        handler.postDelayed({
            imagenesClickadas.add(image)
            if (imagenesClickadas.size == 2){
                //compruebo que son match pero que no haya sido la misma imagen
                if ((imagenesClickadas[0].tag.equals(imagenesClickadas[1].tag) && !areTheSameImage(imagenesClickadas))){
                    imagenesClickadas[0].isEnabled = false
                    imagenesClickadas[1].isEnabled = false
                    imagenesClickadas[0].tag="encontrada"
                    imagenesClickadas[1].tag="encontrada"
                    //sonido("good_ending")
                }else{
                    imagenesClickadas[0].setImageResource(R.drawable.oculto)
                    imagenesClickadas[1].setImageResource(R.drawable.oculto)
                    imagenesClickadas[0].isEnabled = true
                    imagenesClickadas[1].isEnabled = true
                    //sonido("bad_ending")
                }
                imagenesClickadas.clear()
            }
            if (hayVictoria()){
                congratulation()
            }
        },700)
    }

    private fun sonido(soundToPlay: String) {
        //obtengo el id del recurso que he pasado por parámetro
        val idResource:Int = resources.getIdentifier(soundToPlay,"raw",packageName)
        //hago sonarlo pasando el id del recurso
        val sound:MediaPlayer = MediaPlayer.create(this,idResource)
        sound.setVolume(1.0F,1.0F)
        sound.start()

        //hago que cuando haya terminado de sonar, pare y libere recursos
        sound.setOnCompletionListener {
            it.stop()
            it.release()
        }
    }

    /**
     * funcion para saber si dos imagenes son la misma instancia,
     * para que no se pueda dar como valido que se haga click sobre la misma imagen
     */
    private fun areTheSameImage(listaImagenes: ArrayList<ImageView>): Boolean{
        return listaImagenes[0].equals(listaImagenes[1])
    }

    private fun hayVictoria():Boolean{
        var victoria:Boolean = true
        for (i in 0 until contenedorImagenes.childCount){
            val linearLayout:LinearLayout = contenedorImagenes[i] as LinearLayout
            for (j in 0 until linearLayout.childCount){
                val currentImage:ImageView = (linearLayout[j] as ImageView)
                //si alguna imagen no tiene como tag encontrada es que
                // no se ha terminado el juego
                if(!currentImage.tag.equals("encontrada")){
                    victoria = false
                }
            }
        }
        return victoria
    }

    private fun congratulation(){
        imagen.visibility = View.VISIBLE
        this.bgSong.pause()
        sonido("win")
        Handler().postDelayed({
            // Hacer invisible
            bgSong.start()
            imagen.visibility = View.INVISIBLE

        }, 4000)
        resetear()
    }

    private fun resetear(){
        //randomizo la lista de nuevo
        imageList = imageList.shuffled()
        //vuelvo a generar la lista de listas con la nueva lista randomizada
        listOfLists = imageList.chunked(CANTIDAD_COLUMNAS)
        imagenesClickadas.clear()
        for (i in 0 until contenedorImagenes.childCount){
            val linearLayout:LinearLayout = contenedorImagenes[i] as LinearLayout
            for (j in 0 until linearLayout.childCount){
                val currentImage:ImageView = (linearLayout[j] as ImageView)
                currentImage.tag=listOfLists[i][j]
                currentImage.setImageResource(R.drawable.oculto)
                currentImage.isEnabled = true
            }
        }
    }
}