package models;

public class BukuDigital extends MenuBuku {
    private double ukuranFile;
    private String formatFile;

    public BukuDigital(String judul, String penulis, double harga, String genre, double ukuranFile, String formatFile) {
        super(judul, penulis, harga, genre);
        this.ukuranFile = ukuranFile;
        this.formatFile = formatFile;
    }

    public double getUkuranFile() {
        return ukuranFile;
    }

    public String getFormatFile() {
        return formatFile;
    }

    @Override
    public void display() {
        System.out.println("Buku Digital - Judul: " + getJudul() + 
                         ", Penulis: " + getPenulis() + 
                         ", Ukuran File: " + ukuranFile + 
                         "MB, Format: " + formatFile);
    }
}
