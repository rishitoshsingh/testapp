package com.alphae.testapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.camerakit.CameraKitView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class CameraActivity : AppCompatActivity() {

    private var cameraKitView: CameraKitView? = null
    private val TAG: String = "CameraActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        cameraKitView = findViewById(R.id.camera)


        camera_button.setOnClickListener {

            cameraKitView?.captureImage(CameraKitView.ImageCallback { cameraKitView, capturedImage ->
                val savedPhoto = File(Environment.getExternalStorageDirectory(), "photo.jpg")
                try {
                    val outputStream = FileOutputStream(savedPhoto.path)
                    outputStream.write(capturedImage)
                    outputStream.close()

                    Compressor(this)
                            .compressToFileAsFlowable(savedPhoto)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ file ->
                                val len = savedPhoto.length() - file.length()
                                Log.d(TAG, "actual Size ${savedPhoto.length()}")
                                Log.d(TAG, "compressed Size ${file.length()}")

                                upload(file)

//                                Toast.makeText(this@CameraActivity, "Compressed $len",Toast.LENGTH_LONG).show()
                            }, { throwable ->
                                throwable.printStackTrace()
                                Log.e(TAG, throwable.message)
                            })


                } catch (e: java.io.IOException) {
                    e.printStackTrace()
                }
            })

        }

    }

    private fun upload(file: File?) {
        Toast.makeText(this, "Uploading", Toast.LENGTH_LONG).show()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images/")
        val stream = FileInputStream(file)
        val uploadTask = imagesRef.putStream(stream)
        val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
                finish()
            }
            return@Continuation imagesRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent()
                intent.putExtra("uploadedUrl", task.result.toString())
                setResult(MainActivity().SELFIE_UPLOADED, intent)
                finish()
            } else {
                // Handle failures
                // ...
            }
        }.addOnFailureListener { exception -> Log.d(TAG, "Failed Upload", exception) }


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
        cameraKitView?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
