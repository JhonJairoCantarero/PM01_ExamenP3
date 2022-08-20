package com.example.pm01_examenp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener{
    //private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView registrarUsuario,LoginUsuario;
    private EditText nombre,correo,contrasena;
    DatabaseReference mRootReference;
    private FirebaseAuth mAuth;
    boolean passwordvisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();

        registrarUsuario = (Button) findViewById(R.id.buttonRegistro);
        registrarUsuario.setOnClickListener(this);

        LoginUsuario = (Button) findViewById(R.id.buttonInicio);
        LoginUsuario.setOnClickListener(this);

        nombre = (EditText)  findViewById(R.id.NombreR);
        correo = (EditText)  findViewById(R.id.CorreoR);
        contrasena = (EditText)  findViewById(R.id.ContrasenaR);

        contrasena.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Rigth=2;
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getRawX()>=contrasena.getRight()-contrasena.getCompoundDrawables()[Rigth].getBounds().width()){
                        int selection=contrasena.getSelectionEnd();
                        if(passwordvisible){
                            contrasena.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_off_24, 0);
                            contrasena.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordvisible=false;
                        }else{
                            contrasena.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_24, 0);
                            contrasena.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordvisible=true;
                        }
                        contrasena.setSelection(selection);
                        return  true;
                    }
                }

                return false;
            }
        });

        mRootReference = FirebaseDatabase.getInstance().getReference();
        mRootReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    usuario user = dataSnapshot.getValue(usuario.class);
                    String as = user.getAs();
                    Log.e("Datos Roles:", ""+as);
                    Log.e("Datos:", ""+dataSnapshot.getValue());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void onClick(View v) {
        switch ( v.getId()){
            case R.id.buttonRegistro:
                registrarUsuarios();
                break;
            case R.id.buttonInicio:
                startActivity(new Intent(this,login.class));
                break;
        }

    }



    private void registrarUsuarios(){
        String nom=  nombre.getText().toString().trim();
        String email=  correo.getText().toString().trim();
        String clave=  contrasena.getText().toString().trim();
        String as = "usuario";
        String uid = mAuth.getUid();


        if(nom.isEmpty()){
            nombre.setError("Campo Obligatorio");
            nombre.requestFocus();
            return;
        }

        if(email.isEmpty()){
            correo.setError("Campo Obligatorio");
            correo.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            correo.setError("Correo Incorrecto");
            correo.requestFocus();
            return;
        }

        if(clave.isEmpty()){
            contrasena.setError("Campo Obligatorio");
            contrasena.requestFocus();
            return;
        }

        if(clave.length() < 6){
            contrasena.setError("Contrasena Menor De 6 Caracteres");
            contrasena.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,clave)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            User user = new User(nom, email , clave, as, uid);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegistroActivity.this, "Registro Exitoso", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(RegistroActivity.this, "Usuario o Correo Ya Estan Ingresados", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(RegistroActivity.this, "Registro Fallo", Toast.LENGTH_SHORT).show();

                        }
                    }

                });

    }
}