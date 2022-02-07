package com.lahsuak.smartgallary

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File
import java.lang.IllegalArgumentException
import kotlin.collections.ArrayList

class GalleryAdapter(
    var context: Context,
    var list: ArrayList<Image>,
    var listener: GalleryListener
) :
    RecyclerView.Adapter<GalleryAdapter.GalleryHolder>() {
    class GalleryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false)
        return GalleryHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
        Thread {
            val path:String
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                path= getRealPath(Uri.parse(list[position].path),context)!!
            }else
                path = list[position].path

            val file = File(path)
            if (file.exists()) {
                (context as Activity).runOnUiThread {
                    //LOAD IMAGE ON UI THREAD
                    Glide.with(context).load(file).into(holder.image)
                }
            }
        }.start()
        holder.itemView.setOnClickListener {
            val path:String
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                path= getRealPath(Uri.parse(list[position].path),context)!!
            }else
                path = list[position].path
            listener.onPhotoClick(path,list[position].dateTime)
        }
    }
    fun updateList(newlist: ArrayList<Image>) {
        list.clear()
        list.addAll(newlist)
        notifyDataSetChanged()
    }
    private fun getRealPath(uri: Uri, context: Context): String? {
        var realPath: String? = null
        try {
            if (uri.scheme!! == "content") {
                val projection = arrayOf("_data")
                val cursor = context.contentResolver.query(
                    uri,
                    projection, null, null, null
                )
                if (cursor != null) {
                    val id = cursor.getColumnIndexOrThrow("_data")
                    cursor.moveToNext()
                    try {
                        realPath = cursor.getString(id)
                    } catch (e: Exception) {
                        realPath = null
                    } finally {
                        cursor.close()
                    }
                } else if (uri.scheme!!.equals("file")) {
                    realPath = uri.path!!
                }
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            realPath = null
        }
        return realPath
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

interface GalleryListener {
    fun onPhotoClick(path: String, date: String)
}