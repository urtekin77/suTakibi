#include <ESP8266WiFi.h> 

#include <FirebaseESP8266.h> 

#include <ArduinoJson.h> 

#include <SoftwareSerial.h> 

 

const char* ssid = "HUAWEI nova 8i"; 

const char* password = "gamze2002"; 

#define DEVICE_ID  "artKdrAvbpE34qdf357" 

 

#define FIREBASE_HOST "gomuluproject-1c459-default-rtdb.firebaseio.com" 

#define FIREBASE_AUTH "B7H4mEy5wa6usNYlkrR07WAeKJR5f3qNW8vAvRDl" 

 

FirebaseData firebaseData; 

FirebaseConfig firebaseConfig; 

FirebaseAuth firebaseAuth; 

 

SoftwareSerial esp8266Serial(D2, D3); // RX, TX - Arduino ile iletişim için 

 

// Arduino ya veri gönderme 

void sendUserInfoToArduino(const String& kullaniciAd) { 

  esp8266Serial.println(kullaniciAd); 

  Serial.println("Arduino'ya gönderilen kullanıcı adı: " + kullaniciAd); 

} 

// ESP ID sine göre kullanıcıları bulma 

void findUsersByESP_ID(const String& device_id) { 

  if (Firebase.getJSON(firebaseData, "/users")) { 

    Serial.println("Veriler alındı."); 

 

// verileri Josn a çevirir  

    String jsonStr = firebaseData.jsonString(); 

    Serial.println("JSON verisi:"); 

    Serial.println(jsonStr); 

 

    DynamicJsonDocument doc(2048); 

    DeserializationError error = deserializeJson(doc, jsonStr); 

    if (error) { 

      Serial.print("JSON ayrıştırma hatası: "); 

      Serial.println(error.c_str()); 

      return; 

    } 

  

    // ID ye göre veri bulma  

    JsonObject users = doc.as<JsonObject>(); 

    for (JsonPair kv : users) { 

      JsonObject user = kv.value().as<JsonObject>(); 

      if (user["esp_ID"] == device_id) { 

        String kullaniciAd = user["kullaniciAd"].as<String>(); 

        sendUserInfoToArduino(kullaniciAd); 

      } 

    } 

  } else { 

    Serial.print("Firebase okuma hatası: "); 

    Serial.println(firebaseData.errorReason()); 

  } 

} 

// Uno dan gelen veriyi güncelleme işlemi 

void updateWaterConsumption(const String& kullaniciAd, float suMiktari) { 

  if (Firebase.getJSON(firebaseData, "/users")) { 

    Serial.println("Veriler alındı."); 

 

    String jsonStr = firebaseData.jsonString(); 

    Serial.println("JSON verisi:"); 

    Serial.println(jsonStr); 

 

    DynamicJsonDocument doc(4096); 

    DeserializationError error = deserializeJson(doc, jsonStr); 

    if (error) { 

      Serial.print("JSON ayrıştırma hatası: "); 

      Serial.println(error.c_str()); 

      return; 

    } 

 

    JsonObject users = doc.as<JsonObject>(); 

    bool userFound = false; 

 

    for (JsonPair kv : users) { 

      JsonObject user = kv.value().as<JsonObject>(); 

      if (user["kullaniciAd"] == kullaniciAd) { 

        String userId = kv.key().c_str(); 

        float currentWater = user["suMiktari"]; 

        float updatedWater = currentWater + suMiktari; // yeni su miktarını ekliyor 

 

        String path = "/users/" + userId + "/suMiktari"; 

        if (Firebase.setFloat(firebaseData, path, updatedWater)) { 

          Serial.println("Su miktarı güncellendi."); 

        } else { 

          Serial.print("Su miktarı güncellenirken hata: "); 

          Serial.println(firebaseData.errorReason()); 

        } 

        userFound = true; 

        break; 

      } 

    } 

 

    if (!userFound) { 

      Serial.println("Kullanıcı bulunamadı."); 

    } 

  } else { 

    Serial.print("Firebase okuma hatası: "); 

    Serial.println(firebaseData.errorReason()); 

  } 

} 

void setup() { 

  Serial.begin(9600); 

  esp8266Serial.begin(9600); 

  Serial.println("WiFi'ye bağlanılıyor..."); 

  WiFi.begin(ssid, password); // Wİfi bağlanma 

  while (WiFi.status() != WL_CONNECTED) { 

    delay(1000); 

    Serial.println("WiFi bağlantısı bekleniyor..."); 

  } 

  Serial.println("WiFi'ye bağlanıldı."); 

  Serial.print("IP Adresi: "); 

  Serial.println(WiFi.localIP()); 

 

// firebase bağlanma 

  firebaseConfig.host = FIREBASE_HOST; 

  firebaseConfig.signer.tokens.legacy_token = FIREBASE_AUTH; 

 

  Firebase.begin(&firebaseConfig, &firebaseAuth); 

  Firebase.reconnectWiFi(true); 

 

  findUsersByESP_ID(DEVICE_ID); 

} 

 

void loop() { 

  if (esp8266Serial.available()) { 

    String receivedData = esp8266Serial.readStringUntil('\n'); 

    receivedData.trim(); 

    Serial.println("Gelen veri: " + receivedData); 

    // gelen veriyi ayrıştırıyor  

    int separatorIndex = receivedData.indexOf('-'); 

    if (separatorIndex != -1) { 

      String kullaniciAd = receivedData.substring(0, separatorIndex); 

      float suMiktari = receivedData.substring(separatorIndex + 1).toFloat(); 

      Serial.println("Kullanıcı adı: " + kullaniciAd); 

      Serial.println("Su miktarı: " + String(suMiktari)); 

      updateWaterConsumption(kullaniciAd, suMiktari); 

    } else { Serial.println("Geçersiz veri formatı."); 

    }} 
