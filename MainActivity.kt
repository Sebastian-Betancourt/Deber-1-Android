package com.example.myapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * MainActivity.kt
 * Autor: Sebastian Betancourt
 * Curso: Aplicaciones Móviles - Deber 1 (2B)
 *
 * Cambios visibles:
 * - TIEMPO_VIBRACION reducido a 3000 ms
 * - Manejo básico de permisos CAMERA y ACCESS_FINE_LOCATION
 * - Mensajes de usuario usando strings
 */

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERM_REQ_CODE = 1001
        private const val VIBRATION_MS = 3000L // 3 segundos (personalizado por el autor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Botón para "Guardar reporte" (asegúrate que exista en tu layout con id btn_save)
        val btnSave = findViewById<Button>(R.id.btn_save)
        btnSave.setOnClickListener {
            if (hasRequiredPermissions()) {
                vibrateAndNotify()
                // Aquí podrías llamar la función que persiste el reporte
            } else {
                requestRequiredPermissions()
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        val cameraOk = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val locationOk = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return cameraOk && locationOk
    }

    private fun requestRequiredPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION),
            PERM_REQ_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERM_REQ_CODE) {
            val allGranted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                vibrateAndNotify()
            } else {
                Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun vibrateAndNotify() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_MS, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(VIBRATION_MS)
        }

        Toast.makeText(this, getString(R.string.report_saved), Toast.LENGTH_SHORT).show()
    }
}
