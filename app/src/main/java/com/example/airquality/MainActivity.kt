package com.example.airquality

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"
    private val key = ""
    private val PERMISSIONS_REQUEST = 1
    private val REQUEST_CHECK_SETTINGS = 2
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scan.setOnClickListener {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            if (haveFinePermission())  {
                val locationBalanced = LocationRequest.create()
                locationBalanced.priority = PRIORITY_HIGH_ACCURACY
                locationBalanced.interval = 1000
                val builder = LocationSettingsRequest.Builder().addLocationRequest(locationBalanced)
                val result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
                result.addOnCompleteListener{
                    try {
                        result.getResult(ApiException::class.java)
                        scan.setText(R.string.scanning)
                        scan.textSize = 30F
                        val source = CancellationTokenSource()
                        val token = source.token
                        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, token).addOnCanceledListener {
                            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
                        }.addOnSuccessListener { location ->
                            scan(location)
                        }
                    } catch (e : ApiException) {
                        when (e.statusCode) {
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                                (e as ResolvableApiException).startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                            } catch (e : IntentSender.SendIntentException) {
                                Log.e(tag, "something went wrong")
                            }
                        }
                    }
                }
            } else {
                requestPermissions()
            }
        }

        history.setOnClickListener{
            val intent = Intent(this, History::class.java)
            startActivity(intent)
        }
    }

    private fun scan(location : Location) {
        val lat = location.latitude
        val lon = location.longitude
        val url = "https://api.airvisual.com/v2/nearest_city?lat=$lat&lon=$lon&key=$key"
        val queue = Volley.newRequestQueue(this)
        val future : RequestFuture<JSONObject> = RequestFuture.newFuture()
        val jsonRequest = JsonObjectRequest(Request.Method.GET, url, JSONObject(), future, future)
        queue.add(jsonRequest)
        Thread(Runnable{
            try {
                val response = future.get(30, TimeUnit.SECONDS)
                val status = response.getString("status")
                var AQI = "0"
                var date = "Unknown"
                var city = "Unknown"
                if (status == "success") {
                    val data = response.getJSONObject("data")
                    city = data.getString("city")
                    val currentInfo = data.getJSONObject("current")
                    val pollutionInfo = currentInfo.getJSONObject("pollution")
                    AQI = pollutionInfo.getString("aqius")
                    date = pollutionInfo.getString("ts").substring(0, 10) //cutting out xxxx-xx-xx
                } else {
                    Log.e(tag, "Status code: $status")
                }

                val intent = Intent(this, SaveScan::class.java)
                intent.putExtra("AQI", AQI)
                intent.putExtra("city", city)
                intent.putExtra("date", date)
                startActivity(intent)
            } catch (e: TimeoutException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }).start()
    }

    private fun haveFinePermission() : Boolean{
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST-> {
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Enable GPS services to scan AQI", Toast.LENGTH_LONG).show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        //OK!
                    }
                    Activity.RESULT_CANCELED -> {
                        Toast.makeText(this, "Turn on GPS to scan AQI", Toast.LENGTH_LONG).show()
                    }
                }
            }
            else -> {

            }
        }
    }
}