package com.android.virgilsecurity.virgilback4app.chat.thread

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.virgilsecurity.virgilback4app.R

class HolderMessageMe(
        itemView: View
) : RecyclerView.ViewHolder(itemView) {

    fun bind(text: String) {
        itemView.findViewById<TextView>(R.id.tvMessage).text = text
    }
}
