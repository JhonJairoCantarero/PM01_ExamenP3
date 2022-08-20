package com.example.pm01_examenp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Recuperar_Clave extends AppCompatActivity {
    private EditText correo;
    private Button Recuperar_Clave,Inicio;
    FirebaseAuth auth;
    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_clave);

        correo = (EditText) findViewById(R.id.claveRecuperar);
        Recuperar_Clave = (Button) findViewById(R.id.buttonRecuperar_Clave);
        Inicio = (Button) findViewById(R.id.buttonInicio2);
        mDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        Recuperar_Clave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setMessage("Espere Un Momento...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                Recuperar_Claves();
            }
        });

        Inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Recuperar_Clave.this, login.class);
                startActivity(in);
                finish();
            }
        });


    }
    private void Recuperar_Claves(){
        String email = correo.getText().toString().trim();

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


        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Recuperar_Clave.this, "Revise su correo electrónico para restablecer su contraseña", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }else{
                    Toast.makeText(Recuperar_Clave.this, "¡intentar otra vez! algo malo paso", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
                //mDialog.dismiss();
            }
        });
    }
}