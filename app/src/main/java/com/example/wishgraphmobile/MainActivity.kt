package com.example.wishgraphmobile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    private var wallet_field: EditText? = null
    private var find_btn: Button? = null
    private var result_info: TextView? = null
    private var progressBar: ProgressBar? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        wallet_field = findViewById(R.id.wallet_field)
        find_btn = findViewById(R.id.btn_find)
        result_info = findViewById(R.id.result)
        progressBar = findViewById(R.id.progress_bar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        progressBar?.setVisibility(View.INVISIBLE)
        find_btn?.setOnClickListener {
            if (wallet_field?.text?.toString()?.trim()?.equals("")!!) {
                Toast.makeText(this, "Enter address <3", Toast.LENGTH_LONG).show()
                wallet_field?.requestFocus()
            } else {
                val thread = Thread(Runnable {
                    runOnUiThread(Runnable {
                        hideKeyboard()
                        progressBar?.setVisibility(View.VISIBLE)
                        wallet_field?.clearFocus()
                        result_info?.text = ""
                    })
                    var json = "test"
                    val wallet: String = wallet_field?.text.toString()
                    val simpleRunnable = SimpleRunnable(wallet)
                    val threadWithRunnable = Thread(simpleRunnable)
                    threadWithRunnable.start()
                    threadWithRunnable.join()
                    json = simpleRunnable.Text
                    json = "{\"authors\": $json\n}"
                    runOnUiThread(Runnable {
                        Support(json)
                    })
                })
                thread.start()
            }
        }
    }

    fun Support(json: String) {
        Log.println(Log.DEBUG, Log.DEBUG.toString(), json)
        val nft_array: JSONArray = JSONObject(json).getJSONArray("authors")
        val recyclerview = findViewById<RecyclerView>(R.id.nft_view)
        recyclerview?.setNestedScrollingEnabled(false)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val data = ArrayList<String>()
        try {
            for (i in 0..(nft_array.length() - 1)) {
                data.add(
                    nft_array.getJSONObject(i)
                        .getJSONArray("nft")
                        .getJSONObject(0)
                        .toString()
                )
                Log.println(Log.DEBUG, Log.DEBUG.toString(), data[0])
            }
        } catch (ex: FileNotFoundException) {
            Log.println(Log.DEBUG, Log.DEBUG.toString(), "CATCHED")
        }
        val adapter = CustomAdapter(data)
        recyclerview.adapter = adapter
        progressBar?.setVisibility(View.INVISIBLE)
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

class SimpleRunnable(_wallet: String) : Runnable {
    val Wallet: String
    var Text: String = ""

    init {
        Wallet = _wallet
    }

    public override fun run() {
        // val url = "https://api.rarible.org/v0.1/items/byOwner?owner=ETHEREUM%3A$Wallet"
        val url = "http://5.63.159.42:8081/user_recommend/$Wallet"
        Log.println(Log.DEBUG, Log.DEBUG.toString(), url)
        val connection = URL(url).openConnection() as HttpURLConnection
        var text = "test"
        try {
            connection.connect()
            text = connection.inputStream.use {
                it.reader().use() { reader -> reader.readText() }
            }
            Text = text
        } catch (ex: FileNotFoundException) {
            Log.println(Log.DEBUG, Log.DEBUG.toString(), "CATCHED")
        } finally {
            connection.disconnect()
        }
    }
}