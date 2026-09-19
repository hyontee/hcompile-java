package com.brilliant.game

import org.json.JSONObject

interface GUIWrapper {
    fun onShow(data: JSONObject?)
    fun onClose()
    fun receiveUIpacket(data: JSONObject?)
}
