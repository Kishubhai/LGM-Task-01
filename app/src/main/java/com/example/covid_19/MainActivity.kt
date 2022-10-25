package com.example.covid_19

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class MainActivity : AppCompatActivity() {
    lateinit var worldCasesTV: TextView
    lateinit var worldRecoverdTV: TextView
    lateinit var worldDeathsTV: TextView
    lateinit var countryCasesTV: TextView
    lateinit var countryRecoveredTV: TextView
    lateinit var countryDeathstv: TextView
    lateinit var stateRV: RecyclerView
    lateinit var stateRVAdapter: StateRVAdapter
    lateinit var stateList: List<StateModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       countryCasesTV=findViewById(R.id.idTVIndiaCases)
        countryDeathstv=findViewById(R.id.idTVIndiaDeaths)
        countryRecoveredTV=findViewById(R.id.idTVIndiaRecovered)
        stateRV=findViewById(R.id.idRVStates)
        stateList=ArrayList<StateModel>()
        getStateInfo()

    }
    private fun getStateInfo() {

        val url="https://api.rootnet.in/covid19-in/stats/latest"
        val queue =Volley.newRequestQueue(this@MainActivity)
        val request=
            JsonObjectRequest(Request.Method.GET,url,null,{
                response->
                try {
                    val dataObj =response.getJSONObject("data")
                    val summaryObj=dataObj.getJSONObject("summary")
                    val cases:Int = summaryObj.getInt("total")
                    val recovered:Int=summaryObj.getInt("discharged")
                    val deaths:Int=summaryObj.getInt("deaths")

                    countryCasesTV.text=cases.toString()
                    countryRecoveredTV.text=recovered.toString()
                    countryDeathstv.text=deaths.toString()

                    val regionalArray =dataObj.getJSONArray("regional")
                    for(i in 0 until regionalArray.length()){
                        val reginalObj = regionalArray.getJSONObject(i)
                        val stateName:String =reginalObj.getString("loc")
                        val cases:Int =reginalObj.getInt("totalConfirmed")
                        val deaths:Int =reginalObj.getInt("deaths")
                        val recovered:Int =reginalObj.getInt("discharged")

                        val stateModel = StateModel(stateName,recovered,deaths,cases)
                        stateList=stateList+stateModel

                    }
                    stateRVAdapter = StateRVAdapter(stateList)
                    stateRV.layoutManager = LinearLayoutManager(this)
                    stateRV.adapter = stateRVAdapter

                }catch (e:JSONException){
                    e.printStackTrace()
                }
            },{
                error->{

                    Toast.makeText(this,"Fail to get data",Toast.LENGTH_SHORT).show()
            }

            })
        queue.add(request)
    }

}