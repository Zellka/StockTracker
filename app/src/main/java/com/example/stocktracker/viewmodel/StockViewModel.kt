package com.example.stocktracker.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.stocktracker.api.NetworkClient
import com.example.stocktracker.api.RetrofitServices
import com.example.stocktracker.entity.Stock
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private val service: RetrofitServices
        get() = NetworkClient.getClient(context, isNetworkConnected(context))
            .create(RetrofitServices::class.java)

    var stocksMutableLiveData: MutableLiveData<MutableList<Stock>> = MutableLiveData<MutableList<Stock>>()
    var stocks = MutableLiveData<String>()

    fun getAllStockList() {
        service.getStocks().enqueue(object : Callback<MutableList<Stock>> {

            override fun onResponse(
                call: Call<MutableList<Stock>>,
                response: Response<MutableList<Stock>>
            ) {
                stocksMutableLiveData.value = response.body()
            }

            override fun onFailure(call: Call<MutableList<Stock>>, t: Throwable) {
                stocks.value = "err"
            }
        })
    }

    private fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
    }
}