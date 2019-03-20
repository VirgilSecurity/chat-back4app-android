package com.android.virgilsecurity.virgilback4app.chat.thread

import android.support.annotation.IntDef
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.android.virgilsecurity.virgilback4app.AppVirgil
import com.android.virgilsecurity.virgilback4app.R
import com.android.virgilsecurity.virgilback4app.model.Message
import com.parse.ParseUser
import com.virgilsecurity.android.ethree.kotlin.interaction.EThree
import com.virgilsecurity.sdk.crypto.PublicKey

/**
 * Created by Danylo Oliinyk on 11/23/17 at Virgil Security.
 * -__o
 */

class ChatThreadRVAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: MutableList<Message> = mutableListOf()
    private var eThree: EThree = AppVirgil.eThree
    lateinit var interlocutorPublicKey: PublicKey

    @IntDef(MessageType.ME.toLong(), MessageType.YOU.toLong())
    private annotation class MessageType {
        companion object {
            const val ME = 0
            const val YOU = 1
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(viewGroup.context)

        viewHolder = when (viewType) {
            MessageType.ME -> HolderMessageMe(inflater.inflate(R.layout.layout_holder_me,
                                                                viewGroup,
                                                                false))
            MessageType.YOU -> HolderMessageYou(inflater.inflate(R.layout.layout_holder_you,
                                                                 viewGroup,
                                                                 false))
            else -> HolderMessageYou(inflater.inflate(R.layout.layout_holder_me,
                                                      viewGroup,
                                                      false))
        }
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val decryptedText = eThree.decrypt(items[position].body, interlocutorPublicKey)

        when(viewHolder) {
            is HolderMessageMe -> viewHolder.bind(decryptedText)
            is HolderMessageYou -> viewHolder.bind(decryptedText)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].senderId == ParseUser.getCurrentUser().objectId) {
            MessageType.ME
        } else {
            MessageType.YOU
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(items: List<Message>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun addItems(items: List<Message>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(position: Int, item: Message) {
        items.add(position, item)
        notifyDataSetChanged()
    }

    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }
}
