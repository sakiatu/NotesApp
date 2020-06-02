package com.beecoder.notes;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Date;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        findViewById(R.id.fab).setOnClickListener(v -> showDialog());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                NoteAdapter.NoteViewHolder noteViewHolder = (NoteAdapter.NoteViewHolder) viewHolder;
                noteViewHolder.deleteItem();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red))
                    .addActionIcon(R.drawable.icon_delete)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void showDialog() {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), MyDialog.DIALOG_NOTE_ADD);
        dialog.setOnDialogClickListener(this::createNote);
    }

    private void createNote(String text) {
        String userId = FirebaseAuth.getInstance().getUid();
        Note note = new Note(text, false, new Timestamp(new Date()), userId);
        FirebaseFirestore.getInstance().collection("notes").add(note)
                .addOnSuccessListener
                        (documentReference -> Toast.makeText(this, "Note Added", Toast.LENGTH_SHORT).show())
                .addOnFailureListener
                        (e -> Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
    }


    private void openLoginActivity() {
        startActivity(new Intent(this, LoginRegisterActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.logOut:
                logOut();
                break;
            case R.id.profile:
                openProfile();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    private void logOut() {
        AuthUI.getInstance().signOut(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        //if user sign out then this method will be called and user should be null
        if (firebaseAuth.getCurrentUser() == null) {
            openLoginActivity();
            return;
        }
        initNoteListView(firebaseAuth.getCurrentUser());

    }

    private void initNoteListView(FirebaseUser user) {
        Query query = FirebaseFirestore.getInstance()
                .collection("notes")
                .whereEqualTo("userId", user.getUid())
                .orderBy("completed")
                .orderBy("created", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        noteAdapter = new NoteAdapter(options);
        recyclerView.setAdapter(noteAdapter);
        noteAdapter.startListening();
        noteAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot) {
                Note note = snapshot.toObject(Note.class);

                MyDialog dialog = new MyDialog(note.getText());
                dialog.show(getSupportFragmentManager(), MyDialog.DIALOG_NOTE_UPDATE);
                dialog.setOnDialogClickListener(text -> {
                    note.setText(text);
                    snapshot.getReference().set(note);
                });
            }

            @Override
            public void onCheckBoxClick(boolean isChecked, DocumentSnapshot snapshot) {
                snapshot.getReference().update("completed", isChecked);
            }

            @Override
            public void onDeleteItem(DocumentSnapshot snapshot) {
                Note note = snapshot.toObject(Note.class);
                snapshot.getReference().delete()
                        .addOnSuccessListener(aVoid -> {
                            Snackbar.make(recyclerView, note.getText(), BaseTransientBottomBar.LENGTH_LONG)
                                    .setAction("Undo", v -> snapshot.getReference().set(note))
                                    .show();
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            noteAdapter.stopListening();
    }

}
