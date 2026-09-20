package com.rexxy.voice

/**
 * Boundary for the Telegram MTProto + tgcalls native layer.
 * Telegram group-call joining requires a tgcalls-generated WebRTC payload and
 * phone.joinGroupCall; see the project README before enabling this adapter.
 */
interface TelegramRelayEngine {
    suspend fun login(phone: String)
    suspend fun joinVoiceChat(chatId: Long): Result<Unit>
    suspend fun leaveVoiceChat()
    fun sendPcm(samples: FloatArray, sampleRate: Int)
    fun isConnected(): Boolean
}

class NativeTelegramRelayEngine : TelegramRelayEngine {
    override suspend fun login(phone: String) = Unit
    override suspend fun joinVoiceChat(chatId: Long): Result<Unit> = Result.failure(UnsupportedOperationException("Native tgcalls/TDLib adapter must be linked"))
    override suspend fun leaveVoiceChat() = Unit
    override fun sendPcm(samples: FloatArray, sampleRate: Int) {}
    override fun isConnected() = false
}
