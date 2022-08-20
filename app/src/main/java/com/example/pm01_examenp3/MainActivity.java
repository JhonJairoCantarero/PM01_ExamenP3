package com.example.pm01_examenp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pm01_examenp3.clases.medicina;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Hashtable<String, Object> m;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();;
    EditText nombre_medicamento, des_medicamento, cantidad, periocidad;
    ImageView img_medicina;
    Spinner tiempo;
    Button camara, guardar;
    String  valor_tiempo = "";
    medicina mm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombre_medicamento = (EditText) findViewById(R.id.txtnombre_medicina);
        des_medicamento = (EditText) findViewById(R.id.txtdes_medicinaeditar);
        cantidad = (EditText) findViewById(R.id.txtcantidad_medicinaeditar);
        periocidad = (EditText) findViewById(R.id.txtperiocidad_medicinaeditar);
        img_medicina = (ImageView) findViewById(R.id.img_medicinaeditar);
        tiempo = (Spinner) findViewById(R.id.spinner_tiempoeditar);
        camara = (Button) findViewById(R.id.btnfoto_medicinaeditar);
        guardar = (Button) findViewById(R.id.btn_guardareditar);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregar();
            }
        });

        tiempo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    valor_tiempo = "";
                    periocidad.setEnabled(false);
                }else if(position == 1){
                    valor_tiempo = "horas";
                    periocidad.setEnabled(true);
                }else if(position == 2){
                    valor_tiempo = "diarias";
                    periocidad.setText("1");
                    periocidad.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
/*
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregar();
            }
        });
*/
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                    return;
                }

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 103);
                    return;
                }

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 104);
                    return;
                }

                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error al abrir la camara", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (REQUEST_IMAGE_CAPTURE) : {
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    img_medicina.setImageBitmap(imageBitmap);
                }
                break;
            }
        }
    }


    public void agregar(){
        //mostrarDialogoVacios();
        //DocumentReference id;
        //igualando variables
        String nombre = nombre_medicamento.getText().toString();
        String descripcion = des_medicamento.getText().toString();
        String Can = cantidad.getText().toString();
        String Periocidad = periocidad.getText().toString();
        //Validaciones del menu
        if (img_medicina.getDrawable() == null) {
            mostrarDialogoImagenNoTomada();
        }else if (nombre.isEmpty()){
            nombre_medicamento.setError("Este campo no puede estar vacio.");
        } else if (descripcion.isEmpty()) {
            des_medicamento.setError("Este campo no puede estar vacio.");
        } else if (Can.isEmpty()) {
            cantidad.setError("Este campo no puede estar vacio");
        } else if (Periocidad.isEmpty()){
            if(valor_tiempo == "horas"){
                periocidad.setError("Este campo esta vacio");
            }
        }else {

            m = new Hashtable<>();

            Bitmap foto = ((BitmapDrawable) img_medicina.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            foto.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            m.put("foto", Blob.fromBytes(data));
            m.put("medicamento", nombre_medicamento.getText().toString());
            m.put("descripcion", des_medicamento.getText().toString());
            m.put("cantidad", Integer.parseInt(cantidad.getText().toString()));
            m.put("tiempo", valor_tiempo);
            m.put("inicio_tratamiento", new Timestamp(new Date()));
            m.put("paciente", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            if (valor_tiempo == "horas") {
                m.put("periocidad", Integer.parseInt(periocidad.getText().toString()));
            } else {
                m.put("periocidad", 1);
            }
            mFireStore.collection("medicina").add(m).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        Hashtable<String, Object> id_medicina = new Hashtable<>();
                        DocumentReference id = task.getResult();
                        String id_nombre = id.getId();
                        Integer id_number =0;
                        for(int i=0; i<id_nombre.length(); i++){
                            id_number += (int)id_nombre.charAt(i);

                        }
                        Timestamp t = (Timestamp) m.get("inicio_tratamiento");
                        id_number += t.toDate().getYear();
                        id_number += t.toDate().getMonth();
                        id_number += t.toDate().getDate();
                        id_number += t.toDate().getHours();
                        id_number += t.toDate().getMinutes();
                        id_number += t.toDate().getSeconds();
                        id_medicina.put("id", id_number);
                        id.update(id_medicina);
                        Toast.makeText(getApplicationContext(), "agregado correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al agregar", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void mostrarDialogoVacios() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta de Vacíos")
                .setMessage("No puede dejar ningún campo vacío")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    //Validación de la imagen
    private void mostrarDialogoImagenNoTomada() {
        new AlertDialog.Builder(getApplicationContext())
                .setTitle("Alerta De Imagen De Menu")
                .setMessage("No se ha agregado ninguna fotografía, Añadir Imagen")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent, "Seleccione la apliaccione"),10);
                    }
                }).show();
    }



}