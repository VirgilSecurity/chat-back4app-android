package com.android.virgilsecurity.virgilback4app.chat.contactsList

import android.support.v7.widget.RecyclerView
import android.view.View
import com.android.virgilsecurity.virgilback4app.model.ChatThread
import com.parse.ParseUser
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_list_threads.*

class ContactHolder(
        override val containerView: View?,
        private val listener: ThreadsListRVAdapter.ClickListener
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(thread: ChatThread) {
        if (thread.senderUsername == ParseUser.getCurrentUser().username)
            tvUsername.text = thread.recipientUsername
        else
            tvUsername.text = thread.senderUsername

        rlItemRoot.setOnClickListener { v -> listener.onItemClicked(adapterPosition, thread) }
    }
}