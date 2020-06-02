package com.beecoder.notes;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder> {
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot snapshot);
        void onCheckBoxClick(boolean isChecked, DocumentSnapshot snapshot);
        void onDeleteItem(DocumentSnapshot snapshot);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView note, date;
        CheckBox checkBox;
        public NoteViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            note = itemView.findViewById(R.id.noteTextView);
            date = itemView.findViewById(R.id.dateTextView);
            checkBox = itemView.findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener((view, isChecked) -> {
                int itemPosition = getAdapterPosition();
                DocumentSnapshot snapshot = getSnapshots().getSnapshot(itemPosition);
                if (getItem(itemPosition).isCompleted() != isChecked)
                    onItemClickListener.onCheckBoxClick(isChecked, snapshot);
            });
            itemView.setOnClickListener(v->onItemClickListener.onItemClick(getSnapshots().getSnapshot(getAdapterPosition())));
        }
        void deleteItem(){
            onItemClickListener.onDeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note currentNote) {
        CharSequence dateSequence = DateFormat.format("EEEE, MMM d, yyyy h:mm:ss a", currentNote.getCreated().toDate());
        holder.note.setText(currentNote.getText());
        holder.date.setText(dateSequence);
        holder.checkBox.setChecked(currentNote.isCompleted());
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_row, parent, false);
        return new NoteViewHolder(itemView, onItemClickListener);
    }
}

