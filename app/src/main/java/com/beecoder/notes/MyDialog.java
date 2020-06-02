package com.beecoder.notes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MyDialog extends DialogFragment {
    public static final String DIALOG_NOTE_ADD = "addNote";
    public static final String DIALOG_NOTE_UPDATE = "updateNote";
    private String text;

    public MyDialog() {
    }

    public MyDialog(String text) {
        this.text = text;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = null;
        if (getTag().equals(DIALOG_NOTE_ADD)) dialog = getAddNoteDialog();
        if (getTag().equals(DIALOG_NOTE_UPDATE)) dialog = getUpdateNoteDialog();
        return dialog;
    }

    private Dialog getUpdateNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_note, null);
        EditText note_edt = layout.findViewById(R.id.note_edt);
        note_edt.setText(text);
        note_edt.setSelection(text.length());
        builder.setView(layout)
                .setTitle("Update Note")
                .setPositiveButton("update", (a, b) ->
                        onDialogClickListener.onPositiveButtonClick(note_edt.getText().toString()))
                .setNegativeButton("cancel", null);
        return builder.create();
    }

    private Dialog getAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_note, null);
        EditText note_edt = layout.findViewById(R.id.note_edt);
        builder.setView(layout)
                .setTitle("New Note")
                .setPositiveButton("create", (a, b) ->
                        onDialogClickListener.onPositiveButtonClick(note_edt.getText().toString()))
                .setNegativeButton("cancel", null);
        return builder.create();
    }

    private OnDialogClickListener onDialogClickListener;

    public interface OnDialogClickListener {
        void onPositiveButtonClick(String string);
    }

    public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener) {
        this.onDialogClickListener = onDialogClickListener;
    }
}