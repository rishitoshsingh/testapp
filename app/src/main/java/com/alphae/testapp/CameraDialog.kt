package com.alphae.testapp

import android.app.Dialog
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.TextView
import com.camerakit.CameraKit
import com.camerakit.CameraKitView
import java.io.File
import java.io.FileOutputStream


/**
 * Created by rishi on 2/5/19.
 */
class CameraDialog : android.support.v4.app.DialogFragment() {

    private lateinit var captureButton: Button
    private var cameraKitView: CameraKitView? = null

    companion object {

        fun newInstance(): CameraDialog {
            return CameraDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CameraDialogStyle)
        val bundle = this.arguments
        if (bundle != null) {
//            mYear = bundle.getInt("year", 2015)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.camera_dialog, null)
        cameraKitView = view?.findViewById<CameraKitView>(R.id.camera)
//        cameraKitView?.sensorPreset = CameraKit.SENSOR_PRESET_NONE
        captureButton = view?.findViewById<Button>(R.id.capture_button)!!
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

            dialog.dismiss()
        }

        return AlertDialog.Builder(this.activity!!)
                .setView(view)
                .setCancelable(true)
                .create()

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