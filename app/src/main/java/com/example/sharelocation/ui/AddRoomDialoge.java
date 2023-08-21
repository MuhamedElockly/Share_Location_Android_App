package com.example.sharelocation.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.sharelocation.R;

public class AddRoomDialoge extends AppCompatDialogFragment {


    private EditText roomName;
    private EditText roomCapacity;
    private AddRoomDialogListener listener;
    private Button apply;
    private Button cancel;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_room, null);
        roomName = view.findViewById(R.id.dialogeRoomName);



        builder.setView(view);

        final AlertDialog dialog = builder.create();


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        apply = (Button) view.findViewById(R.id.apply);
        cancel = (Button) view.findViewById(R.id.cancel);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomNameText = roomName.getText().toString();

                listener.applyNewRoom(roomNameText);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (AddRoomDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ExampleDialogListener");
        }
    }

    public interface AddRoomDialogListener {
        void applyNewRoom(String roomName);
    }
}

