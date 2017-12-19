package com.android.virgilsecurity.virgilback4app.chat.thread;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.virgilsecurity.virgilback4app.AppVirgil;
import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.model.Message;
import com.parse.ParseUser;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsNotFoundException;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.highlevel.VirgilKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Danylo Oliinyk on 11/23/17 at Virgil Security.
 * -__o
 */

public class ChatThreadRVAdapter extends RecyclerView.Adapter<ChatThreadRVAdapter.HolderMessage> {

    @IntDef({MessageType.ME, MessageType.YOU})
    private @interface MessageType {
        int ME = 0;
        int YOU = 1;
    }

    private List<Message> items;

    ChatThreadRVAdapter() {
    }

    @Override
    public HolderMessage onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        HolderMessage viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case MessageType.ME:
                viewHolder = new HolderMessage(inflater.inflate(R.layout.layout_holder_me,
                                                                viewGroup,
                                                                false));
                break;
            case MessageType.YOU:
                viewHolder = new HolderMessage(inflater.inflate(R.layout.layout_holder_you,
                                                                viewGroup,
                                                                false));
                break;
            default:
                viewHolder = new HolderMessage(inflater.inflate(R.layout.layout_holder_me,
                                                                viewGroup,
                                                                false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HolderMessage viewHolder, int position) {
        viewHolder.bind(items.get(position));
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

    void addItems(List<Message> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    void addItem(int position, Message item) {
        if (items == null)
            items = new ArrayList<>();

        items.add(position, item);
        notifyDataSetChanged();
    }

    static class HolderMessage extends RecyclerView.ViewHolder {

        private VirgilKey virgilKey;

        @BindView(R.id.tvMessage) TextView tvMessage;

        HolderMessage(View v) {
            super(v);
            ButterKnife.bind(this, v);

            try {
                virgilKey = AppVirgil.getInfoHolder()
                                     .getVirgilApi()
                                     .getKeys()
                                     .load(ParseUser.getCurrentUser().getUsername());
            } catch (VirgilKeyIsNotFoundException e) {
                e.printStackTrace();
            } catch (CryptoException e) {
                e.printStackTrace();
            }
        }

        void bind(Message message) {
            tvMessage.setText(decrypt(message.getBody()));
        }

        /**
         * Decrypt data
         *
         * @param text to encrypt
         * @return decrypted data
         */
        String decrypt(String text) {
            try {
                return virgilKey.decrypt(text).toString();
            } catch (CryptoException e) {
                e.printStackTrace();
                return "";
            }
        }
    }
}
