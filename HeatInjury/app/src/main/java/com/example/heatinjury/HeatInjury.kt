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
import org.w3c.dom.Text
import java.io.IOException

class HeatInjury : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heat_injury)
        val constraintLayout1 = findViewById<ConstraintLayout>(R.id.mainLayout1)
        constraintLayout1.setBackgroundResource(R.drawable.goose)

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

                    //get到熱傷害資訊後資訊後
                    //這邊抓資料的問題要問老師
                    val gson = Gson()
                    val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)

                    val CountyName = jsonObject
                        .getAsJsonObject("record")
                        .getAsJsonArray("Locations")
                        .asJsonObject
                        .get("CountyName")
                        .asString
                    println("縣市:${CountyName}")

                    val TimeArray = jsonObject
                        .getAsJsonObject("record")
                        .getAsJsonObject("Locations")
                        .getAsJsonArray("Location")[6]
                        .asJsonObject
                        .getAsJsonObject("Time")
                        .get("IssueTime")
                        .asString
                    println("時間:${TimeArray}")

                    //顯示在TEXTVIEW內
                    runOnUiThread{
                        findViewById<TextView>(R.id.textView).text = responseBody
                        findViewById<TextView>(R.id.textView2).text = TimeArray
                    }
                }
                else{
                    println("伺服器請求錯誤!")
                    runOnUiThread{
                        findViewById<TextView>(R.id.textView).text = "資料錯誤"
                    }
                }
            }
        })
    }
}