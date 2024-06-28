package com.example.sutakibi;

import java.util.Map;

public class users {
    private String ESP_ID;
    private int age;
    private String aktivite;
    private String user_ID;
    private float boy;
    private String cevresel;
    private String cinsiyet;
    private float gerekliSuMiktari;

    private float kilo;
    private String kullaniciAd;
    private String saglik;
    private float suMiktari;
    private Map<String, Float> suGecmis;
    public users(){
        // boş yapıcı
    }
    public users(String user_ID ,String ESP_ID, int age, String aktivite, float boy, String cevresel, String cinsiyet, float gerekliSuMiktari,
                  float kilo, String kullaniciAd, String saglik, float suMiktari, Map<String, Float> suGecmis){
        this.user_ID = user_ID;
        this.age = age;
        this.ESP_ID = ESP_ID;
        this.aktivite = aktivite;
        this.boy = boy;
        this.cevresel = cevresel;
        this.cinsiyet = cinsiyet;
        this.gerekliSuMiktari = gerekliSuMiktari;
        this.kilo = kilo;
        this.kullaniciAd = kullaniciAd;
        this.saglik = saglik;
        this.suMiktari = suMiktari;
        this.suGecmis = suGecmis;
    }
    public Map<String, Float> getSuGecmis() {
        return suGecmis;
    }

    public void setSuGecmis(Map<String, Float> suGecmis) {
        this.suGecmis = suGecmis;
    }
    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public float getBoy() {
        return boy;
    }

    public float getGerekliSuMiktari() {
        return gerekliSuMiktari;
    }


    public float getKilo() {
        return kilo;
    }

    public float getSuMiktari() {
        return suMiktari;
    }

    public int getAge() {
        return age;
    }

    public String getAktivite() {
        return aktivite;
    }

    public String getCevresel() {
        return cevresel;
    }

    public String getCinsiyet() {
        return cinsiyet;
    }

    public String getESP_ID() {
        return ESP_ID;
    }

    public String getKullaniciAd() {
        return kullaniciAd;
    }

    public String getSaglik() {
        return saglik;
    }

    public void setGerekliSuMiktari(float gerekliSuMiktari) {
        this.gerekliSuMiktari = gerekliSuMiktari;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAktivite(String aktivite) {
        this.aktivite = aktivite;
    }

    public void setBoy(float boy) {
        this.boy = boy;
    }

    public void setCevresel(String cevresel) {
        this.cevresel = cevresel;
    }

    public void setCinsiyet(String cinsiyet) {
        this.cinsiyet = cinsiyet;
    }

    public void setESP_ID(String ESP_ID) {
        this.ESP_ID = ESP_ID;
    }


    public void setKilo(float kilo) {
        this.kilo = kilo;
    }

    public void setKullaniciAd(String kullaniciAd) {
        this.kullaniciAd = kullaniciAd;
    }

    public void setSaglik(String saglik) {
        this.saglik = saglik;
    }

    public void setSuMiktari(float suMiktari) {
        this.suMiktari = suMiktari;
    }
}
