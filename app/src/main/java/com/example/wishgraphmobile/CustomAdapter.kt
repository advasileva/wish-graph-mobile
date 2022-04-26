package com.example.wishgraphmobile

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.FileNotFoundException

class CustomAdapter(private val mList: List<String>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.nft_layout, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
//            val content = JSONObject(mList[position]).getJSONArray("content")
//            val picture_url = JSONObject(mList[position]).getJSONObject(0).getString("url")
            Picasso.get()
                .load(mList[position])
                .into(holder.imageView)
        } catch (ex: FileNotFoundException) {
            Log.println(Log.DEBUG, Log.DEBUG.toString(), "CATCHED")
        }

        // sets the text to the textview from our itemHolder class
        //holder.textView.text = "See"

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        //val textView: TextView = itemView.findViewById(R.id.textView)
    }
}
