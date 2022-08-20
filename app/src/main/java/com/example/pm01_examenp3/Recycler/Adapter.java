package com.example.pm01_examenp3.Recycler;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pm01_examenp3.EditarActivity;
import com.example.pm01_examenp3.MainActivity;
import com.example.pm01_examenp3.R;
import com.example.pm01_examenp3.clases.medicina;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.MedicinasviewHolder>{
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    ArrayList<medicina> medicinas = new ArrayList<>();


    public Adapter(ArrayList<medicina> medicinas) {

        this.medicinas = medicinas;
    }

    @NonNull
    @Override
    public MedicinasviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.filas, parent, false);
        MedicinasviewHolder holder = new MedicinasviewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MedicinasviewHolder holder, int position) {

        medicina Medicina = medicinas.get(position);
        int posicion = position;
        holder.txtNombre.setText(Medicina.getMedicamento());
        holder.Imagen.setImageBitmap(Medicina.getfoto());
        holder.cantidad.setText(Medicina.getCantidad().toString() + " dosis");


        if(Medicina.getTiempo().equals("diarias")) {
            holder.periocidad.setText("Cada dia");
        }
        else
        {
            if(Medicina.getPeriocidad() > 1) {
                holder.periocidad.setText("Cada " + Medicina.getPeriocidad().toString() + " horas");
            }
            else
            {
                holder.periocidad.setText("Cada hora");
            }
        }

        holder.borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete(id);
                mFirestore.collection("medicina").whereEqualTo("id", Medicina.getId())
                        .whereEqualTo("medicamento",Medicina.getMedicamento())
                        .whereEqualTo("inicio_tratamiento",Medicina.getInicio_tratamiento())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                        documentSnapshot.getReference().delete();
                                        medicinas.remove(posicion);
                                        Adapter.this.notifyDataSetChanged();
                                        Toast.makeText(v.getContext(), "Medicina eliminada",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
            }
        });

        holder.editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext().getApplicationContext(), EditarActivity.class);
                i.putExtra("id", Medicina.getId());
                i.putExtra("medicamento",Medicina.getMedicamento());
                //i.putExtra("inicio_tratamiento",Medicina.getInicio_tratamiento());
                v.getContext().startActivity(i);

            }
        });

    }


    @Override
    public int getItemCount() {
        return this.medicinas.size();
    }

    public static class MedicinasviewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, cantidad, periocidad;
        ImageView Imagen;
        ImageView borrar,editar;


        private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        public MedicinasviewHolder(@NonNull View itemView) {
            super(itemView);


            txtNombre = (TextView) itemView.findViewById(R.id.nombre);
            Imagen = (ImageView) itemView.findViewById(R.id.photo);
            cantidad = (TextView) itemView.findViewById(R.id.cantidad);
            periocidad = (TextView) itemView.findViewById(R.id.periocidad);
            borrar = (ImageView) itemView.findViewById(R.id.btn_eliminar);
            editar = (ImageView) itemView.findViewById(R.id.btn_editar);
        }

    }
}