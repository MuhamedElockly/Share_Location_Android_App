package com.example.sharelocation.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.sharelocation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

public class JoinRoom {
    private Context context;
    private Button submit;
    private DatabaseReference database;
    private AlertDialog dialog;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    private DatabaseReference roomRef;

    public JoinRoom(Context context) {
        this.context = context;
        fAuth = FirebaseAuth.getInstance();
        this.user = fAuth.getCurrentUser();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;

    }

    public void joinRoom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.join_room, null);
        submit = (Button) view.findViewById(R.id.submit);


        builder.setView(view);
        final AlertDialog invitationCodeDialoge = builder.create();
        invitationCodeDialoge.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        invitationCodeDialoge.show();
        EditText codeFeild1 = view.findViewById(R.id.codeFeild1);
        EditText codeFeild2 = view.findViewById(R.id.codeFeild2);
        EditText codeFeild3 = view.findViewById(R.id.codeFeild3);
        EditText codeFeild4 = view.findViewById(R.id.codeFeild4);
        EditText codeFeild5 = view.findViewById(R.id.codeFeild5);
        EditText codeFeild6 = view.findViewById(R.id.codeFeild6);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgreesBar();
                StringBuilder sb = new StringBuilder(6);

                //  ((Home)context).printValues();


                sb.append(codeFeild1.getText().toString());
                sb.append(codeFeild2.getText().toString());
                sb.append(codeFeild3.getText().toString());
                sb.append(codeFeild4.getText().toString());
                sb.append(codeFeild5.getText().toString());
                sb.append(codeFeild6.getText().toString());
                String invitationCode = sb.toString();
                //   Toast.makeText(context, invitationCode, Toast.LENGTH_SHORT).show();

                database = FirebaseDatabase.getInstance().getReference("rooms");
                database.orderByChild("invitationCode").equalTo(invitationCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String roomName = null;
                            String id = null;
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                roomName = ds.child("name").getValue(String.class);
                                id = ds.child("id").getValue(String.class);
                                //  pushUserToFireBase(id, roomName);
                            }
                            String finalId = id;
                            String finalRoomName = roomName;
                            database = FirebaseDatabase.getInstance().getReference("roomMembers");
                            database.child(id).orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        dialog.cancel();
                                        //   openNewRoom(finalId, finalRoomName);
                                        Toast.makeText(context, "Room already exist !", Toast.LENGTH_SHORT).show();
                                    } else {
                                        pushUserToFireBase(finalId, finalRoomName);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    dialog.cancel();
                                    Toast.makeText(context, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            //   Toast.makeText(context, "Exist : " + id, Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.cancel();
                            Toast.makeText(context, "Sorry, that code is not valid", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog.cancel();
                        Toast.makeText(context, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        //  context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        //   showKeypoard(codeFeild1);
        UIUtil.showKeyboardInDialog(dialog, codeFeild1);
        codeFeild1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        codeFeild2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        codeFeild3.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        codeFeild4.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        codeFeild5.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        codeFeild6.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        codeFeild6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {


                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    return false;
                }
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (codeFeild6.getText().toString().length() == 0) {
                        codeFeild5.setEnabled(true);
                        codeFeild5.requestFocus();
                    }
                }

                return false;

            }
        });

        codeFeild5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //   Log.e("keyDownn5", "enter");
                    return false;
                }

                if (keyCode == KeyEvent.KEYCODE_DEL) {

                    //   Log.e("keyDownn5", "delete");
                    if (codeFeild5.getText().toString().length() == 0) {


                        codeFeild4.setEnabled(true);
                        codeFeild4.requestFocus();
                    }
                } else {
                    //   Log.e("keyDownn5", "other");
                    if (codeFeild5.getText().toString().length() > 0) {

                        codeFeild6.setEnabled(true);
                        codeFeild6.requestFocus();
                    }
                }

                return false;

            }
        });
        codeFeild4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    return false;
                }

                if (keyCode == KeyEvent.KEYCODE_DEL) {


                    if (codeFeild4.getText().toString().length() == 0) {


                        codeFeild3.setEnabled(true);
                        codeFeild3.requestFocus();
                    }
                } else {

                    if (codeFeild4.getText().toString().length() > 0) {

                        codeFeild5.setEnabled(true);
                        codeFeild5.requestFocus();
                    }
                }

                return false;

            }
        });
        codeFeild3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    return false;
                }

                if (keyCode == KeyEvent.KEYCODE_DEL) {


                    if (codeFeild3.getText().toString().length() == 0) {


                        codeFeild2.setEnabled(true);
                        codeFeild2.requestFocus();
                    }
                } else {

                    if (codeFeild3.getText().toString().length() > 0) {

                        codeFeild4.setEnabled(true);
                        codeFeild4.requestFocus();
                    }
                }

                return false;

            }
        });
        codeFeild2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    return false;
                }

                if (keyCode == KeyEvent.KEYCODE_DEL) {


                    if (codeFeild2.getText().toString().length() == 0) {


                        codeFeild1.setEnabled(true);
                        codeFeild1.requestFocus();
                    }
                } else {

                    if (codeFeild2.getText().toString().length() > 0) {

                        codeFeild3.setEnabled(true);
                        codeFeild3.requestFocus();
                    }
                }

                return false;

            }
        });

        codeFeild1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (codeFeild1.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild2.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild3.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild4.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild5.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild6.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else {
                    enableButten(submit);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    codeFeild2.setEnabled(true);
                    codeFeild2.requestFocus();

                }
            }
        });
        codeFeild2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (codeFeild1.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild2.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild3.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild4.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild5.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild6.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else {
                    enableButten(submit);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {


                    codeFeild3.setEnabled(true);
                    codeFeild3.requestFocus();
                }
            }
        });
        codeFeild3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (codeFeild1.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild2.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild3.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild4.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild5.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild6.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else {
                    enableButten(submit);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {


                    codeFeild4.setEnabled(true);
                    codeFeild4.requestFocus();
                }
            }
        });

        codeFeild4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (codeFeild1.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild2.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild3.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild4.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild5.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild6.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else {
                    enableButten(submit);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    codeFeild5.setEnabled(true);
                    codeFeild5.requestFocus();
                }
            }
        });
        codeFeild5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("beforeText", "beforeeee");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (codeFeild1.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild2.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild3.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild4.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild5.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild6.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else {
                    enableButten(submit);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("afterText", "after");
                if (s.toString().length() > 0) {
                    codeFeild6.setEnabled(true);
                    codeFeild6.requestFocus();
                }
            }

        });
        codeFeild6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("beforeText", "beforeeee");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (codeFeild1.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild2.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild3.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild4.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild5.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else if (codeFeild6.getText().toString().isEmpty()) {
                    disenableButton(submit);
                } else {
                    enableButten(submit);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("afterText", "after");

                //   codeFeild6.setEnabled(true);
                //    codeFeild6.requestFocus();
            }

        });
    }

    private void enableButten(Button button) {
        Drawable drawable = context.getResources().getDrawable(R.drawable.submit_btn_active);

        button.setEnabled(true);
        button.setBackground(drawable);
    }

    private void disenableButton(Button button) {
        Drawable drawable = context.getResources().getDrawable(R.drawable.submit_btn);
        button.setEnabled(false);
        button.setBackground(drawable);
    }

    private void pushUserToFireBase(String roomId, String roomName) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String id = database.push().getKey();
        //  RoomModel roomModel1 = new RoomModel(roomName, roomCapacity, id);


        // to add the current roomId for userRooms tree after getting the size
        database.child("userRooms").child(user.getUid()).child(roomId).child("id").setValue(roomId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // to add a new userId for roomMembers tree after getting the size
                database.child("roomMembers").child(roomId).child(user.getUid()).child("id").setValue(user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        increseRoomCapacity(roomId);
                        //   refresh();
                        openNewRoom(roomId, roomName);
                        Toast.makeText(context, "Invitation accepted succesfly", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        //  Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
    }

    private void openNewRoom(String roomId, String roomName) {
        Intent intent = new Intent(context, Room.class);
        //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //     ((Home) context).finish();
        intent.putExtra("roomId", (String) roomId);
        intent.putExtra("roomName", roomName);
        context.startActivity(intent);
    }

    private void increseRoomCapacity(String roomId) {
        roomRef = FirebaseDatabase.getInstance().getReference("rooms");
        roomRef.child(roomId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                dialog.cancel();
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    String lastRoomCapcity = snapshot.child("roomCapacity").getValue(String.class);
                    int newRoomCapacity = Integer.parseInt(lastRoomCapcity);
                    newRoomCapacity++;
                    roomRef.child(roomId).child("roomCapacity").setValue(String.valueOf(newRoomCapacity));
                }
            }
        });
    }

    private void showProgreesBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.load_dialoge, null);
        ProgressBar progressBar = view.findViewById(R.id.profilePbar);

        builder.setView(view);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


    }

}
