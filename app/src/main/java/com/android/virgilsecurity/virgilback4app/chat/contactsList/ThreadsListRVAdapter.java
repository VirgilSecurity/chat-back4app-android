package com.android.virgilsecurity.virgilback4app.chat.contactsList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.model.ChatThread;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

public class ThreadsListRVAdapter extends RecyclerView.Adapter<ThreadsListRVAdapter.ContactHolder> {

    private List<ChatThread> items;
    private Context context;
    private ClickListener clickListener;

    public ThreadsListRVAdapter(Context context) {
        this.items = Collections.emptyList();
        this.context = context;
    }

    @Override
    public ThreadsListRVAdapter.ContactHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {

        View view = LayoutInflater.from(context)
                                  .inflate(R.layout.item_list_threads, parent, false);

        return new ContactHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(ContactHolder holder, int position) {
        holder.bind(items.get(position));
    }

    void setItems(List<ChatThread> items) {
        if (items != null) {
            this.items = new ArrayList<>(items);
        } else {
            this.items = Collections.emptyList();
        }
        notifyDataSetChanged();
    }

    void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : -1;
    }

    public void addItems(List<ChatThread> items) {
        if (items == null)
            items = new ArrayList<>();

        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(int i, ChatThread thread) {
        if (items == null || items.isEmpty())
            items = new ArrayList<>();

        items.add(i, thread);
        notifyDataSetChanged();
    }

    static class ContactHolder extends RecyclerView.ViewHolder {

        private ClickListener listener;

        @BindView(R.id.rlItemRoot)
        View rlItemRoot;
        @BindView(R.id.ivUserPhoto)
        ImageView ivUserPhoto;
        @BindView(R.id.tvUsername)
        TextView tvUsername;

        ContactHolder(View view, ClickListener listener) {
            super(view);

            ButterKnife.bind(this, view);
            this.listener = listener;
        }

        void bind(ChatThread thread) {
            if (thread.getSenderUsername().equals(ParseUser.getCurrentUser().getUsername()))
                tvUsername.setText(thread.getRecipientUsername());
            else
                tvUsername.setText(thread.getSenderUsername());

            rlItemRoot.setOnClickListener((v) -> listener.onItemClicked(getAdapterPosition(), thread));
//            ivUserPhoto.setImageResource(user.getPhotoUrl().....);
        }
    }

    public interface ClickListener {

        void onItemClicked(int position, ChatThread user);
    }
}
