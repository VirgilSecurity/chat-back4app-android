package com.android.virgilsecurity.virgilback4app.chat.contactsList

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.android.virgilsecurity.virgilback4app.R
import com.android.virgilsecurity.virgilback4app.model.ChatThread

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

class ThreadsListRVAdapter internal constructor(private val context: Context) : RecyclerView.Adapter<ContactHolder>() {

    private var items: MutableList<ChatThread> = mutableListOf()
    private lateinit var clickListener: ClickListener

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ContactHolder {

        val view = LayoutInflater.from(context)
                .inflate(R.layout.item_list_threads, parent, false)

        return ContactHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        holder.bind(items[position])
    }

    internal fun setItems(items: List<ChatThread>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    internal fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    override fun getItemCount(): Int {
        return items.size
    }

    internal fun addItems(items: List<ChatThread>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    internal fun addItem(i: Int, thread: ChatThread) {
        items.add(i, thread)
        notifyDataSetChanged()
    }

    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    fun contains(chatThread: ChatThread) = items.contains(chatThread)

    interface ClickListener {

        fun onItemClicked(position: Int, user: ChatThread)
    }
}
