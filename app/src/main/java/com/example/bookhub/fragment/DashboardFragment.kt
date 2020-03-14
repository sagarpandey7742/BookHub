package com.example.bookhub.fragment


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.CycleInterpolator
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookhub.R
import com.example.bookhub.adapter.DashboardRecyclerAdapter
import com.example.bookhub.model.book
import com.example.bookhub.utils.ConnectionManager
import kotlinx.android.synthetic.main.recycler_dashboard_item.*
import org.json.JSONException

class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var progressLayout : RelativeLayout
    lateinit var progressBar: ProgressBar

    val bookInfoList = arrayListOf<book>()


    lateinit var recyclerAdapter: DashboardRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard_fragment, container, false)
        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)

        layoutManager = LinearLayoutManager(activity)

        progressBar = view.findViewById(R.id.progressBar)

        progressLayout = view.findViewById(R.id.progressLayout)

        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v1/book/fetch_books/"

        if(ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url,null,
                Response.Listener {

                    try{
                        progressLayout.visibility = View.GONE
                        val success = it.getBoolean("success")

                        if(success){
                            val data = it.getJSONArray("data")
                            for(i in 0 until data.length()){
                                val bookJsonObject =  data.getJSONObject(i)
                                val bookObject = book(
                                    bookJsonObject.getString("book_id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("author"),
                                    bookJsonObject.getString("rating"),
                                    bookJsonObject.getString("price"),
                                    bookJsonObject.getString("image")
                                )
                                bookInfoList.add(bookObject)
                                recyclerAdapter = DashboardRecyclerAdapter(activity as Context, bookInfoList)

                                recyclerDashboard.adapter = recyclerAdapter

                                recyclerDashboard.layoutManager = layoutManager
                            }
                        }
                        else{
                            Toast.makeText(activity as Context, "Some error occurred", Toast.LENGTH_SHORT).show()
                        }
                        println("Response is $it")
                    }

                    catch (e: JSONException){
                        Toast.makeText(activity as Context,"Some unknown error occurred!!!",Toast.LENGTH_SHORT).show()
                    }



                },
                Response.ErrorListener {
                    Toast.makeText(activity as Context,"Volley error occurred",Toast.LENGTH_SHORT).show()

                })
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "219bf73d98d343"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)
        }

        else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found!!!")
            dialog.setPositiveButton("Open Settings"){text, listner->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit"){text, listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }


        return view
    }

}
