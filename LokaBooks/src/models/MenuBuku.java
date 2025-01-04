package models;

public abstract class MenuBuku {
    private String judul;
    private String penulis;
    private double harga;
    private String genre;
    private boolean dipinjam;
    private String tanggalPinjam;
    private String tanggalKembali;

    public MenuBuku(String judul, String penulis, double harga, String genre) {
        this.judul = judul;
        this.penulis = penulis;
        this.harga = harga;
        this.genre = genre;
        this.dipinjam = false;
    }

    // Getters and setters
    public String getJudul() {
        return judul;
    }

    public String getPenulis() {
        return penulis;
    }

    public double getHarga() {
        return harga;
    }

    public String getGenre() {
        return genre;
    }

    public boolean isDipinjam() {
        return dipinjam;
    }

    public void setDipinjam(boolean dipinjam) {
        this.dipinjam = dipinjam;
    }

    public String getTanggalPinjam() {
        return tanggalPinjam;
    }

    public void setTanggalPinjam(String tanggalPinjam) {
        this.tanggalPinjam = tanggalPinjam;
    }

    public String getTanggalKembali() {
        return tanggalKembali;
    }

    public void setTanggalKembali(String tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }

    public abstract void display();
}