package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Keanggotaan {
    private String nomorKeanggotaan;
    private String namaPelanggan;
    private LocalDate tanggalMember;
    private int jumlahTransaksiBeli;
    private int jumlahTransaksiPinjam;
    private int poinReward;

    public Keanggotaan(String nomorKeanggotaan, String namaPelanggan, LocalDate tanggalMember) {
        this.nomorKeanggotaan = nomorKeanggotaan;
        this.namaPelanggan = namaPelanggan;
        this.tanggalMember = tanggalMember;
        this.jumlahTransaksiBeli = 0;
        this.jumlahTransaksiPinjam = 0;
        this.poinReward = 0;
    }

    // Getters
    public String getNomorKeanggotaan() { return nomorKeanggotaan; }
    public String getNamaPelanggan() { return namaPelanggan; }
    public LocalDate getTanggalMember() { return tanggalMember; }
    public int getJumlahTransaksiBeli() { return jumlahTransaksiBeli; }
    public int getJumlahTransaksiPinjam() { return jumlahTransaksiPinjam; }
    public int getPoinReward() { return poinReward; }

    // Setters
    public void setNomorKeanggotaan(String nomorKeanggotaan) { this.nomorKeanggotaan = nomorKeanggotaan; }
    public void setNamaPelanggan(String namaPelanggan) { this.namaPelanggan = namaPelanggan; }
    public void setTanggalMember(LocalDate tanggalMember) { this.tanggalMember = tanggalMember; }
    
    // Methods untuk menambah transaksi dan poin
    public void tambahTransaksiBeli() {
        this.jumlahTransaksiBeli++;
        this.poinReward += 2; // Setiap pembelian mendapat 2 poin
        System.out.printf("Poin bertambah 2! Total poin Anda sekarang: %d\n", this.poinReward);
        double diskonBaru = hitungDiskon() * 100;
        System.out.printf("Selamat! Anda mendapatkan diskon %.1f%% untuk pembelian berikutnya\n", diskonBaru);
    }

    public void tambahTransaksiPinjam() {
        this.jumlahTransaksiPinjam++;
        this.poinReward += 2; // Setiap peminjaman mendapat 2 poin
        System.out.printf("Poin bertambah 2! Total poin Anda sekarang: %d\n", this.poinReward);
        double diskonBaru = hitungDiskon() * 100;
        System.out.printf("Selamat! Anda mendapatkan diskon %.1f%% untuk pembelian berikutnya\n", diskonBaru);
    }

    public double hitungDiskon() {
        if (poinReward >= 100) {
            return 0.15; // 15% diskon untuk poin >= 100
        } else if (poinReward >= 50) {
            return 0.10; // 10% diskon untuk poin 50-99
        } else if (poinReward >= 25) {
            return 0.07; // 7% diskon untuk poin 25-49
        } else if (poinReward >= 10) {
            return 0.05; // 5% diskon untuk poin 10-24
        }
        return 0.0; // tidak ada diskon untuk poin < 10
    }

    public void tampilkanInfoKeanggotaan() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("Nomor Keanggotaan: " + nomorKeanggotaan);
        System.out.println("Nama Pelanggan: " + namaPelanggan);
        System.out.println("Tanggal Menjadi Member: " + tanggalMember.format(formatter));
        System.out.println("Jumlah Transaksi Beli: " + jumlahTransaksiBeli);
        System.out.println("Jumlah Transaksi Pinjam: " + jumlahTransaksiPinjam);
        System.out.println("Poin Reward: " + poinReward);
        System.out.printf("Diskon: %.1f%%\n", hitungDiskon() * 100);
        
        // Tampilkan informasi poin yang dibutuhkan untuk diskon berikutnya
        if (poinReward < 10) {
            System.out.println("Butuh " + (10 - poinReward) + " poin lagi untuk mendapatkan diskon 5%");
        } else if (poinReward < 25) {
            System.out.println("Butuh " + (25 - poinReward) + " poin lagi untuk mendapatkan diskon 7%");
        } else if (poinReward < 50) {
            System.out.println("Butuh " + (50 - poinReward) + " poin lagi untuk mendapatkan diskon 10%");
        } else if (poinReward < 100) {
            System.out.println("Butuh " + (100 - poinReward) + " poin lagi untuk mendapatkan diskon 15%");
        }
    }

    public void resetPoin() {
        this.poinReward = 0;
    }
}
