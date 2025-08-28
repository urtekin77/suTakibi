#include <Wire.h> 

#include <LiquidCrystal_I2C.h> 

#include <SoftwareSerial.h> 

 

SoftwareSerial esp8266(10, 11); // RX, TX 

 

// LCD ayarları 

LiquidCrystal_I2C lcd(0x27, 16, 2);  // SDA => A4 - SCL => A5 

const int motorPin1 = 9; // Motor sürücü IN4 => 9 

const int suMiktarBelirleme = A0; // Gelecek su miktarının belirlenmesi 

const int kullaniciButon = 3; // Kullanıcı değiştirme butonu pin 

const int motorButon = 2; // Motor kontrol butonu pin 

float suMiktari = 0; 

bool motorCalisiyor = false; // Motorun çalışma durumunu belirleyen bayrak 

const float litreBasinaSure = 34.29; // Litre başına süre (saniye) 

 

const int maxUsers = 10; // Maksimum kullanıcı sayısı 

String kullaniciAdlari[maxUsers]; // Kullanıcı adlarını tutan dizi 

int userCount = 0; // Mevcut kullanıcı sayısı 

int currentUserIndex = 0; // Şu anki kullanıcı indeksi 

 

void setup() { 

  lcd.begin(); // LCD'yi başlat 

  lcd.backlight(); // Arka ışığı aç 

  Serial.begin(9600); 

  esp8266.begin(9600); 

  lcd.setCursor(3, 0); 

  lcd.print("MERHABA"); 

 

  pinMode(motorPin1, OUTPUT); 

  pinMode(suMiktarBelirleme, INPUT); 

  pinMode(kullaniciButon, INPUT); // Kullanıcı değiştirme butonu giriş olarak ayarlandı 

  pinMode(motorButon, INPUT); // Motor kontrol butonu giriş olarak ayarlandı 

 

  digitalWrite(motorPin1, LOW); 

 

  for (int i = 0; i < maxUsers; i++) { 

    kullaniciAdlari[i] = ""; 

  } 

} 

 

void loop() { 

  // ESP8266'dan gelen veri kontrolü 

  if (esp8266.available()) { 

    String receivedData = esp8266.readStringUntil('\n'); 

    receivedData.trim(); // Başındaki ve sonundaki boşlukları kaldır 

    Serial.println(receivedData); 

    if (isValidUsername(receivedData)) { 

      addUser(receivedData); 

    } else { 

      Serial.println("Geçersiz kullanıcı adı alındı."); 

    } 

  } 

   

  // Kullanıcı değiştirme butonuna basılıp basılmadığını kontrol et 

  if (digitalRead(kullaniciButon) == HIGH) { 

    // Kullanıcıyı değiştir 

    currentUserIndex = (currentUserIndex + 1) % userCount; 

    displayUserInfo(kullaniciAdlari[currentUserIndex]); 

    delay(500); // Tuş basımının düzgün algılanması için küçük bir gecikme 

  } 

 

   

 

  // Motor çalışmıyorsa su miktarını güncelle 

  if (!motorCalisiyor) { 

    int analogDeger = analogRead(suMiktarBelirleme); 

    suMiktari = map(analogDeger, 0, 1023, 10, 200) / 100.0; // 0.1 ile 2 arasında değerler 

    lcd.setCursor(0, 1); 

    lcd.print("Su Miktari: "); 

    lcd.print(suMiktari); 

    delay(500); 

  } 

 

  // Motor kontrol butonuna basılıp basılmadığını kontrol et 

  if (digitalRead(motorButon) == HIGH && !motorCalisiyor) { 

    motorCalisiyor = true; 

    digitalWrite(motorPin1, HIGH); 

    delay(suMiktari* 40000); // suMiktari süresi boyunca motoru çalıştır 

    digitalWrite(motorPin1, LOW); 

    motorCalisiyor = false; 

    esp8266.print(kullaniciAdlari[currentUserIndex]); 

    esp8266.print("-"); 

    esp8266.println(suMiktari); 

    delay(300); // Tuş basımının düzgün algılanması için küçük bir gecikme 

  } 

} 

 

void displayUserInfo(const String& data) { 

  lcd.clear(); 

  lcd.setCursor(0, 0); 

  lcd.print(data); 

} 

 

void addUser(const String& kullaniciAdi) { 

  if (userCount < maxUsers) { 

    kullaniciAdlari[userCount] = kullaniciAdi; 

    userCount++; 

    Serial.println("Kullanıcı eklendi: " + kullaniciAdi); 

  } else { 

    Serial.println("Kullanıcı dizisi dolu!"); 

  } 

} 

 

bool isValidUsername(const String& username) { 

  // Geçersiz karakterlerin olup olmadığını kontrol edin 

  for (int i = 0; i < username.length(); i++) { 

    char c = username.charAt(i); 

    if (!isPrintable(c) || c == '\n' || c == '\r') { 

      return false; 

    } 

  } 

  // Boş stringleri kabul etmeyin 

  if (username.length() == 0) { 

    return false; 

  } 

  return true; 

} 
