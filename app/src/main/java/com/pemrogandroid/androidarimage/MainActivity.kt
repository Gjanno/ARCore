package com.pemrogandroid.androidarimage


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isGone
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.math.Vector3
import dev.romainguy.kotlin.math.rotation
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.localRotation
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.node.Node
import io.github.sceneview.node.VideoNode
import com.google.ar.core.Session
import com.google.ar.core.AugmentedImage
import io.github.sceneview.ar.node.AugmentedImageNode


class MainActivity : AppCompatActivity() {
    lateinit var sceneView:ArSceneView
    lateinit var videoNode: VideoNode
    lateinit var mediaPlayer: MediaPlayer

    /*
        - ArModelNode is a class from the SceneView library for Android AR development.
          It is used to represent a node in an Augmented Reality (AR) scene that can hold a 3D model.
        - VideoNode allow us to apply video as a texture on a 'screen' that is used
    */


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sceneView = findViewById<ArSceneView?>(R.id.SceneView).apply {
            this.lightEstimationMode = Config.LightEstimationMode.DISABLED
            this.planeRenderer.isVisible = true
        }

        mediaPlayer= MediaPlayer.create(this,R.raw.videoadv)

        sceneView.addChild(AugmentedImageNode(
            engine = sceneView.engine,
            imageName = "video",
            bitmap = this.assets.open("models/kartuNama.png")
                .use(BitmapFactory::decodeStream),
            onUpdate = { node, _ ->
                if (node.isTracking) {
                    if (!videoNode.player.isPlaying) {
                        sceneView.planeRenderer.isVisible = false

                        videoNode.player.start()


                    }
                } else {
                    if (videoNode.player.isPlaying) {
                        sceneView.planeRenderer.isVisible = true
                        videoNode.player.pause()
                    }
                }
            }
        ).apply {

            videoNode=VideoNode(engine=sceneView.engine,player = mediaPlayer, scaleToUnits = 0.2f, centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f),glbFileLocation = "models/plane.glb", onLoaded = { _, _->

            })
            addChild(videoNode)
        })


    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

}