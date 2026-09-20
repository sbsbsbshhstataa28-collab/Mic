package com.rexxy.voice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private val dsp = DspEngine()
    private lateinit var chat: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(24,24,24,24) }
        fun label(t:String)=TextView(this).apply { text=t; textSize=16f; setPadding(0,12,0,4) }
        root.addView(label("Rexxy Voice Relay"))
        root.addView(label("Telegram account/session"))
        val phone=EditText(this).apply { hint="Phone number (+countrycode...)"; inputType=3 }
        root.addView(phone)
        root.addView(label("Voice chat / group Chat ID"))
        chat=EditText(this).apply { hint="-1001234567890"; inputType=2 }
        root.addView(chat)
        root.addView(label("Preset"))
        val presets=listOf("Bass 400","Boss Voice","Echo God","Crystal")
        val spinner=Spinner(this).apply { adapter=ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item,presets) }
        root.addView(spinner)
        spinner.onItemSelectedListener=object: android.widget.AdapterView.OnItemSelectedListener { override fun onNothingSelected(p:android.widget.AdapterView<*>?){}; override fun onItemSelected(p:android.widget.AdapterView<*>?,v:android.view.View?,pos:Int,id:Long){dsp.preset(presets[pos])} }
        val start=Button(this).apply { text="START RELAY" }
        val stop=Button(this).apply { text="STOP" }
        root.addView(start); root.addView(stop)
        val status=TextView(this).apply { text="Status: idle"; setPadding(0,18,0,0) }
        root.addView(status)
        start.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED) { ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.RECORD_AUDIO),44); return@setOnClickListener }
            status.text="Status: capture started; Telegram native adapter required for VC publish"
            startService(Intent(this,RelayService::class.java))
        }
        stop.setOnClickListener { stopService(Intent(this,RelayService::class.java)); status.text="Status: stopped" }
        setContentView(root)
    }
}
