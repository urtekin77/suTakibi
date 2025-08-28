# 💧 Akıllı Su Tüketim Sistemi

Bu proje, kullanıcıların günlük su tüketimlerini izlemelerini, yönetmelerini ve optimize etmelerini sağlayarak sağlıklı su içme alışkanlıkları kazanmalarına yardımcı olan **Arduino tabanlı bir IoT sistemidir.**

---

## 📋 Proje Hakkında
**Akıllı Su Tüketim Sistemi**, iki ana bileşenden oluşmaktadır:

- **Donanım**: Arduino UNO tabanlı su dağıtım sistemi  
- **Mobil Uygulama**: Android uygulama ile veri takibi ve yönetimi  

Sistem, **Firebase Realtime Database** kullanarak gerçek zamanlı veri senkronizasyonu sağlar ve kullanıcılara kişiselleştirilmiş su tüketim hedefleri sunar.

---

## 🎯 Özellikler

### 🔌 Donanım Özellikleri
- **Otomatik Su Dağıtımı**: Potansiyometre ile ayarlanabilen su miktarı (0.1L - 2.0L)  
- **Çok Kullanıcı Desteği**: Butonlar ile kullanıcı değiştirme  
- **LCD Görüntüleme**: 16x2 LCD ekran ile kullanıcı bilgileri gösterimi  
- **WiFi Bağlantısı**: ESP8266 modülü ile internet bağlantısı  
- **Gerçek Zamanlı Veri İletimi**: Firebase ile anlık veri senkronizasyonu  

### 📱 Mobil Uygulama Özellikleri
- Kullanıcı Yönetimi (ekleme, güncelleme, silme)  
- Kişiselleştirilmiş Su Hesaplama (yaş, kilo, boy, aktivite seviyesi vb.)  
- Günlük su tüketim hedefine ulaşım yüzdesi  
- Geçmiş veriler ile **grafik raporlar**  
- Saat başı **hatırlatma bildirimleri**  
- Manuel su ekleme desteği  

---

## 🛠️ Kullanılan Teknolojiler

### Donanım Teknolojileri

| Bileşen          | Teknoloji/Ürün               | Açıklama                   |
|------------------|------------------------------|----------------------------|
| Mikrodenetleyici | Arduino UNO R3              | Ana kontrol ünitesi        |
| WiFi Modülü      | ESP8266 NodeMCU V3          | İnternet bağlantısı        |
| Motor Sürücü     | L298N                        | Su pompası kontrolü        |
| Su Pompası       | R385 DC6-12V                 | Su dağıtımı                |
| Görüntüleme      | 16x2 LCD + I2C               | Kullanıcı arayüzü          |
| Veritabanı       | Firebase Realtime Database   | Bulut veri depolama        |
| Mobil Geliştirme | Android Studio (Java)        | Mobil uygulama             |
| Sensör/Giriş     | Potansiyometre, Butonlar     | Kullanıcı etkileşimi       |



### Mobil Uygulama Teknolojileri


## 📱 Mobil Uygulama Teknolojileri

| Katman              | Teknoloji/Ürün              | Açıklama                                 |
|---------------------|-----------------------------|------------------------------------------|
| IDE & Geliştirme    | Android Studio              | Uygulama geliştirme ortamı               |
| Programlama Dili    | Java                        | Android uygulama geliştirme              |
| UI Tasarımı         | XML Layouts                 | Kullanıcı arayüzü tasarımı               |
| Veri Tabanı         | Firebase Realtime Database  | Gerçek zamanlı senkronizasyon            |
| Kimlik Doğrulama    | Firebase Authentication     | Kullanıcı hesap yönetimi (opsiyonel)     |
| API / Servis        | Firebase Cloud Functions    | Arka plan işlemleri                      |
| Bildirimler         | Firebase Cloud Messaging    | Su içme hatırlatmaları                   |
| Grafik/Raporlama    | HelloCharts                 | Su tüketim istatistiklerinin grafikleri  |
| JSON İşleme         | Gson / org.json             | Veri serileştirme ve ayrıştırma          |


---
## 🔌 Donanım Bağlantı Şeması

<div align="center">
  <img src="https://github.com/urtekin77/suTakibi/blob/master/images/WhatsApp Image 2025-08-28 at 20.53.34.jpeg" alt="Donanım Bağlantı Şeması" width="150"/>

</div>
---

## 📊 Proje Blok Diyagramı
<div align="center">
  <img src="https://github.com/urtekin77/suTakibi/blob/master/images/WhatsApp Image 2025-08-28 at 20.53.33.jpeg" alt="Proje Blok Diyagramı" width="150"/>

</div>
---

---

## 🖼️ Görseller


- Mobil Uygulama Ana Ekran:  

<div align="center">
<img src="https://github.com/urtekin77/suTakibi/blob/master/images/girisEkrani.png" alt="Giriş Ekranı" width="150"/>
  <img src="https://github.com/urtekin77/suTakibi/blob/master/images/anaSAyfa.png" alt="Ana Sayfa Ekranı" width="150"/>
  <img src="https://github.com/urtekin77/suTakibi/blob/master/images/rapor1.png" alt="Kullanıcı rapor ekranı" width="150"/>
    <img src="https://github.com/urtekin77/suTakibi/blob/master/images/bildirim.png" alt="Bildirim" width="150"/>
</div>

- Projenin Denemesi:  
<div align="center">
  <img src="https://github.com/urtekin77/suTakibi/blob/master/images/bağlantı1.jpeg" alt="Proje Blok Diyagramı" width="150"/>

</div>

---



## 🚀 Kurulum

### 1. Donanım Kurulumu
- Arduino IDE’yi indirin  
- Gerekli kütüphaneleri yükleyin:
  ```
  - Wire.h (dahili)
  - LiquidCrystal_I2C.h
  - SoftwareSerial.h (dahili)
  ```

**ESP8266 Kurulumu**
- ESP8266 board tanımlarını ekleyin  
- Gerekli kütüphaneleri yükleyin:
  ```
  - ESP8266WiFi.h
  - FirebaseESP8266.h
  - ArduinoJson.h
  - SoftwareSerial.h
  ```
- WiFi & Firebase bilgilerini girin:  
  ```cpp
  const char* ssid = "WiFi_AĞINIZ";
  const char* password = "WiFi_ŞIFRENIZ";
  #define FIREBASE_HOST "firebase-proje-url.firebaseio.com"
  #define FIREBASE_AUTH "firebase-auth-anahtarınız"
  ```

---

### 2. Firebase Kurulumu
- Firebase Console’da yeni proje oluşturun  
- Realtime Database’i etkinleştirin  
- Kuralları aşağıdaki gibi ayarlayın:
  ```json
  {
    "rules": {
      ".read": true,
      ".write": true
    }
  }
  ```

---

### 3. Android Uygulama Kurulumu
- Android Studio’da `mobile-app/android/` klasörünü açın  
- `google-services.json` dosyasını `app/` klasörüne ekleyin  
- Gradle Sync yapın ve uygulamayı yükleyin  

---

## 📱 Kullanım

**Donanım:**
1. Sistemi açın → LCD ekranda "MERHABA" yazısını bekleyin  
2. ESP8266’nın WiFi’ye bağlanmasını bekleyin  
3. Kullanıcı butonlarıyla kullanıcı seçin  
4. Potansiyometre ile su miktarını ayarlayın  
5. Motor butonuna basarak su alımını başlatın  

**Mobil Uygulama:**
- ESP_ID’nizi girin  
- Kullanıcı bilgilerinizi ekleyin/güncelleyin  
- Günlük hedefinizi takip edin  
- Raporlar bölümünden geçmiş verilerinizi inceleyin  

---

## 📊 Su İhtiyacı Hesaplama
- **Temel**: Kilo × 0.033 L  
- **Aktivite**: Yüksek (0.7L), Orta (0.5L), Düşük (0.3L)  
- **İklim**: Sıcak (0.5L), Ilıman (0.2L), Soğuk (0L)  
- **Sağlık**: Hamile (0.3L), Emzirme (0.5L), Hastalık (0.4L)  
- **Yaş**: <30 (0.3L), 30–55 (0.4L), >55 (0.3L)  

---

## 🐛 Sorun Giderme

- **ESP8266 WiFi’ye bağlanmıyor** → WiFi bilgilerini ve sinyal gücünü kontrol edin  
- **Firebase’e veri gitmiyor** → API anahtarını ve kuralları kontrol edin  
- **Motor çalışmıyor** → Güç bağlantısı, motor sürücü ve adaptörü kontrol edin  

---

## 🤝 Katkıda Bulunma
1. Projeyi fork edin  
2. Yeni dal oluşturun → `git checkout -b ozellik/YeniOzellik`  
3. Commit atın → `git commit -m 'Yeni özellik eklendi'`  
4. Push yapın → `git push origin ozellik/YeniOzellik`  
5. Pull Request gönderin  

---


## 👥 Geliştirici
**Eda Nur Urtekin**  

---
