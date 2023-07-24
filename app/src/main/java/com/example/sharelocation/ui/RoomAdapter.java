package com.example.sharelocation.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharelocation.R;
import com.example.sharelocation.pojo.RoomModel;

import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.MyViewHolder> {
    Context context;
    ArrayList<RoomModel> rooms;

    public RoomAdapter(Context context, ArrayList<RoomModel> rooms) {
        this.context = context;
        this.rooms = rooms;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_recycler_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomAdapter.MyViewHolder holder, int position) {
        RoomModel room = rooms.get(position);
        holder.name.setText(room.getName());
        String temp = " Member";
        holder.numberOfRooms.setText(room.getRoomCapacity() + temp);
        holder.name.setTag(room.getId());
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, numberOfRooms;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.roomName);
            numberOfRooms = itemView.findViewById(R.id.membersNumber);
            setMembers(itemView);

        }

        public void setMembers(View itemView) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getApplicationContext(), Room.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //     ((Home) context).finish();
                    intent.putExtra("roomId", (String) name.getTag());
                    intent.putExtra("roomName", name.getText());
                    ((Home) context).startActivity(intent);

                }
            });
        }
    }
}
