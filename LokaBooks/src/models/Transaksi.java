package models;

// Transaksi.java
public class Transaksi {
    private String deskripsi;
    private double nominal;
    private String tanggal;
    private String minggu;
    private String bulan;
    private String jenis; // "1" untuk harian, "2" untuk mingguan, "3" untuk bulanan

    // Konstruktor dengan parameter sesuai kebutuhan
    public Transaksi(String deskripsi, double nominal, String tanggal, String minggu, String bulan, String jenis) {
        this.deskripsi = deskripsi;
        this.nominal = nominal;
        this.tanggal = tanggal;
        this.minggu = minggu;
        this.bulan = bulan;
        this.jenis = jenis;
    }

    // Getter dan setter
    public String getDeskripsi() {
        return deskripsi;
    }

    public double getNominal() {
        return nominal;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getMinggu() {
        return minggu;
    }

    public String getBulan() {
        return bulan;
    }

    public String getJenis() {
        return jenis;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public void setMinggu(String minggu) {
        this.minggu = minggu;
    }

    public void setBulan(String bulan) {
        this.bulan = bulan;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    // Method untuk menampilkan informasi transaksi
    @Override
    public String toString() {
        return String.format("Transaksi{deskripsi='%s', nominal=%.2f, tanggal='%s', minggu='%s', bulan='%s', jenis='%s'}",
                deskripsi, nominal, tanggal, minggu, bulan, jenis);
    }
}
