package com.example.musicnkt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(private var listSong: MutableList<Song>) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    var onClick: OnClickItemMusic? = null

    fun setOnClickItemMusic(onClick: OnClickItemMusic) {
        this.onClick = onClick
    }

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNameSong: TextView = itemView.findViewById(R.id.text_name_song)
        val imageSong: ImageView = itemView.findViewById(R.id.image_item_song)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_music, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = listSong[position]
        holder.textNameSong.text = song.nameSong

        when (position) {
            0 -> holder.imageSong.setBackgroundResource(R.drawable.a)
            1 -> holder.imageSong.setBackgroundResource(R.drawable.b)
            2 -> holder.imageSong.setBackgroundResource(R.drawable.c)
            3 -> holder.imageSong.setBackgroundResource(R.drawable.d)
            4 -> holder.imageSong.setBackgroundResource(R.drawable.e)
        }

        holder.itemView.setOnClickListener { onClick?.onClickItem(song) }
    }

    override fun getItemCount(): Int {
        return listSong.size
    }
}
