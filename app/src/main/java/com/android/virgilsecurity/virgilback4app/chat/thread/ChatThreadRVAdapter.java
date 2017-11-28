package com.android.virgilsecurity.virgilback4app.chat.thread;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.model.Message;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Danylo Oliinyk on 11/23/17 at Virgil Security.
 * -__o
 */

public class ChatThreadRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @IntDef({MessageType.ME, MessageType.YOU})
    private @interface MessageType {
        int ME = 0;
        int YOU = 1;
    }

    private List<Message> items;
    private Context context;

    ChatThreadRVAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case MessageType.ME:
                viewHolder = new HolderMe(inflater.inflate(R.layout.layout_holder_me,
                                                            viewGroup,
                                                            false));
                break;
            case MessageType.YOU:
                viewHolder = new HolderYou(inflater.inflate(R.layout.layout_holder_you,
                                                           viewGroup,
                                                           false));
                break;
            default:
                viewHolder = new HolderYou(inflater.inflate(R.layout.layout_holder_me,
                                                           viewGroup,
                                                           false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case MessageType.ME:
                ((HolderMe) viewHolder).bind(items.get(position));
                break;
            case MessageType.YOU:
                ((HolderYou) viewHolder).bind(items.get(position));
                break;
            default:
                ((HolderMe) viewHolder).bind(items.get(position));
                break;
        }
    }

    @Override public int getItemViewType(int position) {
        if (items.get(position).getSenderId().equals(ParseUser.getCurrentUser().getObjectId())) {
            return MessageType.ME;
        } else {
            return MessageType.YOU;
        }
    }

    @Override public int getItemCount() {
        return items != null ? items.size() : -1;
    }

    void setItems(List<Message> items) {
        if (items != null) {
            this.items = new ArrayList<>(items);
        } else {
            this.items = Collections.emptyList();
        }
        notifyDataSetChanged();
    }

    public void addItem(Message item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void addItem(int position, Message item) {
        items.add(position, item);
        notifyDataSetChanged();
    }

    public static class HolderMe extends RecyclerView.ViewHolder {

        @BindView(R.id.tvMessage) TextView tvMessage;

        HolderMe(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(Message message) {
            tvMessage.setText(message.getBody());
        }
    }

    public static class HolderYou extends RecyclerView.ViewHolder {

        @BindView(R.id.tvMessage) TextView tvMessage;

        HolderYou(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(Message message) {
            tvMessage.setText(message.getBody());
        }
    }

}
