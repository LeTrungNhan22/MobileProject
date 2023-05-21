package com.example.mobileproject.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileproject.MessageActivity;
import com.example.mobileproject.R;
import com.example.mobileproject.models.Chat;
import com.example.mobileproject.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {
    public Context mContext;
    public List<User> mUsers;
    public FirebaseUser firebaseUser;

    public boolean isChat;

    private String theLastMessage;

    public UserChatAdapter(Context mContext, List<User> mUsers, boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_chat_item, parent, false);
        return new UserChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);

        if (user != null) {
            holder.username.setText(user.getUsername());
            holder.fullName.setText(user.getFullName());
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
            if (isChat) {
                lastMessage(user.getId(), holder.last_msg);
            } else {
                holder.last_msg.setVisibility(View.GONE);
            }

            if (isChat && user.getStatusNetwork() != null) {
                if (user.getStatusNetwork().equals("online") || user.getStatusNetwork().equals("trực tuyến")) {
                    holder.onlineStatus.setVisibility(View.VISIBLE);
                    holder.offlineStatus.setVisibility(View.GONE);
                } else {
                    holder.onlineStatus.setVisibility(View.GONE);
                    holder.offlineStatus.setVisibility(View.VISIBLE);
                }
            } else {
                holder.onlineStatus.setVisibility(View.GONE);
                holder.offlineStatus.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    if (user.getId().equals(firebaseUser.getUid())) {
                        intent.putExtra("userId", user.getId());
                    } else {
                        intent.putExtra("userId", user.getId());
                    }
                    mContext.startActivity(intent);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Bạn có chắc chắn muốn xóa cuộc trò chuyện này không?");
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteChat(user.getId());
                        }
                    });
                    builder.setNegativeButton("Không", null);
                    builder.show();
                    return true;
                }
            });


        }


    }

    private void deleteChat(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Chat chat = snapshot1.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(id) ||
                            chat.getReceiver().equals(id) && chat.getSender().equals(firebaseUser.getUid())) {
                        snapshot1.getRef().removeValue();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (mUsers != null) {
            return mUsers.size();
        } else {
            return 0;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView profile_image;
        public TextView fullName;
        private ImageView onlineStatus, offlineStatus;
        private TextView last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            onlineStatus = itemView.findViewById(R.id.onlineStatus);
            offlineStatus = itemView.findViewById(R.id.offlineStatus);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            fullName = itemView.findViewById(R.id.fullName);
            last_msg = itemView.findViewById(R.id.message_content);

        }
    }

    private void lastMessage(final String userId, final TextView last_msg) {
        theLastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Chat chat = snapshot1.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {
                        theLastMessage = chat.getMessage();
                    }
                }

                switch (theLastMessage) {
                    case "default":
                        last_msg.setText("No message");
                        last_msg.setVisibility(View.GONE);
                        break;
                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
