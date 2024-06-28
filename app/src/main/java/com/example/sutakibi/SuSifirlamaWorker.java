package com.example.sutakibi;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SuSifirlamaWorker extends Worker {

    public SuSifirlamaWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("SuSifirlamaWorker", "Çalışmaya başladı!");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("kullanici");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("SuSifirlamaWorker", "Firebase'den veri alındı");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    users user = snapshot.getValue(users.class);
                    if (user != null) {
                        // Su miktarını sıfırla
                        snapshot.getRef().child("suMiktari").setValue(0);
                        Log.d("SuSifirlamaWorker", "güncellemeye girdi");

                    }
                    else {
                        Log.d("SuSifirlamaWorker", "güncelleme yok");

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("SuSifirlamaWorker", "loadPost:onCancelled", databaseError.toException());
            }
        });

        return Result.success();
    }
}
