package com.example.heatinjury


import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {
    private var items: ArrayList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbrw: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val constraintLayout = findViewById<ConstraintLayout>(R.id.mainLayout)
        constraintLayout.setBackgroundResource(R.drawable.duck)

        //切換到熱傷害layout
        val button = findViewById<Button>(R.id.button2)
        button.setOnClickListener{
            startActivity(Intent(this, HeatInjury::class.java))
        }

        dbrw = SQlite(this).writableDatabase
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        findViewById<ListView>(R.id.listView).adapter = adapter
        setUpListeners()
    }

    override fun onDestroy() {
        dbrw.close()
        super.onDestroy()
    }

    private fun setUpListeners(){

        //Time只能輸入類似05/01,21:00(五月一日晚上九點)的形式
        val hi_Time = findViewById<EditText>(R.id.hi_Time)
        val hi_HeatInjuryIndex = findViewById<EditText>(R.id.hi_HeatInjuryIndex)
        val regex1 = Regex("""^\d{2}/\d{2},\d{2}:\d{2}$""")
        val regex2 = Regex("""^\d{2}$""")

            //新增資料
        findViewById<Button>(R.id.button_Insert).setOnClickListener{
            val input1 = hi_Time.text.toString()
            val isValidFormat1 = regex1.matches(input1)
            val input2 = hi_HeatInjuryIndex.text.toString()
            val isValidFormat2 = regex2.matches(input2)

            if(!isValidFormat1 || !isValidFormat2){
                showToast("請輸入時間格式如05/01,21:00(五月一日晚上九點)或請輸入熱指數")
            }
            else try{
                dbrw.execSQL(
                    "INSERT INTO myTable(time, heatinjuryindex) VALUES(?,?)",
                    arrayOf(
                        hi_Time.text.toString(),
                        hi_HeatInjuryIndex.text.toString()
                    )
                )
                showToast("新增時間:${hi_Time.text},熱指數:${hi_HeatInjuryIndex.text}")
                cleanEditText()
            }catch (e: Exception){
                showToast("新增失敗:$e")
            }
        }

        //更新資料
        findViewById<Button>(R.id.button_Upgrade).setOnClickListener{
            val input1 = hi_Time.text.toString()
            val isValidFormat1 = regex1.matches(input1)
            val input2 = hi_HeatInjuryIndex.text.toString()
            val isValidFormat2 = regex2.matches(input2)

            if(!isValidFormat1 || !isValidFormat2){
                showToast("請輸入正確的時間格式或是請輸入熱指數")
            }
            else try{
                dbrw.execSQL("UPDATE myTable SET heatinjuryindex = ${hi_HeatInjuryIndex.text.toString().toInt()} WHERE time LIKE '${hi_Time.text}'")
                showToast("更新時間:${hi_Time.text},熱指數:${hi_HeatInjuryIndex.text}")
                cleanEditText()
            }catch(e: Exception){
                showToast("更新失敗:$e")
            }
        }

        //刪除資料
        findViewById<Button>(R.id.button_Delete).setOnClickListener{
            val input1 = hi_Time.text.toString()
            val isValidFormat1 = regex1.matches(input1)

            if(!isValidFormat1){
                showToast("請輸入正確的時間格式")
            }
            else try{
                dbrw.execSQL("DELETE FROM myTable WHERE time LIKE '${hi_Time.text}'")
                showToast("刪除:${hi_Time.text},熱指數:${hi_HeatInjuryIndex.text}")
                cleanEditText()
            }catch(e: Exception){
                showToast("刪除失敗:$e")
            }
        }

        //查詢資料
        findViewById<Button>(R.id.button_Select).setOnClickListener{
            val input = hi_Time.text.toString()
            val isValidFormat = regex1.matches(input)

            val queryString = if(!isValidFormat){
                "SELECT * FROM myTable"
            }
            else{
                "SELECT * FROM myTable WHERE time LIKE '${hi_Time.text}'"
            }

            val c = dbrw.rawQuery(queryString, null)
            c.moveToFirst()
            items.clear()
            showToast("共有${c.count}筆資料")
            for( i in 0 until c.count){
                items.add("時間:${c.getString(0)}\t\t\t\t 熱指數:${c.getInt(1)}")
                c.moveToNext()    
            }
            adapter.notifyDataSetChanged()
            c.close()
        }
    }

    private fun showToast(text : String){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun cleanEditText(){
        findViewById<EditText>(R.id.hi_Time).setText("")
        findViewById<EditText>(R.id.hi_HeatInjuryIndex).setText("")
    }



}