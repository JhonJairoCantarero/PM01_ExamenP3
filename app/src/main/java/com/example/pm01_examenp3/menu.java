package com.example.pm01_examenp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import com.example.pm01_examenp3.Recycler.lista;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class menu extends AppCompatActivity {


    Button ingresar_medicamento, ver_lista,cerrar;
    Runnable r;
    Handler h = new Handler(Looper.getMainLooper());
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);



        ingresar_medicamento = (Button) findViewById(R.id.btnIngresarM);

        cerrar = (Button) findViewById(R.id.buttonLogout);
        ver_lista = (Button) findViewById(R.id.btnListaM);

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstado();
                FirebaseAuth.getInstance().signOut();
                Intent in = new Intent(menu.this, login.class);
                startActivity(in);
                finish();
            }
        });

        ingresar_medicamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(in);
            }
        });

        ver_lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), lista.class);
                startActivity(in);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel("canal_id", "canal1", NotificationManager.IMPORTANCE_HIGH);
            canal.setDescription("primer canal");
            NotificationManager nManager = getSystemService(NotificationManager.class);
            nManager.createNotificationChannel(canal);
        }

        NotificationManagerCompat nManagerCompat = NotificationManagerCompat.from(this);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }
        h.postDelayed(r = new Runnable() {
            @Override
            public void run() {
                h.postDelayed(r, 60000);

                db.collection("medicina")
                        .whereEqualTo("paciente", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        Calendar calI = Calendar.getInstance();
                                        calI.setTime(doc.getTimestamp("inicio_tratamiento").toDate());

                                        long inicio = calI.getTimeInMillis() / 60000;
                                        long ahora = Calendar.getInstance().getTimeInMillis() / 60000;
                                        long tiempo = ahora - inicio;
                                        long periodicidad = doc.getLong("periocidad");

                                        if (doc.getString("tiempo").equals("horas")) {
                                            periodicidad *= 60;
                                        }

                                        if (doc.getString("tiempo").equals("diarias")) {
                                            periodicidad *= 60 * 24;
                                        }

                                        if (tiempo % periodicidad == 0 && tiempo>0) {
                                            int id = Math.toIntExact(doc.getLong("id"));
                                            nManagerCompat.notify(id, new NotificationCompat.Builder(menu.this, "canal_id")
                                                    .setSmallIcon(R.drawable.fotomedicamento)
                                                    .setContentTitle("Recordatorio de medicina")
                                                    .setContentText("Debe tomar " + doc.getLong("cantidad").toString() + " dosis de " + doc.getString("medicamento"))
                                                    .setPriority(Notification.PRIORITY_MAX)
                                                    .build());
                                        }
                                    }
                                }
                            }
                        });
            }
        }, 0);

    }


    public void cambiarEstado() {
        SharedPreferences preferences3 = this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        boolean estado3 = false;
        SharedPreferences.Editor editor3 = preferences3.edit();
        editor3.putBoolean("estado_2", estado3);
        editor3.commit();

    }


}