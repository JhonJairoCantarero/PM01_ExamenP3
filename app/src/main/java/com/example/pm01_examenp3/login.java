package com.example.pm01_examenp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity implements View.OnClickListener {

    private TextView recuperarClave, ver;
    private EditText correo, contrasena;
    private FirebaseAuth mAuth;
    private Button registrarUsuario, LoginUsuario;
    private ProgressDialog mDialog;
    boolean passwordvisible;
    //final public static String REFERENCE_1 = "Users";
    DatabaseReference mRootReference;
    String as = "";
    String us = "";
    String rol = "";
    String uid = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);






        correo = (EditText) findViewById(R.id.NombreLogin);
        contrasena = (EditText) findViewById(R.id.ClaveLogin);

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

        registrarUsuario = (Button) findViewById(R.id.buttonRegistroLogin);
        registrarUsuario.setOnClickListener(this);

        LoginUsuario = (Button) findViewById(R.id.buttonIngresar);
        LoginUsuario.setOnClickListener(this);

        recuperarClave = (TextView) findViewById(R.id.Recuperar_Clave);
        recuperarClave.setOnClickListener(this);

        mDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonIngresar:
                mDialog.setMessage("Espere Un Momento...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                userLogin();
                mDialog.dismiss();
                break;
            case R.id.buttonRegistroLogin:
                startActivity(new Intent(this, RegistroActivity.class));
                break;
            case R.id.Recuperar_Clave:
                startActivity(new Intent(this, Recuperar_Clave.class));
                break;


        }
    }


    private void userLogin() {
        String email = correo.getText().toString().trim();
        String clave = contrasena.getText().toString().trim();
        if (email.isEmpty()) {
            correo.setError("Campo Obligatorio");
            correo.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mDialog.dismiss();
            correo.setError("Correo Incorrecto");
            correo.requestFocus();
            return;
        }
        if (clave.isEmpty()) {
            mDialog.dismiss();
            contrasena.setError("Campo Obligatorio");
            contrasena.requestFocus();
            return;
        }
        if (clave.length() < 6) {
            mDialog.dismiss();
            contrasena.setError("Contrasena Menor De 6 Caracteres");
            contrasena.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, clave).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
                    //FirebaseUser User = firebaseAuth.getCurrentUser();
                    if (User.isEmailVerified()) {
                        uid = task.getResult().getUser().getUid();
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        firebaseDatabase.getReference().child("Users").child(uid).child("as").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                rol = snapshot.getValue(String.class);

                                if (rol.equals("usuario")) {
                                    Intent in = new Intent(login.this, menu.class);
                                    startActivity(in);
                                    mDialog.dismiss();
                                    guardarEstadoUser();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        User.sendEmailVerification();
                        Toast.makeText(login.this, "Verifica Tu Correo", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();

                    }
                } else {
                    Toast.makeText(login.this, "Usuario o Contrasena Incorrecta", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }
        });
    }

    public void guardarEstadoUser() {
        SharedPreferences preferences3 = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        boolean estado3 = true;
        SharedPreferences.Editor editor3 = preferences3.edit();
        editor3.putBoolean("estado_2", estado3);
        editor3.commit();
    }





}