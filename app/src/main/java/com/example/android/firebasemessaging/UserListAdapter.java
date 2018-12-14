package com.example.android.firebasemessaging;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import static com.example.android.firebasemessaging.R.color.timeColor;


public class UserListAdapter extends FirebaseRecyclerAdapter<ChatListUser, UserListAdapter.MyUserHolder> {

    Context context;


    /**
     * @param1      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param2 modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param3 The class that hold references to all sub-views in an instance modelLayout.
     * @param4 ref             The Firebase location to watch for data changes. Can also be a slice of a location,
     *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public UserListAdapter(int modelLayout, Query ref, Context context) {
        super(ChatListUser.class, modelLayout, MyUserHolder.class, ref);
        this.context = context;

    }

    @NonNull
    @Override
    public MyUserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false);
        return new MyUserHolder(view);
    }

    @Override
    protected void populateViewHolder(MyUserHolder viewHolder, ChatListUser model, int position) {
        model.reverseTime();
        viewHolder.userName.setText(model.getUserName());
        viewHolder.userMessage.setText(model.getLastMessage());
        viewHolder.userTime.setText(DateFormat.format("h:mm a", model.getLastMessageTime()));
        if(model.getUnreadMessages()){
            viewHolder.unreadMessages.setVisibility(View.VISIBLE);
            viewHolder.userTime.setTextColor(context.getResources().getColor(R.color.timeColor));
            viewHolder.userMessage.setTypeface(viewHolder.userMessage.getTypeface(), Typeface.BOLD);
        }
        else{
            viewHolder.unreadMessages.setVisibility(View.INVISIBLE);
            viewHolder.userTime.setTextColor(context.getResources().getColor(android.R.color.tab_indicator_text));
            viewHolder.userMessage.setTypeface(null, Typeface.NORMAL);
        }
        if(model.isOnline()){
            viewHolder.online.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.online.setVisibility(View.GONE);
        }
    }


    class MyUserHolder extends RecyclerView.ViewHolder{

        TextView userName, userMessage, userTime;
        ImageView unreadMessages, online;

        MyUserHolder(@NonNull final View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userMessage = itemView.findViewById(R.id.user_message);
            userTime = itemView.findViewById(R.id.user_time);
            unreadMessages = itemView.findViewById(R.id.unread);
            online = itemView.findViewById(R.id.online_label);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ChatRoomActivity.class);
                    intent.putExtra("user_uid", getItem(getAdapterPosition()).getUserID());
                    intent.putExtra("user_name", getItem(getAdapterPosition()).getUserName());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
