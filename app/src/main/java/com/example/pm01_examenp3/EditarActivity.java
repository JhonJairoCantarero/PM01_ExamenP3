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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pm01_examenp3.Recycler.lista;
import com.example.pm01_examenp3.clases.medicina;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Hashtable;

public class EditarActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Hashtable<String, Object> m;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();;
    EditText nombre_medicamento, des_medicamento, cantidad, periocidad;
    ImageView img_medicina;
    Spinner tiempo;
    Button camara, guardar2;
    String  valor_tiempo = "";
    DocumentReference documentReference;
    medicina mm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        Integer id = getIntent().getExtras().getInt("id");
        String medicamento = getIntent().getStringExtra("medicamento");
        String initra = getIntent().getStringExtra("inicio_tratamiento");

        nombre_medicamento = (EditText) findViewById(R.id.txtnombre_medicinaeditar);
        des_medicamento = (EditText) findViewById(R.id.txtdes_medicinaeditar);
        cantidad = (EditText) findViewById(R.id.txtcantidad_medicinaeditar);
        periocidad = (EditText) findViewById(R.id.txtperiocidad_medicinaeditar);
        img_medicina = (ImageView) findViewById(R.id.img_medicinaeditar);
        tiempo = (Spinner) findViewById(R.id.spinner_tiempoeditar);
        camara = (Button) findViewById(R.id.btnfoto_medicinaeditar);
        guardar2 = (Button) findViewById(R.id.btn_editartext);
        // mFireStore = FirebaseFirestore.getInstance();

        Log.e("id:", ""+id);
        Log.e("medicamento:", ""+medicamento);
        // Log.e("Datos Roles:", ""+initra);


            getMed(id,medicamento);
            guardar2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agregar();
                    finish();
                    Intent i = new Intent(getApplicationContext(), lista.class);
                    startActivity(i);
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
                if (ContextCompat.checkSelfPermission(EditarActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(EditarActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                    return;
                }

                if (ContextCompat.checkSelfPermission(EditarActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(EditarActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 103);
                    return;
                }

                if (ContextCompat.checkSelfPermission(EditarActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(EditarActivity.this, new String[]{Manifest.permission.CAMERA}, 104);
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
           // mFireStore.collection("medicina").update(m).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            documentReference.update(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(EditarActivity.this,"Se Actualizo Correctamente", Toast.LENGTH_SHORT).show();
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

    private void getMed(int id,String medicamentO) {
        mFireStore.collection("medicina").whereEqualTo("id", id)
                .whereEqualTo("medicamento",medicamentO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                //documentSnapshot.getReference().update();
                                mm = documentSnapshot.toObject(medicina.class);
                                nombre_medicamento.setText(mm.getMedicamento());
                                des_medicamento.setText(mm.getDescripcion());
                                cantidad.setText(mm.getCantidad().toString());
                                periocidad.setText(mm.getPeriocidad().toString());
                                img_medicina.setImageBitmap(mm.getfoto());
                                if (mm.getTiempo().equals("horas")) {
                                    tiempo.setSelection(1);
                                }else if(mm.getTiempo().equals("diarias")){
                                    tiempo.setSelection(2);
                                }
                                documentReference = documentSnapshot.getReference();

                                break;


                                //Toast.makeText(MainActivity.this, "M",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });


    }

/*
    private void getMed(Integer id){
        mFireStore.collection("medicina").whereEqualTo("id",id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("medicamento");
                String desc = documentSnapshot.getString("descripcion");
                String photoPet = documentSnapshot.getString("foto");

                nombre_medicamento.setText(name);
                des_medicamento.setText(desc);

                /*
                try {
                    if(!photoPet.equals("")){
                        Toast toast = Toast.makeText(getApplicationContext(), "Cargando foto", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP,0,200);
                        toast.show();
                        Picasso.with(CreatePetActivity.this)
                                .load(photoPet)
                                .resize(150, 150)
                                .into(photo_pet);
                    }
                }catch (Exception e){
                    Log.v("Error", "e: " + e);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
*/


}