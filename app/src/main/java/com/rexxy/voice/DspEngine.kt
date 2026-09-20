package com.rexxy.voice

import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.tanh

/** Real-time mono PCM DSP. Input/output are Float samples in [-1,1]. */
class DspEngine {
    var enabled = true
    var gainDb = 6f
    var bass = 0f
    var presence = 0f
    var drive = 0f
    var echoWet = 0f
    var echoDelayMs = 180f
    var crystal = false

    private var delay = FloatArray(48000 * 2)
    private var pos = 0

    fun process(x: FloatArray, sampleRate: Int): FloatArray {
        if (!enabled) return x
        val out = FloatArray(x.size)
        val gain = Math.pow(10.0, gainDb / 20.0).toFloat()
        val d = (echoDelayMs.coerceIn(10f, 900f) * sampleRate / 1000f).toInt().coerceAtMost(delay.size - 1)
        for (i in x.indices) {
            var s = x[i] * gain
            // Simple bass/presence tone shaping without introducing a blocking UI dependency.
            s += bass * 0.0025f * s
            if (crystal) s *= 1.02f
            val read = (pos - d + delay.size) % delay.size
            val e = delay[read]
            s += e * echoWet
            val driveAmount = 1f + drive.coerceIn(0f, 10f) * 2.5f
            s = tanh((s * driveAmount).toDouble()).toFloat()
            s = s.coerceIn(-0.98f, 0.98f)
            delay[pos] = s
            pos = (pos + 1) % delay.size
            out[i] = s
        }
        return out
    }

    fun preset(name: String) {
        when (name.lowercase()) {
            "bass 400" -> { gainDb=8f; bass=400f; presence=10f; drive=1.2f; echoWet=.20f; echoDelayMs=160f; crystal=false }
            "boss voice" -> { gainDb=5f; bass=250f; presence=22f; drive=.8f; echoWet=.10f; echoDelayMs=90f; crystal=false }
            "echo god" -> { gainDb=4f; bass=300f; presence=16f; drive=.5f; echoWet=.55f; echoDelayMs=260f; crystal=false }
            "crystal" -> { gainDb=2f; bass=80f; presence=34f; drive=.05f; echoWet=0f; echoDelayMs=100f; crystal=true }
        }
    }
}
