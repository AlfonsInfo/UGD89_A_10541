package com.alfonsus.ugd89_a_10541_project1


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.hardware.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity(), SensorEventListener  {
    //Camera
    private var mCamera: Camera? = null
    private var mCameraView : CameraView? = null
    private var currentCameraId : Int  = Camera.CameraInfo.CAMERA_FACING_BACK
    //Proximity
    lateinit var proximitySensor: Sensor
    lateinit var sensorManager : SensorManager
    private val CHANNEL_ID_1 = "channel_notification_01"
    private val notificationId1 = 101


    lateinit var sensorStatusTV: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorStatusTV = findViewById(R.id.idTVSensorStatus)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        setUpSensorStuff()
        createNotificationChannel()

        if (proximitySensor == null) {
            Toast.makeText(this, "No proximity sensor found in device..", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            sensorManager.registerListener(
                proximitySensorEventListener,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        try{
            mCamera = Camera.open()
        }catch (e: Exception)
        {
            Log.d("Error", "Failed to get Camera " + e.message)
        }
        if(mCamera !=null)
        {
            mCameraView = CameraView(applicationContext, mCamera!!)
            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView(mCameraView)

            @SuppressLint("MissingInflateId", "LocalSuppress") val imageClose =
                findViewById<View>(R.id.imgClose) as ImageButton
            imageClose.setOnClickListener{view: View? -> System.exit(0)}
        }
    }
    fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
// Specify the sensor you want to listen to
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    private fun sendNotification(){
        val builder = NotificationCompat.Builder(this, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Modul89_A_10541_Project 2")
            .setContentText("Selamat anda sudah berhasil mengerjakan Modul 8 dan 9 ")
            .setPriority(NotificationCompat.PRIORITY_LOW)

        with(NotificationManagerCompat.from(this)){
            notify(notificationId1, builder.build())
        }
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"

            val channel1 = NotificationChannel(CHANNEL_ID_1, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }

    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // method to check accuracy changed in sensor.
        }

        override fun onSensorChanged(event: SensorEvent) {
            // check if the sensor type is proximity sensor.
            if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0f) {
                    if(mCameraView != null)
                    {
                        mCamera?.stopPreview()
                    }
                    mCamera?.release()
                    if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    }
                    else {
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    }
                    mCamera = Camera.open(currentCameraId);
                    if(mCamera !=null)
                    {
                        mCameraView = CameraView(applicationContext, mCamera!!)
                        val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
                        camera_view.addView(mCameraView)
                        
                    }
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            //Log.d("Main", "onSensorChanged: sides ${event.values[0]}
            //front/back ${event.values[1]} ")
            // Sides = Tilting phone left(10) and right(-10)
            val sides = event.values[0]
            val upDown = event.values[1]




            if (!(upDown.toInt() == 0 && sides.toInt() == 0))
            {
                Log.d("Test", "Masuk If" )
                sendNotification()
            }

        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }


}

