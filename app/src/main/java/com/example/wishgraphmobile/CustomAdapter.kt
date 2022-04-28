package com.example.wishgraphmobile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.FileNotFoundException

class CustomAdapter(private val mList: List<String>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    var vcontext : Context? = null
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        vcontext = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.nft_layout, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
//            val content = JSONObject(mList[position]).getJSONArray("content")
            val picture_url = JSONObject(mList[position]).getString("url")
            val picture_title = JSONObject(mList[position]).getString("title")
            val picture_id = JSONObject(mList[position]).getString("id").substringAfter(":")
            if (picture_url.isNullOrEmpty()) {
                mList
            }
            Picasso.get()
                .load(picture_url)
                .into(holder.imageView)
            holder.imageView.setOnClickListener {
                val i = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://rarible.com/token/$picture_id?tab=details")
                )
                vcontext?.startActivity(i)
            }

            holder.textView.text = picture_title
        } catch (ex: FileNotFoundException) {
            Log.println(Log.DEBUG, Log.DEBUG.toString(), "CATCHED")
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val textView: TextView = itemView.findViewById(R.id.nft_title)
    }
}
