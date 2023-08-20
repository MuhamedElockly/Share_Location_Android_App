package com.example.sharelocation.ui;

import android.content.Context;
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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.sharelocation.R;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

public class JoinRoom {
    private Context context;
    private Button submit;

    public JoinRoom(Context context) {
        this.context = context;
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
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        EditText codeFeild1 = view.findViewById(R.id.codeFeild1);
        EditText codeFeild2 = view.findViewById(R.id.codeFeild2);
        EditText codeFeild3 = view.findViewById(R.id.codeFeild3);
        EditText codeFeild4 = view.findViewById(R.id.codeFeild4);
        EditText codeFeild5 = view.findViewById(R.id.codeFeild5);
        EditText codeFeild6 = view.findViewById(R.id.codeFeild6);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder(6);

                //  ((Home)context).printValues();


                sb.append(codeFeild1.getText().toString());
                sb.append(codeFeild2.getText().toString());
                sb.append(codeFeild3.getText().toString());
                sb.append(codeFeild4.getText().toString());
                sb.append(codeFeild5.getText().toString());
                sb.append(codeFeild6.getText().toString());
                String invitationCode = sb.toString();
                Toast.makeText(context, invitationCode, Toast.LENGTH_SHORT).show();
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

                codeFeild6.setEnabled(true);
                codeFeild6.requestFocus();
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
}
