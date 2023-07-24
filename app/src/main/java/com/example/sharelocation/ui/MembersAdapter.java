package com.example.sharelocation.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sharelocation.R;
import com.example.sharelocation.pojo.MemebrsModel;

import java.util.ArrayList;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MyViewHolder> {
    Context context;
    ArrayList<MemebrsModel> members;

    public MembersAdapter(Context context, ArrayList<MemebrsModel> rooms) {
        this.context = context;
        this.members = rooms;
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
        holder.memberName.setText(member.getName());
        Glide.with(context).load(member.getPhotoUri()).into(holder.memberPhoto);
        holder.memberName.setTag(member.getId());
        holder.memberPhoto.setTag(member.getPhotoUri());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView memberName;
        ImageView memberPhoto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.memberName);
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
                    Intent intent = new Intent(context.getApplicationContext(), Member.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //     ((Home) context).finish();
                    intent.putExtra("userId", (String) memberName.getTag());
                    intent.putExtra("memberName", memberName.getText());
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
