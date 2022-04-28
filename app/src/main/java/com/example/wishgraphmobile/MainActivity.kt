package com.example.wishgraphmobile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
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

    private var activity: Activity? = null
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
                    runOnUiThread(Runnable {
                        try {
                            Support(json)
                        } catch (ex: Exception) {
                            Toast.makeText(this, "Server-side error", Toast.LENGTH_LONG).show()
                        } finally {
                            progressBar?.setVisibility(View.INVISIBLE)
                        }
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
        var set = mutableSetOf("")
        val data = ArrayList<String>()
        try {
            for (i in 0..(nft_array.length() - 1)) {
                val array = nft_array.getJSONObject(i)
                    .getJSONArray("nft")
                for (j in 0..(array.length() - 1)) {
                    val element = array
                        .getJSONObject(j)
                        .toString()
                    val url = JSONObject(element).getString("url")
                    val title = JSONObject(element).getString("title")
                    if (!set.contains(title)) {
                        set.add(title)
                        if (url != "null" && !url.contains("metadata")) {
                            data.add(element)
                            Log.println(
                                Log.DEBUG,
                                Log.DEBUG.toString(), url
                            )
                        } else {
                            Log.println(Log.DEBUG, Log.DEBUG.toString(), "DELETED")
                        }
                    } else {
                        Log.println(Log.DEBUG, Log.DEBUG.toString(), "ALREADY EXIST")
                    }
                }
            }
        } catch (ex: FileNotFoundException) {
            Log.println(Log.DEBUG, Log.DEBUG.toString(), "CATCHED")
        }
        val adapter = CustomAdapter(data)
        recyclerview.adapter = adapter
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

    class SimpleRunnable(_wallet: String) : Runnable {
        val Wallet: String
        var Text: String = ""
        var IsSuccess: Boolean = true

        init {
            Wallet = _wallet
        }

        public override fun run() {
            val url = "http://185.46.8.253:8080/v0.1/recommend?walletToken=ETHEREUM:$Wallet"
            Log.println(Log.DEBUG, Log.DEBUG.toString(), url)
            val connection = URL(url).openConnection() as HttpURLConnection
            var text = "test"
            try {
                connection.connect()
                text = connection.inputStream.use {
                    it.reader().use() { reader -> reader.readText() }
                }
                Text = text
            } catch (ex: Exception) {
                Log.println(Log.DEBUG, Log.DEBUG.toString(), "CATCHED")
                IsSuccess = false
            } finally {
                connection.disconnect()
            }
        }
    }
}