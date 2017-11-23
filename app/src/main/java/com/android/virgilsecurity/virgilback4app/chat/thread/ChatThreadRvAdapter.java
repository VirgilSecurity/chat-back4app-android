package com.android.virgilsecurity.virgilback4app.chat.thread;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by Danylo Oliinyk on 11/23/17 at Virgil Security.
 * -__o
 */

public class ChatThreadRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

//    // The items to display in your RecyclerView
//    private List<Message> items;
//    private Context mContext;
//
//    private final int DATE = 0, YOU = 1, ME = 2;
//
//    // Provide a suitable constructor (depends on the kind of dataset)
//    public ChatThreadRvAdapter(Context context, List<ChatData> items) {
//        this.mContext = context;
//        this.items = items;
//    }
//
//    // Return the size of your dataset (invoked by the layout manager)
//    @Override
//    public int getItemCount() {
//        return this.items.size();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        //More to come
//        if (items.get(position).getType().equals("0")) {
//            return DATE;
//        } else if (items.get(position).getType().equals("1")) {
//            return YOU;
//        }else if (items.get(position).getType().equals("2")) {
//            return ME;
//        }
//        return -1;
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        RecyclerView.ViewHolder viewHolder;
//        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
//
//        switch (viewType) {
//            case DATE:
//                View v1 = inflater.inflate(R.layout.layout_holder_date, viewGroup, false);
//                viewHolder = new HolderDate(v1);
//                break;
//            case YOU:
//                View v2 = inflater.inflate(R.layout.layout_holder_you, viewGroup, false);
//                viewHolder = new HolderYou(v2);
//                break;
//            default:
//                View v = inflater.inflate(R.layout.layout_holder_me, viewGroup, false);
//                viewHolder = new HolderMe(v);
//                break;
//        }
//        return viewHolder;
//    }
//    public void addItem(List<ChatData> item) {
//        items.addAll(item);
//        notifyDataSetChanged();
//    }
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
//        switch (viewHolder.getItemViewType()) {
//            case DATE:
//                HolderDate vh1 = (HolderDate) viewHolder;
//                configureViewHolder1(vh1, position);
//                break;
//            case YOU:
//                HolderYou vh2 = (HolderYou) viewHolder;
//                configureViewHolder2(vh2, position);
//                break;
//            default:
//                HolderMe vh = (HolderMe) viewHolder;
//                configureViewHolder3(vh, position);
//                break;
//        }
//    }
//
//
//    public class HolderMe extends RecyclerView.ViewHolder {
//
//        private TextView tvText;
//
//        public HolderMe(View v) {
//            super(v);
//            time = (TextView) v.findViewById(R.id.tv_time);
//            chatText = (TextView) v.findViewById(R.id.tv_chat_text);
//        }
//
//        public TextView getTime() {
//            return time;
//        }
//
//        public void setTime(TextView time) {
//            this.time = time;
//        }
//
//        public TextView getChatText() {
//            return chatText;
//        }
//
//        public void setChatText(TextView chatText) {
//            this.chatText = chatText;
//        }
//    }

}
