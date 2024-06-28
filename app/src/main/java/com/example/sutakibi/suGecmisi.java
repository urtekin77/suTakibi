package com.example.sutakibi;

public class suGecmisi {
    private String tarih;
    private float miktar;
    public suGecmisi(){
        //boş yapıcı
    }
    public suGecmisi(String tarih, float miktar) {
        this.tarih = tarih;
        this.miktar = miktar;
    }
    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public float getMiktar() {
        return miktar;
    }

    public void setMiktar(float miktar) {
        this.miktar = miktar;
    }
}
