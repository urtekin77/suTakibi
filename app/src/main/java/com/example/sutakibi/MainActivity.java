package com.example.sutakibi;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.lifecycle.Observer;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import android.Manifest;
import com.example.sutakibi.WaterReminderWorker;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "AppPrefs";
    private static final String FIRST_RUN = "FirstRun";
    private DatabaseReference databaseReference;
    private Spinner kullaniciSpinner, cinsiyetSpinner, fizikselAktiviteSpinner, cevreselKosulSpinner, saglikDurumuSpinner;
    private String userInfo;
    private TextView gerekliSu, ictigiSu, progressText;
    private EditText suGiris, kilo, yas, boy;
    private Button suGirisButon, guncelle, suRaporuButon, kullaniciSilme;
    private ImageView kullaniciEkle;
    private List<users> userObjects = new ArrayList<>();
    private users selectedUser;
    private ProgressBar progressBar;
    private int selectedUserPosition = -1;
    private static final String CHANNEL_ID = "your_channel_id";
    private static final String TAG = "WaterReminderWorker";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Android 13 ve sonrası için POST_NOTIFICATIONS iznini kontrol etme ve isteme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean firstRun = settings.getBoolean(FIRST_RUN, true);

        if (firstRun) {
            // Bu kısım yalnızca uygulama ilk kez açıldığında çalışır
            startMyService();

            // First run'ı false olarak ayarla
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(FIRST_RUN, false);
            editor.apply();
        }


        final View customLayout = getLayoutInflater().inflate(R.layout.giris_lalert, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(customLayout);

        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText esp_ID = customLayout.findViewById(R.id.esp_ID);
                userInfo = esp_ID.getText().toString();
                startMainPage(userInfo);
            }
        });

        builder.setCancelable(false);
        builder.show();


    }


    private void startMainPage(String userInfo) {
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        kullaniciSpinner = findViewById(R.id.kullanici_spinner);
        gerekliSu = findViewById(R.id.gerekliSu);
        ictigiSu = findViewById(R.id.ictgiSu);
        suGiris = findViewById(R.id.suGiris);
        suGirisButon = findViewById(R.id.sugirisButon);
        kilo = findViewById(R.id.kilo);
        yas = findViewById(R.id.yas);
        boy = findViewById(R.id.boy);
        guncelle = findViewById(R.id.guncelle);
        kullaniciEkle = findViewById(R.id.kullaniciEkle);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        cinsiyetSpinner = findViewById(R.id.cinsiyet);
        fizikselAktiviteSpinner = findViewById(R.id.fizikselAktivite);
        cevreselKosulSpinner = findViewById(R.id.cevreselKosul);
        saglikDurumuSpinner = findViewById(R.id.saglikDurumu);

        suRaporuButon = findViewById(R.id.suRaporuButon);

        kullaniciSilme = findViewById(R.id.kullaniciSilme);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> userList = new ArrayList<>();
                userObjects.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    users user = snapshot.getValue(users.class);
                    if (user != null && user.getESP_ID().equals(userInfo)) {
                        userList.add(user.getKullaniciAd());
                        userObjects.add(user);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, userList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                kullaniciSpinner.setAdapter(adapter);

                if (selectedUserPosition != -1 && selectedUserPosition < userObjects.size()) {
                    kullaniciSpinner.setSelection(selectedUserPosition);
                }

                kullaniciSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedUser = userObjects.get(position);
                        selectedUserPosition = position;
                        updateUserInfo(selectedUser);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MainActivity", "loadPost:onCancelled", databaseError.toException());
            }
        });

        Log.d("suuuu", String.valueOf(selectedUser));
        suGirisButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedUser != null) {
                    String suGirisText = suGiris.getText().toString();
                    if (suGirisText.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Lütfen bir su miktarı girin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!isValidFloat(suGirisText)) {
                        Toast.makeText(MainActivity.this, "Geçersiz su miktarı", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    float yeniSuMiktari = selectedUser.getSuMiktari() + Float.parseFloat(suGirisText);
                    selectedUser.setSuMiktari(yeniSuMiktari);
                    ictigiSu.setText(String.format("%.2f L", yeniSuMiktari));

                    Map<String, Float> suGecmis = selectedUser.getSuGecmis();
                    if (suGecmis == null) {
                        suGecmis = new HashMap<>();
                    }
                    suGecmis.put(getCurrentDate(), yeniSuMiktari);
                    selectedUser.setSuGecmis(suGecmis);

                    databaseReference.child(selectedUser.getUser_ID()).setValue(selectedUser)
                            .addOnSuccessListener(aVoid -> {
                                updateProgressBar(selectedUser.getSuMiktari(), selectedUser.getGerekliSuMiktari());
                                Toast.makeText(MainActivity.this, "Su miktarı güncellendi", Toast.LENGTH_SHORT).show();
                                updateUserInfo(selectedUser);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MainActivity.this, "Su miktarı güncellenemedi", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });

        guncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedUser != null) {
                    String kiloStr = kilo.getText().toString();
                    String boyStr = boy.getText().toString();
                    String yasStr = yas.getText().toString();

                    if (kiloStr.isEmpty() || boyStr.isEmpty() || yasStr.isEmpty() || !isValidFloat(kiloStr) || !isValidFloat(boyStr) || !isValidInteger(yasStr)) {
                        Toast.makeText(MainActivity.this, "Geçersiz bilgi", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    float kiloBilgisi = Float.parseFloat(kiloStr);
                    float boyBilgisi = Float.parseFloat(boyStr);
                    int yasBilgisi = Integer.parseInt(yasStr);

                    selectedUser.setKilo(kiloBilgisi);
                    selectedUser.setBoy(boyBilgisi);
                    selectedUser.setAge(yasBilgisi);

                    selectedUser.setCinsiyet(cinsiyetSpinner.getSelectedItem().toString());
                    selectedUser.setAktivite(fizikselAktiviteSpinner.getSelectedItem().toString());
                    selectedUser.setCevresel(cevreselKosulSpinner.getSelectedItem().toString());
                    selectedUser.setSaglik(saglikDurumuSpinner.getSelectedItem().toString());

                    selectedUser.setGerekliSuMiktari(hesaplaGerekliSuMiktari(kiloBilgisi, boyBilgisi, yasBilgisi, selectedUser.getCinsiyet(), selectedUser.getAktivite(), selectedUser.getCevresel(), selectedUser.getSaglik()));

                    databaseReference.child(selectedUser.getUser_ID()).setValue(selectedUser)
                            .addOnSuccessListener(aVoid -> {
                                updateProgressBar(selectedUser.getSuMiktari(), selectedUser.getGerekliSuMiktari());
                                Toast.makeText(MainActivity.this, "Kullanıcı bilgileri güncellendi", Toast.LENGTH_SHORT).show();
                                updateUserInfo(selectedUser);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MainActivity.this, "Kullanıcı bilgileri güncellenemedi", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });

        suRaporuButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final View customLayout = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_dialog, null);
                builder.setView(customLayout);

                ColumnChartView chart = customLayout.findViewById(R.id.chart);

                databaseReference.child(selectedUser.getUser_ID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("olmaddddd", "onDataChange: dataSnapshot received");

                        if (!dataSnapshot.exists()) {
                            Log.d("olmaddddd", "onDataChange: No data found for user");
                            return;
                        }

                        DataSnapshot suGecmisSnapshot = dataSnapshot.child("suGecmis");
                        if (!suGecmisSnapshot.exists()) {
                            Log.d("olmaddddd", "onDataChange: No suGecmis data found");
                            return;
                        }

                        List<Column> columns = new ArrayList<>();
                        List<AxisValue> axisValues = new ArrayList<>();
                        int index = 0;

                        for (DataSnapshot snapshot : suGecmisSnapshot.getChildren()) {
                            String tarih = snapshot.getKey();
                            Double miktar = snapshot.getValue(Double.class);
                            if (tarih != null && miktar != null) {
                                Log.d("AG", "onDataChange: history received - Tarih: " + tarih + ", Miktar: " + miktar);
                                List<SubcolumnValue> values = new ArrayList<>();
                                values.add(new SubcolumnValue(miktar.floatValue(), ChartUtils.COLOR_BLUE));
                                columns.add(new Column(values).setHasLabels(true));
                                axisValues.add(new AxisValue(index).setLabel(tarih));
                                index++;
                            } else {
                                Log.d("TAG", "onDataChange: Invalid history data - Tarih: " + tarih + ", Miktar: " + miktar);
                            }
                        }

                        ColumnChartData data = new ColumnChartData(columns);

                        Axis axisX = new Axis();
                        axisX.setValues(axisValues);
                        axisX.setName("Tarih");
                        axisX.setTextColor(R.color.acikMavi);
                        axisX.setTextSize(14);
                        axisX.setHasTiltedLabels(true);
                        axisX.setMaxLabelChars(6); // Limit the number of characters per label to avoid overlap
                        axisX.setInside(false); // Move labels inside the chart
                        axisX.setHasLines(true); // Add lines for better readability
                        data.setAxisXBottom(axisX);

                        Axis axisY = new Axis().setHasLines(true);
                        axisY.setName("Miktar (L)");
                        axisY.setTextColor(R.color.acikMavi);
                        axisY.setTextSize(14);
                        data.setAxisYLeft(axisY);

                        chart.setColumnChartData(data);
                        chart.setZoomEnabled(true);
                        chart.setScrollEnabled(true);
                        chart.setInteractive(true);

                        Log.d("olmaddddd", "onDataChange: LineChartData set successfully");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("olmaddddd", "onDataChange: loadPost:onCancelled", databaseError.toException());
                    }
                });

                builder.setPositiveButton("Kapat", (dialog, which) -> dialog.dismiss());
                builder.show();
            }

        });

        kullaniciSilme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedUser != null){
                    databaseReference.child(selectedUser.getUser_ID()).removeValue();

                }
            }
        });

        kullaniciEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddUserDialog();
            }
        });


    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.kullanici_ekle_alert, null);
        builder.setView(customLayout);

        builder.setPositiveButton("Ekle", null);
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText kullaniciAd = customLayout.findViewById(R.id.kullaniciAdiEkle);
                EditText kilo = customLayout.findViewById(R.id.kiloKayit);
                EditText boy = customLayout.findViewById(R.id.boyKayit);
                EditText yas = customLayout.findViewById(R.id.yasKayit);

                String newUserAd = kullaniciAd.getText().toString();
                String kiloStr = kilo.getText().toString();
                String boyStr = boy.getText().toString();
                String yasStr = yas.getText().toString();

                if (newUserAd.isEmpty() || kiloStr.isEmpty() || boyStr.isEmpty() || yasStr.isEmpty() || !isValidFloat(kiloStr) || !isValidFloat(boyStr) || !isValidInteger(yasStr)) {
                    Toast.makeText(MainActivity.this, "Lütfen geçerli ve tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                    return;
                }

                float newKilo = Float.parseFloat(kiloStr);
                float newBoy = Float.parseFloat(boyStr);
                int newYas = Integer.parseInt(yasStr);

                Spinner cinsiyetSpinner = customLayout.findViewById(R.id.cinsiyetKayit);
                Spinner fizikselAktiviteSpinner = customLayout.findViewById(R.id.fizikselAktiviteKayit);
                Spinner cevreselKosulSpinner = customLayout.findViewById(R.id.cevreselKosulKayit);
                Spinner saglikDurumuSpinner = customLayout.findViewById(R.id.saglikDurumuKayit);

                String newCinsiyet = cinsiyetSpinner.getSelectedItem().toString();
                String newAktivite = fizikselAktiviteSpinner.getSelectedItem().toString();
                String newCevresel = cevreselKosulSpinner.getSelectedItem().toString();
                String newSaglik = saglikDurumuSpinner.getSelectedItem().toString();

                float newGerekliSuMiktari = hesaplaGerekliSuMiktari(newKilo, newBoy, newYas, newCinsiyet, newAktivite, newCevresel, newSaglik);
                String newUserId = UUID.randomUUID().toString();
                Map<String, Float> suGecmis = new HashMap<>();
                users newUser = new users(newUserId, userInfo, newYas, newAktivite, newBoy, newCevresel, newCinsiyet, newGerekliSuMiktari, newKilo, newUserAd, newSaglik, 0, suGecmis);

                DatabaseReference newUserRef = databaseReference.child(newUserId);
                newUserRef.setValue(newUser)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(MainActivity.this, "Yeni kullanıcı eklendi", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("MainActivity", "Yeni kullanıcı eklenemedi", e);
                            Toast.makeText(MainActivity.this, "Yeni kullanıcı eklenemedi", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void updateUserInfo(users user) {
        gerekliSu.setText(String.format("%.2f L", user.getGerekliSuMiktari()));
        ictigiSu.setText(String.format("%.2f L", user.getSuMiktari()));

        kilo.setText(String.valueOf(user.getKilo()));
        yas.setText(String.valueOf(user.getAge()));
        boy.setText(String.valueOf(user.getBoy()));

        setSpinnerValue(cinsiyetSpinner, user.getCinsiyet());
        setSpinnerValue(fizikselAktiviteSpinner, user.getAktivite());
        setSpinnerValue(cevreselKosulSpinner, user.getCevresel());
        setSpinnerValue(saglikDurumuSpinner, user.getSaglik());

        updateProgressBar(user.getSuMiktari(), user.getGerekliSuMiktari());
    }

    private boolean isValidFloat(String value) {
        try {
            Float.parseFloat(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private float hesaplaGerekliSuMiktari(float kilo, float boy, int yas, String cinsiyet, String aktivite, String cevresel, String saglik) {
        float vw = kilo * 0.033f;
        float paf;
        switch (aktivite) {
            case "Yüksek Aktivite":
                paf = 0.7f;
                break;
            case "Orta Aktivite":
                paf = 0.5f;
                break;
            case "Düşük Aktivite":
            default:
                paf = 0.3f;
                break;
        }

        float ecf;
        switch (cevresel) {
            case "Sıcak İklim":
                ecf = 0.5f;
                break;
            case "Ilıman İklim":
                ecf = 0.2f;
                break;
            case "Soğuk İklim":
            default:
                ecf = 0.0f;
                break;
        }

        float hcf;
        switch (saglik) {
            case "Hamilelik":
                hcf = 0.3f;
                break;
            case "Emzirme":
                hcf = 0.5f;
                break;
            case "Hastalık":
                hcf = 0.4f;
                break;
            case "Normal Durum":
            default:
                hcf = 0.0f;
                break;
        }

        float yasFaktoru;
        if (yas < 30) {
            yasFaktoru = 0.3f;
        } else if (yas >= 30 && yas < 55) {
            yasFaktoru = 0.4f;
        } else {
            yasFaktoru = 0.3f;
        }

        return vw + paf + ecf + hcf + yasFaktoru;
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(value);
        spinner.setSelection(position);
    }

    private void updateProgressBar(float suMiktari, float gerekliSuMiktari) {
        int progress = (int) ((suMiktari / gerekliSuMiktari) * 100);
        progressBar.setProgress(progress);
        progressText.setText(progress + "%");
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Bildirim izni verildi");
            } else {
                Log.d(TAG, "Bildirim izni reddedildi");
            }
        }
    }


    private void startMyService() {
        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);
    }
}
