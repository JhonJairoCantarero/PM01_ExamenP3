package com.example.pm01_examenp3;

import static android.R.attr.id;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddMedicina extends AppCompatActivity {

    EditText nameMed, descMed, doseMed;
    TextView setTimeBtn, getTimeBtn;
    Button setRemind;
    ImageView backBtn;
    String UserID;
    String nameTxt, descTxt, doseTxt;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    DatabaseReference medRef;
    String saveCurrentDate, saveCurrentTime, productRandomKey;
    TimePickerDialog timePickerDialog;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicina);

        backBtn = findViewById(R.id.backhomebtn);
        nameMed = findViewById(R.id.medicineName);
        descMed = findViewById(R.id.descMedicine);
        doseMed = findViewById(R.id.doseMed);
        setTimeBtn = findViewById(R.id.btnAlert);
        getTimeBtn = findViewById(R.id.timeMed);

        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openTimePickerDialog(false);

            }
        });

        getTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        medRef = FirebaseDatabase.getInstance().getReference("Medicine");
        //UserID = fAuth.getCurrentUser().getUid();
        setRemind = findViewById(R.id.reminderBtn);

        setRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProduct();
            }
        });



    }

    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(
                AddMedicina.this,
                onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24r);
        timePickerDialog.setTitle("Set Time");
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if(calSet.compareTo(calNow) <= 0){
                //Today Set time passed, count to tomorrow
                calSet.add(Calendar.DATE, 1);
            }
            //setAlarm(calSet);
        }
    };


    private void ValidateProduct() {
        nameTxt = nameMed.getText().toString().trim();
        descTxt = descMed.getText().toString().trim();
        doseTxt = doseMed.getText().toString().trim();

        if (TextUtils.isEmpty(nameTxt)) {
            Toast.makeText(this, "Se requiere el nombre del medicamento", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(descTxt)) {
            Toast.makeText(this, "Se requiere la descripción", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(doseTxt)) {
            Toast.makeText(this, "Se requiere el número de dosis", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreOrderInformation(nameTxt,descTxt,doseTxt);
        }

    }

    /*private void setAlarm(Calendar calSet) {
        nameTxt = nameMed.getText().toString().trim();
        descTxt = descMed.getText().toString().trim();
        doseTxt = doseMed.getText().toString().trim();

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("name",nameTxt);
        intent.putExtra("dose",doseTxt);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),id,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calSet.getTimeInMillis(),pendingIntent);
    }*/


    private void StoreOrderInformation(String nameTxt, String descTxt, String doseTxt) {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        //To create a unique product random key, so that it doesn't overwrite other product
        productRandomKey = saveCurrentDate + saveCurrentTime;
        saveData(nameTxt,descTxt,doseTxt);
    }

    private void saveData(String nameTxt, String descTxt, String doseTxt) {
        HashMap<String, Object> medicMap = new HashMap<>();
        medicMap.put("medicineID",UserID);
        medicMap.put("name",nameTxt);
        medicMap.put("description", descTxt);
        medicMap.put("dose", doseTxt);

        String addData = medRef.push().getKey();
        Log.d(TAG, "saveData: "+addData);

        medRef.child(addData).updateChildren(medicMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    setReminder();
                    Intent intent = new Intent(AddMedicina.this, MainActivity.class);
                    startActivity(intent);
                    finish();



                    // loadingBar.dismiss();
                    Toast.makeText(AddMedicina.this, "Se agregó con éxito..", Toast.LENGTH_SHORT).show();
                }else{
                    String message = task.getException().toString();
                    Toast.makeText(AddMedicina.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setReminder() {


    }
}