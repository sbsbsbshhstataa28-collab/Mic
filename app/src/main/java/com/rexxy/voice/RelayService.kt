package com.rexxy.voice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.IBinder

class RelayService : Service() {
    private var running = false
    private var record: AudioRecord? = null
    private val dsp = DspEngine()
    private val telegram = NativeTelegramRelayEngine()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel(); startForeground(7, notification())
        if (!running) startCapture()
        return START_STICKY
    }

    private fun startCapture() {
        val sr = 48000
        val min = AudioRecord.getMinBufferSize(sr, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        record = AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, sr, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, min * 2)
        running = true
        record!!.startRecording()
        Thread {
            val pcm = ShortArray(960)
            val f = FloatArray(960)
            while (running) {
                val n = record?.read(pcm, 0, pcm.size) ?: 0
                if (n > 0) {
                    for (i in 0 until n) f[i] = pcm[i] / 32768f
                    telegram.sendPcm(dsp.process(f.copyOf(n), sr), sr)
                }
            }
        }.start()
    }

    override fun onDestroy() { running=false; try { record?.stop() } catch (_: Exception) {}; record?.release(); record=null; super.onDestroy() }
    override fun onBind(intent: Intent?): IBinder? = null
    private fun createChannel() { getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel("relay", "Voice Relay", NotificationManager.IMPORTANCE_LOW)) }
    private fun notification(): Notification = Notification.Builder(this, "relay").setContentTitle("Rexxy Voice Relay").setContentText("Audio relay service running").setSmallIcon(android.R.drawable.ic_btn_speak_now).build()
}
