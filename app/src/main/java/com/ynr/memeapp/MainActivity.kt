package com.ynr.memeapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.ynr.memeapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(isNetworkConnected){
            getMemeData()
        } else {
            Toast.makeText(this, "check internet connection",
                Toast.LENGTH_SHORT).show()
        }


        binding.shareBtn.setOnClickListener{
            Toast.makeText(this, "Share",
                Toast.LENGTH_SHORT).show()
        }

        binding.nextBtn.setOnClickListener {
            if(isNetworkConnected){
                getMemeData()
            } else {
                Toast.makeText(this, "check internet connection",
                    Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun getMemeData() {

        RetrofitInstance.apiInterface.getMemeData()
            .enqueue(object : Callback<Response?> {
                override fun onResponse(
                    call: Call<Response?>,
                    response: retrofit2.Response<Response?>
                ) {

                    binding.memeTitle.text = response.body()?.title
                    binding.memeAuthor.text = response.body()?.author
                    Glide.with(this@MainActivity).load(response.body()?.url)
                        .into(binding.memeImage)

                    binding.progressBar.visibility = View.GONE

                }

                override fun onFailure(call: Call<Response?>, t: Throwable) {

                    Toast.makeText(this@MainActivity, t.localizedMessage,
                        Toast.LENGTH_SHORT).show()

                    binding.progressBar.visibility = View.GONE

                }
            })

    }

    private val Context.isNetworkConnected : Boolean
        get() {
            val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                manager.getNetworkCapabilities(manager.activeNetwork)?.let {
                    it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            it.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) ||
                            it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                            it.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                } ?: false
            else
                @Suppress("DEPRECATION")
                manager.activeNetworkInfo?.isConnectedOrConnecting == true
        }

}