package com.android.virgilsecurity.virgilback4app.chat.thread;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.virgilsecurity.virgilback4app.R;
import com.android.virgilsecurity.virgilback4app.model.Message;
import com.android.virgilsecurity.virgilback4app.util.PrefsManager;
import com.parse.ParseUser;
import com.virgilsecurity.sdk.client.exceptions.VirgilKeyIsNotFoundException;
import com.virgilsecurity.sdk.client.model.CardModel;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.highlevel.VirgilApi;
import com.virgilsecurity.sdk.highlevel.VirgilApiContext;
import com.virgilsecurity.sdk.highlevel.VirgilCard;
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

public class ChatThreadRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @IntDef({MessageType.ME, MessageType.YOU})
    private @interface MessageType {
        int ME = 0;
        int YOU = 1;
    }

    private List<Message> items;
    private VirgilApi virgilApi;
    private VirgilApiContext virgilApiContext;
    private VirgilCard youCard;
    private VirgilCard meCard;

    ChatThreadRVAdapter(VirgilApi virgilApi, VirgilApiContext virgilApiContext) {
        this.virgilApi = virgilApi;
        this.virgilApiContext = virgilApiContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case MessageType.ME:
                viewHolder = new HolderMe(inflater.inflate(R.layout.layout_holder_me,
                                                           viewGroup,
                                                           false),
                                          virgilApi,
                                          virgilApiContext,
                                          meCard);
                break;
            case MessageType.YOU:
                viewHolder = new HolderYou(inflater.inflate(R.layout.layout_holder_you,
                                                            viewGroup,
                                                            false),
                                           virgilApi,
                                           virgilApiContext,
                                           youCard);
                break;
            default:
                viewHolder = new HolderMe(inflater.inflate(R.layout.layout_holder_me,
                                                           viewGroup,
                                                           false),
                                          virgilApi,
                                          virgilApiContext,
                                          meCard);
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

    void setCards(VirgilCard meCard, VirgilCard youCard) {
        this.meCard = meCard;
        this.youCard = youCard;
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

    static class HolderMe extends RecyclerView.ViewHolder {

        private VirgilApi virgilApi;
        private VirgilApiContext virgilApiContext;
        private VirgilCard card;

        @BindView(R.id.tvMessage) TextView tvMessage;

        HolderMe(View v,
                 VirgilApi virgilApi,
                 VirgilApiContext virgilApiContext,
                 VirgilCard card) {
            super(v);
            ButterKnife.bind(this, v);

            this.virgilApi = virgilApi;
            this.virgilApiContext = virgilApiContext;
            this.card = card;
        }

        void bind(Message message) {
            tvMessage.setText(decrypt(message.getBody(), card));
        }

        /**
         * Decrypt data
         *
         * @param text to encrypt
         * @param card senders card to verify data
         * @return decrypted data
         */
        public String decrypt(String text, VirgilCard card) {
            String decryptedText = null;

            try {
                VirgilKey virgilKey = loadKey(getMyCard().getIdentity());
                decryptedText = virgilKey.decryptThenVerify(text, card).toString();
            } catch (VirgilKeyIsNotFoundException e) {
                e.printStackTrace();
            } catch (CryptoException e) {
                e.printStackTrace();
            }

            return decryptedText;
        }

        private VirgilKey loadKey(String identity) throws VirgilKeyIsNotFoundException, CryptoException {
            return virgilApi.getKeys().load(identity);
        }

        private VirgilCard getMyCard() {
            CardModel cardModel = PrefsManager.VirgilPreferences.getCardModel();
            return new VirgilCard(virgilApiContext, cardModel);
        }
    }

    static class HolderYou extends RecyclerView.ViewHolder {

        private VirgilApi virgilApi;
        private VirgilApiContext virgilApiContext;
        private VirgilCard card;

        @BindView(R.id.tvMessage) TextView tvMessage;

        HolderYou(View v,
                  VirgilApi virgilApi,
                  VirgilApiContext virgilApiContext,
                  VirgilCard card) {
            super(v);
            ButterKnife.bind(this, v);

            this.virgilApi = virgilApi;
            this.virgilApiContext = virgilApiContext;
            this.card = card;
        }

        void bind(Message message) {
            tvMessage.setText(decrypt(message.getBody(), card));
        }

        /**
         * Decrypt data
         *
         * @param text to encrypt
         * @param card senders card to verify data
         * @return decrypted data
         */
        public String decrypt(String text, VirgilCard card) {
            String decryptedText = null;

            try {
                VirgilKey virgilKey = loadKey(getMyCard().getIdentity());
                decryptedText = virgilKey.decryptThenVerify(text, card).toString();
            } catch (VirgilKeyIsNotFoundException e) {
                e.printStackTrace();
            } catch (CryptoException e) {
                e.printStackTrace();
            }

            return decryptedText;
        }

        private VirgilKey loadKey(String identity) throws VirgilKeyIsNotFoundException, CryptoException {
            return virgilApi.getKeys().load(identity);
        }

        private VirgilCard getMyCard() {
            CardModel cardModel = PrefsManager.VirgilPreferences.getCardModel();
            return new VirgilCard(virgilApiContext, cardModel);
        }
    }
}
