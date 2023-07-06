package com.example.sharelocation.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.memberName.setTag(member.getId());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView memberName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.memberName);

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
                 //   Toast.makeText((Context) context, (CharSequence) memberName.getTag(), Toast.LENGTH_SHORT).show();
                    ((Room) context).startActivity(intent);

                }
            });
        }

    }
}
