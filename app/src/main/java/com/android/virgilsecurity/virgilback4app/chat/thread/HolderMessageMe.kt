package com.android.virgilsecurity.virgilback4app.chat.thread

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_holder_me.*

class HolderMessageMe(
        override val containerView: View?
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(text: String) {
        tvMessage.text = text
    }
}