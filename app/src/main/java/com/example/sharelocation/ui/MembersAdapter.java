package com.example.sharelocation.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sharelocation.R;
import com.example.sharelocation.pojo.MemebrsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MyViewHolder> {
    Context context;
    ArrayList<MemebrsModel> members;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String roomAdmin = "";

    public MembersAdapter(Context context, ArrayList<MemebrsModel> members, String roomAdmin) {
        this.context = context;
        this.members = members;
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        this.roomAdmin = roomAdmin;
    }

    @NonNull
    @Override
    public MembersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.members_recycler_view, parent, false);
        return new MembersAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersAdapter.MyViewHolder holder, int position) {
        MemebrsModel member = members.get(position);
        String currentUser = " (You)";
        if (member.getId().equals(user.getUid())) {
            holder.memberName.setText(member.getName() + currentUser);
        } else {
            holder.memberName.setText(member.getName());
            //  holder.admin.setVisibility(View.VISIBLE);
        }
        if (member.getId().equals(roomAdmin)) {
            holder.admin.setVisibility(View.VISIBLE);
            holder.memberName.setGravity(Gravity.BOTTOM);
        } else {
            holder.admin.setVisibility(View.GONE);
        }

        Glide.with(context).load(member.getPhotoUri()).into(holder.memberPhoto);
        holder.memberName.setTag(member.getId());
        holder.memberPhoto.setTag(member.getPhotoUri());
        holder.admin.setTag(member.getName());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView memberName;
        TextView admin;
        ImageView memberPhoto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.memberName);
            admin = itemView.findViewById(R.id.admin);
            memberPhoto = itemView.findViewById(R.id.memberImageView);
            memberPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  Toast.makeText((Context) context, "Mohamed Elockly", Toast.LENGTH_SHORT).show();
                    showAlertDialoge((String) memberPhoto.getTag());
                }
            });
            setMemberProfile(itemView);
        }


        public void setMemberProfile(View itemView) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (memberName.getTag().equals(user.getUid())) {
                        //    Toast.makeText((Context) context, (CharSequence) memberName.getTag(), Toast.LENGTH_SHORT).show();
                        intent = new Intent(context.getApplicationContext(), ProfileActivity.class);
                    } else {
                        intent = new Intent(context.getApplicationContext(), Member.class);
                    }


                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //     ((Home) context).finish();
                    intent.putExtra("userId", (String) memberName.getTag());
                    intent.putExtra("memberName", (String) admin.getTag());
                    //   Toast.makeText((Context) context, (CharSequence) memberName.getTag(), Toast.LENGTH_SHORT).show();
                    ((Room) context).startActivity(intent);

                }
            });
        }

        public void showAlertDialoge(String uri) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.small_image_dialoge, null);
            ImageView imageView = view.findViewById(R.id.smallImageView);
            builder.setView(view);
            final AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            Glide.with(context).load(uri).into(imageView);
        }

    }
}
