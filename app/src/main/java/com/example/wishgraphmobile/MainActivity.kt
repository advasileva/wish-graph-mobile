package com.example.wishgraphmobile

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private var wallet_field: EditText? = null
    private var find_btn: Button? = null
    private var result_info: TextView? = null
    private var image_view: ImageView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        wallet_field = findViewById(R.id.wallet_field)
        find_btn = findViewById(R.id.btn_find)
        result_info = findViewById(R.id.result)
        image_view = findViewById(R.id.nft_view)

        find_btn?.setOnClickListener {
            if(wallet_field?.text?.toString()?.trim()?.equals("")!!)
                Toast.makeText(this, "Enter address <3", Toast.LENGTH_LONG).show()
            else {
                val wallet: String = wallet_field?.text.toString()
                val simpleRunnable = SimpleRunnable(wallet)
                val threadWithRunnable = Thread(simpleRunnable)
                threadWithRunnable.start()
                result_info?.text="Finding..."
                threadWithRunnable.join()
                val json = simpleRunnable.Text
                val nft_array = JSONObject(json).getJSONArray("items")
                val meta = nft_array.getJSONObject(0).getString("meta")
                val content = JSONObject(meta).getJSONArray("content")
                val picture_url = content.getJSONObject(0).getString("url")
                Picasso.get()
                    .load(picture_url)
                    .into(image_view);
                result_info?.text = picture_url
            }
        }
    }
}

class SimpleRunnable(_wallet: String) : Runnable {
    val Wallet: String
    var Text: String = ""

    init {
        Wallet = _wallet
    }

    public override fun run() {
        val url =
            "https://api.rarible.org/v0.1/items/byOwner?owner=ETHEREUM%3A$Wallet"
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
        Log.println(Log.DEBUG, Log.DEBUG.toString(), "GET $text")
    }
}