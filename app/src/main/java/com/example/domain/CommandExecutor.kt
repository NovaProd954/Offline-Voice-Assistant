package com.example.domain

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import android.net.wifi.WifiManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager

class CommandExecutor(private val context: Context) {

    fun execute(command: String): String {
        return when {
            command.contains("open camera") -> openCamera()
            command.contains("turn on flashlight") -> toggleFlashlight(true)
            command.contains("turn off flashlight") -> toggleFlashlight(false)
            command.contains("open whatsapp") -> openApp("com.whatsapp", "WhatsApp")
            command.contains("call") -> callContact(command)
            command.contains("open gallery") -> openGallery()
            command.contains("turn on bluetooth") -> toggleBluetooth(true)
            command.contains("turn off bluetooth") -> toggleBluetooth(false)
            command.contains("turn on wifi") || command.contains("turn on wi-fi") -> toggleWifi(true)
            command.contains("turn off wifi") || command.contains("turn off wi-fi") -> toggleWifi(false)
            else -> "Command not supported: $command"
        }
    }

    private fun openCamera(): String {
        return try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            "Opening camera"
        } catch (e: Exception) {
            "Failed to open camera"
        }
    }

    private fun toggleFlashlight(turnOn: Boolean): String {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull()
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId, turnOn)
                "Flashlight turned ${if (turnOn) "on" else "off"}"
            } else {
                "No camera available for flashlight"
            }
        } catch (e: Exception) {
            "Failed to toggle flashlight"
        }
    }

    private fun openApp(packageName: String, appName: String): String {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        return if (intent != null) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            "Opening $appName"
        } else {
            "$appName is not installed"
        }
    }

    private fun callContact(command: String): String {
        // A simple implementation since contact name parsing and searching is complex.
        // We will just open the dialer for "call [number]" or just generic dialer.
        val intent = Intent(Intent.ACTION_DIAL)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        return "Opening dialer for call"
    }

    private fun openGallery(): String {
        return try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.type = "image/*"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            "Opening gallery"
        } catch (e: Exception) {
            "Failed to open gallery"
        }
    }

    private fun toggleBluetooth(turnOn: Boolean): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Modern API: Need to direct user to settings as apps can't toggle BT silently as easily anymore
            return try {
                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                "Opening Bluetooth settings"
            } catch (e: Exception) {
                "Failed to open Bluetooth settings"
            }
        } else {
            val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
            val bluetoothAdapter = bluetoothManager?.adapter
            return if (bluetoothAdapter == null) {
                "Bluetooth not supported on this device"
            } else {
                if (turnOn) {
                    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                    "Requesting Bluetooth be turned on"
                } else {
                    // Turn off bluetooth (deprecated but might work on older OS)
                    val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                    "Opening Bluetooth settings to turn off"
                }
            }
        }
    }

    private fun toggleWifi(turnOn: Boolean): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ restricts toggling wifi in background, use Settings Panel
            try {
                val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
                panelIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(panelIntent)
                "Opening Wi-Fi panel"
            } catch (e: Exception) {
                "Failed to access Wi-Fi panel"
            }
        } else {
            @Suppress("DEPRECATION")
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.isWifiEnabled = turnOn
            "Wi-Fi turned ${if (turnOn) "on" else "off"}"
        }
    }
}
