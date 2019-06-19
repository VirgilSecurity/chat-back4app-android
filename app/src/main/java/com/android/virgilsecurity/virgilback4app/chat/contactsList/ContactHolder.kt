package com.android.virgilsecurity.virgilback4app.chat.contactsList

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.virgilsecurity.virgilback4app.R
import com.android.virgilsecurity.virgilback4app.model.ChatThread
import com.parse.ParseUser

class ContactHolder(
        itemView: View,
        private val listener: ThreadsListRVAdapter.ClickListener
) : RecyclerView.ViewHolder(itemView) {

    fun bind(thread: ChatThread) {
        if (thread.senderUsername == ParseUser.getCurrentUser().username)
            itemView.findViewById<TextView>(R.id.tvUsername).text = thread.recipientUsername
        else
            itemView.findViewById<TextView>(R.id.tvUsername).text = thread.senderUsername

        itemView.findViewById<RelativeLayout>(R.id.rlItemRoot)
                .setOnClickListener { v -> listener.onItemClicked(adapterPosition, thread) }
    }
}
