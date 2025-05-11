package com.zipper.magicimage

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imageView = findViewById<MagicImageView>(R.id.magicImageView)
        val imageView2 = findViewById<MagicImageView>(R.id.magicImageView2)
        Glide.with(this)
//            .load("file:///android_asset/images/pag.pag")
            .load("https://svga.dev/assets/svga/index-response.svga")
            .into(imageView)
        Glide.with(this)
//            .load("file:///android_asset/images/pag.pag")
            //.load("https://svga.dev/assets/svga/index-response.svga")
            .load("file:///android_asset/images/rose.svga")
            .into(imageView2)

        imageView.addAnimateListener { intent ->
            when(intent) {
                is AnimateStartIntent -> {
                    Log.d("MagicImage", "animateStart")
                }
                is AnimateEndIntent ->{
                    Log.d("MagicImage", "animateEnd")
                }
                is AnimateRepeatIntent -> {
                    Log.d("MagicImage", "animateRepeat")
                }
                is AnimateUpdateIntent -> {
                    Log.d("MagicImage", "animateUpdate")
                }

                else -> Unit
            }
        }
    }
}