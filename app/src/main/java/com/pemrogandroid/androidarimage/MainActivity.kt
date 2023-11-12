package com.pemrogandroid.androidarimage


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
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
    lateinit var sceneView:ArSceneView // Create a reference to the AR scene view
    lateinit var videoNode: VideoNode // Create a reference to the VideoNode for playing video
    lateinit var mediaPlayer: MediaPlayer // Create a reference to the MediaPlayer for handling video playback
    lateinit var websiteButton: Button //setup for button
    var isVideoPlaying: Boolean = false //flag to check if video is played
    var tracking: Boolean=false



    /*
        - ArModelNode is a class from the SceneView library for Android AR development.
          It is used to represent a node in an Augmented Reality (AR) scene that can hold a 3D model.
        - VideoNode allow us to apply video as a texture on a 'screen' that is used
    */


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)// Set the content view to the main layout



        // Initialize the AR scene view and configure it
        sceneView = findViewById<ArSceneView?>(R.id.SceneView).apply {
            this.lightEstimationMode = Config.LightEstimationMode.DISABLED //Disable light estimation
            this.planeRenderer.isVisible = true // Make the plane renderer visible
        }

        // Initialize the MediaPlayer and load a video from resources
        mediaPlayer= MediaPlayer.create(this,R.raw.videoadv)

        // Initialize the websiteButton
        websiteButton = findViewById(R.id.websiteButton)


        // Add an AugmentedImageNode to the scene view, representing the target image
        sceneView.addChild(AugmentedImageNode(
            engine = sceneView.engine,
            imageName = "video",
            bitmap = this.assets.open("models/kartuNama.png")
                .use(BitmapFactory::decodeStream),
            onUpdate = { node, _ ->
                if (node.isTracking) {// If the image is being tracked
                    tracking=true
                    if (!videoNode.player.isPlaying) {
                        sceneView.planeRenderer.isVisible = false // Hide the plane renderer
                        startVideo()// Start playing the video
                    }
                    if (videoNode.parent == null) {
                        node.addChild(videoNode)
                    }
                } else {// If the image is not being tracked
                    tracking=false
                    if (videoNode.player.isPlaying) {
                        sceneView.planeRenderer.isVisible = true// Show the plane renderer
                        pauseVideo()// Stop playing video
                        videoNode.parent?.removeChild(videoNode)
                    }
                }
            }
        ).apply {
            // Create a VideoNode for displaying the video

            videoNode=VideoNode(engine=sceneView.engine,
                player = mediaPlayer,
                scaleToUnits = 0.2f,
                centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f),
                glbFileLocation = "models/plane.glb",
                onLoaded = { _, _->
                // // Handle video node loaded event if needed
            })
            addChild(videoNode)// Add the video node as a child of the AugmentedImageNode

        })

        // Set an OnClickListener for the button
        websiteButton.setOnClickListener {
            if (isVideoPlaying) {
                pauseVideo()//pause the video when opening website
            }
            openWebsite()
        }


    }

    //Start the video
    private fun startVideo() {
        isVideoPlaying = true
        mediaPlayer.start()
    }


    //Pause video
    private fun pauseVideo() {
        isVideoPlaying = false
        mediaPlayer.pause()
    }
    override fun onPause() {
        super.onPause()

        // Pause the video when the app is not in the foreground
        if (videoNode.player.isPlaying) {
            pauseVideo()
        }
    }
    override fun onResume() {
        super.onResume()

        // Logic to start or pause the video based on tracking
        if (tracking && !videoNode.player.isPlaying) {
            startVideo()
        } else if (!tracking && videoNode.player.isPlaying) {
            pauseVideo()
        }
    }

    // Function to open the website when the button is clicked
    private fun openWebsite() {
        val websiteUrl = "https://miland.co.id/" // Replace with your website URL
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
        startActivity(intent)
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()// Release the MediaPlayer resources when the activity is destroyed
    }

}