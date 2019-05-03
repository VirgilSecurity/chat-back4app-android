package com.android.virgilsecurity.virgilback4app.chat.thread

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.android.virgilsecurity.virgilback4app.R

class HolderMessageYou(
        itemView: View
) : RecyclerView.ViewHolder(itemView) {

    fun bind(text: String) {
        itemView.findViewById<TextView>(R.id.tvMessage).text = text
    }
}
