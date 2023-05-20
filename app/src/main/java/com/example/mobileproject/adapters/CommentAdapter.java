package com.example.mobileproject.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileproject.CommentsActivity;
import com.example.mobileproject.MainActivity;
import com.example.mobileproject.R;
import com.example.mobileproject.fragments.ProfileFragment;
import com.example.mobileproject.models.Comment;
import com.example.mobileproject.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;

    private List<Comment> mComment;

    private String postId;

    private FirebaseUser firebaseUser;

    public CommentAdapter(Context mContext, List<Comment> mComment, String postId) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.postId = postId;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = mComment.get(position);

        holder.comment.setText(comment.getComment());

        getUserInfo(holder.image_profile, holder.username, comment.getPublisher());


        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileFragment.class);
                intent.putExtra("publisherId", comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(mContext, R.color.color_gray_light));
                cd.setAlpha(191);
                holder.itemView.setBackground(cd);
                if (comment.getPublisher().equals(firebaseUser.getUid())) {
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Bạn có muốn xóa bình luận này?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, " ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Có",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseDatabase.getInstance().getReference("Comments")
                                            .child(postId).child(comment.getCommentId())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(mContext, "Đã xóa bình luận", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    // Thiết lập lại màu nền của item view sau khi xóa xong
                                    cd.setAlpha(0); // 0% alpha
                                    holder.itemView.setBackground(cd);
                                    dialogInterface.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile;
        public TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(ImageView imageView, TextView username, String publisherId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(publisherId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user.getImageURL().equals("default")) {
                    imageView.setImageResource(R.mipmap.ic_launcher);
                } else {
                    if (mContext != null && imageView != null) {
                        Glide.with(mContext).load(user.getImageURL()).into(imageView);
                    }

                }
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
