package com.pargar.kidsevolution.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.google.ar.core.Anchor
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.pargar.kidsevolution.R
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.Toast
import com.google.ar.sceneform.assets.RenderableSource

class Reality : Fragment() {

    private lateinit var arFragment: ArFragment
    private var modelRenderable: ModelRenderable? = null
    private var modelIsPlaced = false
    private var renderable: ModelRenderable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reality, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arFragment = childFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment

        downloadModel("concrete_cat.glb")
        arFragment.setOnTapArPlaneListener{ hitResult, plane, motionEvent ->
            if(renderable == null){return@setOnTapArPlaneListener}
            val anchor: Anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment.arSceneView.scene)
            val node = TransformableNode(arFragment.transformationSystem)
            node.renderable = renderable
            node.scaleController.minScale = 0.06f
            node.scaleController.maxScale = 1.0f
            node.worldScale = Vector3(0.5f, 0.5f, 0.5f)
            node.setParent(anchorNode)
            node.select()
        }
        //loadModel("concrete_cat.glb")

        /*loadModel("concrete_cat.glb") {
            // Este callback se llama cuando el modelo ya está cargado
            Log.d("RealityFragment", "Modelo cargado correctamente.")

            arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
                if (!modelIsPlaced && modelRenderable != null) {
                    Log.d("RealityFragment", "Colocando modelo...")
                    val anchor = hitResult.createAnchor()
                    placeModel(anchor, modelRenderable!!)
                    modelIsPlaced = true
                } else {
                    Log.d("RealityFragment", when {
                        modelIsPlaced -> "Modelo ya fue colocado"
                        else -> "Modelo no cargado aún"
                    })
                }
            }
        }

        arFragment.arSceneView.scene.addOnUpdateListener {
            if (arFragment.arSceneView.arFrame?.camera?.trackingState == TrackingState.TRACKING) {
                // Ya está listo para colocar modelos
            }
        }

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            Log.d("RealityFragment", "Plano tocado, intentando colocar modelo.")
            if (modelRenderable != null && !modelIsPlaced) {
                val anchor = hitResult.createAnchor()
                placeModel(anchor, modelRenderable!!)
                modelIsPlaced = true
            } else if (modelRenderable == null) {
                Log.d("RealityFragment", "Modelo aún no cargado.")
            }
        }*/
    }

    private fun loadModel(fileName: String, onModelReady: () -> Unit) {
        try {
            // Verifica que el archivo existe en assets
            val inputStream = requireContext().assets.open(fileName)
            inputStream.close()

            ModelRenderable.builder()
                .setSource(requireContext(), Uri.parse("file:///android_asset/$fileName"))
                .build()
                .thenAccept {
                    modelRenderable = it
                    onModelReady()
                    Log.d("RealityFragment", "Modelo cargado exitosamente: $fileName")
                }
                .exceptionally {
                    Log.e("RealityFragment", "Error al cargar el modelo $fileName", it)
                    null
                }
        } catch (e: Exception) {
            Log.e("RealityFragment", "No se pudo encontrar el archivo $fileName en assets", e)
        }
    }

    fun downloadModel(fileName: String) {
        var renderableSource = RenderableSource.Builder()
            .setSource(requireContext(), Uri.parse("file:///android_asset/$fileName"), RenderableSource.SourceType.GLB)
            .setRecenterMode(RenderableSource.RecenterMode.CENTER)
            .build()

        ModelRenderable.builder()
            .setSource(requireContext(), renderableSource)
            .build()
            .thenAccept { modelRenderable ->
                renderable = modelRenderable
                val toast = Toast.makeText(requireContext(), "Descarga completa, Toque una superficie", Toast.LENGTH_LONG)
                toast.show()
            //Log.d("RealityFragment", "Modelo cargado exitosamente: $fileName")
            }
            .exceptionally { throwable ->
                val toast = Toast.makeText(requireContext(), "No pudo descargarse el elemento 3D", Toast.LENGTH_LONG)
                //Log.e("RealityFragment", "Error al cargar el modelo $fileName", it)
                toast.show()
                return@exceptionally null
            }
    }


    private fun placeModel(anchor: Anchor, modelRenderable: ModelRenderable) {
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(arFragment.arSceneView.scene)

        val modelNode = TransformableNode(arFragment.transformationSystem)
        modelNode.setParent(anchorNode)
        modelNode.renderable = modelRenderable
        modelNode.select()
        modelNode.localScale = Vector3(1.0f, 1.0f, 1.0f)
    }
}

