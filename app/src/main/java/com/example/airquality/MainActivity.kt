package com.example.airquality

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"
    private val key = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scan.setOnClickListener {
            scan.setText(R.string.scanning)
            scan.textSize = 30F
            val url = "https://api.airvisual.com/v2/nearest_city?key=$key"
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

        history.setOnClickListener{
            val intent = Intent(this, History::class.java)
            startActivity(intent)
        }
    }
}