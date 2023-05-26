package com.example.heatinjury

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.IOException

class HeatInjury : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heat_injury)
        val constraintLayout1 = findViewById<ConstraintLayout>(R.id.mainLayout1)
        constraintLayout1.setBackgroundResource(R.drawable.night)

        //回到主頁面
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        //開始到政府開放平台get外匯資料
        val client = OkHttpClient()
        val url = "https://opendata.cwb.gov.tw/api/v1/rest/datastore/M-A0085-001?Authorization=CWB-4DAAE268-702B-4649-A3E3-AD474B4A8CA3&limit=5&CountyName=%E5%BD%B0%E5%8C%96%E7%B8%A3&sort=IssueTime"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {

                if(response.isSuccessful){
                    val responseBody = response.body?.string()
                    println(responseBody)
                    val jsonObject = JSONObject(responseBody)

                    val RecordObject = jsonObject.getJSONObject("records")
                    val LocationsArray = RecordObject.getJSONArray("Locations")
                    val LocationArray = LocationsArray.getJSONObject(0).getJSONArray("Location")
                    var i = 0

                    //將彰化市的陣列抓出來
                    while( i <= 26) {
                        var TownName = LocationArray.getJSONObject(i).get("TownName").toString()
                        if (TownName == "彰化市"){
                            runOnUiThread{
                                findViewById<TextView>(R.id.textView2).text = "彰化縣，$TownName 的熱指數"
                            }

                        val TimeArray = LocationArray.getJSONObject(i).getJSONArray("Time")

                        //將時間、熱指數以及提示印出來
                        for(j in 0 until 6){

                            val textViewId = "Time$j"
                            var IssueTime = TimeArray.getJSONObject(j).get("IssueTime").toString()
                            val WeatherElementsArray = TimeArray.getJSONObject(j).getJSONObject("WeatherElements")
                            val HeatInjuryIndex = WeatherElementsArray.get("HeatInjuryIndex").toString()
                            val HeatInjuryWarning = WeatherElementsArray.get("HeatInjuryWarning").toString()
                            runOnUiThread{
                                val resourceId = resources.getIdentifier(textViewId, "id", packageName)
                                val textView = findViewById<TextView>(resourceId)
                                textView.text = "時間:$IssueTime  熱指數:$HeatInjuryIndex  $HeatInjuryWarning"
                                }
                            }
                            break
                        }
                        else {
                            i++
                        }
                    }
                }
                else{
                    println("伺服器請求錯誤!")
                    runOnUiThread{
                        findViewById<TextView>(R.id.textView2).text = "資料錯誤"
                    }
                }
            }
        })
    }
}