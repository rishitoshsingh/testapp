package com.alphae.testapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.camerakit.CameraKit
import com.camerakit.CameraKitView

class Main2Activity : AppCompatActivity() {


    private lateinit var captureButton: Button
    private var cameraKitView: CameraKitView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        cameraKitView = view?.findViewById<CameraKitView>(R.id.camera2)
        cameraKitView?.sensorPreset = CameraKit.SENSOR_PRESET_NONE
        captureButton = view?.findViewById<Button>(R.id.capture_button2)!!
        captureButton.setOnClickListener {

            cameraKitView?.captureImage { p0, p1 ->
                val savedPhoto: File = File(Environment.getExternalStorageDirectory(), "photo.jpg")
                try {
                    val outputStream: FileOutputStream = FileOutputStream(savedPhoto.path)
                    outputStream.write(p1)
                    outputStream.close()
                } catch (e: java.io.IOException) {
                    e.printStackTrace()
                }
            }
        }


    }


    override fun onStart() {
        super.onStart()
        cameraKitView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        cameraKitView?.onResume()
    }

    override fun onPause() {
        cameraKitView?.onPause()
        super.onPause()
    }

    override fun onStop() {
        cameraKitView?.onStop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraKitView?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}
