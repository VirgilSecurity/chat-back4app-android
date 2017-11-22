package com.android.virgilsecurity.virgilback4app.chat.contactsList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Danylo Oliinyk on 11/22/17 at Virgil Security.
 * -__o
 */

public class ContactsRVAdapter extends RecyclerView.Adapter<ContactsRVAdapter.ContactHolder> {

    private List<User> items;
    private Context context;
    private ClickListener clickListener;

    public ContactsRVAdapter(Context context) {
        this.items = Collections.emptyList();
        this.context = context;
    }

    @Override
    public ContactsRVAdapter.ContactHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.item_list_contacts, parent);

        return new ContactHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(ContactHolder holder, int position) {
        holder.bind(items.get(position));
    }

    public void setItems(List<User> items) {
        if (items != null) {
            this.items = new ArrayList<>(items);
        } else {
            this.items = Collections.emptyList();
        }
        notifyDataSetChanged();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : -1;
    }

    static class ContactHolder extends RecyclerView.ViewHolder {

        private ClickListener listener;

        @BindView(R.id.rlItemRoot) View rlItemRoot;
        @BindView(R.id.ivUserPhoto) ImageView ivUserPhoto;
        @BindView(R.id.tvUsername) TextView tvUsername;

        ContactHolder(View view, ClickListener listener) {
            super(view);

            ButterKnife.bind(this, view);
            this.listener = listener;
        }

        public void bind(User user) {
            tvUsername.setText(user.getName());
            rlItemRoot.setOnClickListener((v) -> listener.onItemClicked(getAdapterPosition(), user));
//            ivUserPhoto.setImageResource(user.getPhotoUrl().....);
        }
    }

    public interface ClickListener {

        void onItemClicked(int position, User user);
    }
}
