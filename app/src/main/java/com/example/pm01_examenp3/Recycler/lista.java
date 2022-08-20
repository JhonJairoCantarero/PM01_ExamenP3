package com.example.pm01_examenp3.Recycler;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pm01_examenp3.R;
import com.example.pm01_examenp3.clases.medicina;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class lista extends AppCompatActivity {

    RecyclerView rv;
    ArrayList<medicina> orderArrayList = new ArrayList<>();
    Adapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista);

        rv = (RecyclerView) findViewById(R.id.lista);
        rv.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        myAdapter = new Adapter(orderArrayList);
        rv.setAdapter(myAdapter);
        db.collection("medicina").whereEqualTo("paciente",
                FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot doc:
                         task.getResult()){
                        orderArrayList.add( doc.toObject(medicina.class));
                    }
                    myAdapter.notifyDataSetChanged();
                }

            }
        });

    }
}