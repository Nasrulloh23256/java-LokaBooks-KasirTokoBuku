package models;

public class BukuFisik extends MenuBuku {
    private int stok;

    public BukuFisik(String judul, String penulis, double harga, String genre, int stok) {
        super(judul, penulis, harga, genre);
        this.stok = stok;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    @Override
    public void display() {
        System.out.println("Buku Fisik: " + getJudul() + 
                         ", Penulis: " + getPenulis() + 
                         ", Genre: " + getGenre() + 
                         ", Harga: Rp " + getHarga() + 
                         ", Stok: " + stok);
    }
} 