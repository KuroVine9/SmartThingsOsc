package com.kuro9.iot.service

import com.illposed.osc.OSCMessage
import com.illposed.osc.OSCPacketListener
import com.illposed.osc.transport.*
import com.kuro9.iot.config.AppConfig
import com.kuro9.iot.utils.infoLog
import org.springframework.stereotype.Service
import java.net.InetAddress
import java.net.InetSocketAddress

@Service
class OSCHandler(config: AppConfig) {

    private val rxPort = config.oscRxPort
    private val txPort = config.oscTxPort

    private val oscTX = OSCPortOut(InetSocketAddress(InetAddress.getLocalHost(), txPort))
    private val oscRX = OSCPortIn(InetSocketAddress(InetAddress.getLocalHost(), rxPort))

//    private var nowListener: OSCPacketListener = object : OSCPacketListener {
//        override fun handlePacket(event: OSCPacketEvent?) {
//            val message = event!!.packet as OSCMessage
//            broadcast(message)
//            println("${message.address}: ${message.arguments.firstOrNull()}")
//        }
//
//        override fun handleBadData(event: OSCBadDataEvent?) {
//            println("bad data?")
//        }
//
//    }

    fun <T> sendMsg(path: String, payload: T) {
        val msg = OSCMessage(path, listOf(payload))
        oscTX.send(msg)
        infoLog("TX: {}", path)
    }

    fun addListener(listener: OSCPacketListener) {
        with(oscRX) {
            addPacketListener(listener)
            if(isListening.not()) startListening()
        }
        infoLog("add Listener: {}", listener::class.qualifiedName)
    }
}