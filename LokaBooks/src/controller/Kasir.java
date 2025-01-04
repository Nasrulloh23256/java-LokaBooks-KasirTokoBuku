package controller;

import models.MenuBuku;
import Payments.Pembayaran;
import Payments.PembayaranKartu;
import Payments.PembayaranTunai;
import Payments.PembayaranTransfer;
import Payments.PembayaranPinjam;
import models.BukuDigital;
import models.BukuFisik;
import models.Keanggotaan;
import services.PencarianStrategy;
import services.PencarianJudul;
import services.PencarianPenulis;
import services.PencarianGenre;
import services.LaporanService;
import utils.TerminalUtils;

import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;

@SuppressWarnings("deprecation")
public class Kasir {
    private ArrayList<MenuBuku> bukuList;
    private List<MenuBuku> bukuDibeli;
    private double totalHarga;
    private List<PencarianStrategy> strategiPencarian;
    private LaporanService laporanService;
    private ArrayList<Keanggotaan> keanggotaanList;

    public Kasir(ArrayList<MenuBuku> bukuList, ArrayList<Keanggotaan> keanggotaanList) {
        this.bukuList = bukuList;
        this.bukuDibeli = new ArrayList<>();
        this.totalHarga = 0.0;
        this.laporanService = LaporanService.getInstance();
        this.keanggotaanList = keanggotaanList;
        
        this.strategiPencarian = new ArrayList<>();
        this.strategiPencarian.add(new PencarianJudul());
        this.strategiPencarian.add(new PencarianPenulis());
        this.strategiPencarian.add(new PencarianGenre());
    }

    public void tampilkanMenuPembayaran() {
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n\tMENU PEMBAYARAN");
            System.out.println("1. Beli Buku");
            System.out.println("2. Pinjam Buku");
            System.out.println("3. Kembalikan Buku");
            System.out.println("4. Cari Buku");
            System.out.println("0. Kembali ke Menu Utama");
            System.out.print("Pilih jenis transaksi: ");
            int pilihanTransaksi = scanner.nextInt();
            scanner.nextLine();

            if (pilihanTransaksi == 0) {
                isRunning = false;
                continue;
            }

            TerminalUtils.clearScreen();
            switch (pilihanTransaksi) {
                case 1:
                    beliBuku(scanner);
                    break;
                case 2:
                    pinjamBuku(scanner);
                    break;
                case 3:
                    kembalikanBuku(scanner);
                    break;
                case 4:
                    cariBukuMenu(scanner);
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
            System.out.println("\nTekan Enter untuk kembali ke menu...");
            scanner.nextLine();
            TerminalUtils.clearScreen();
        }
    }

    private void kembalikanBuku(Scanner scanner) {
        TerminalUtils.clearScreen();
        System.out.print("Masukkan judul buku yang ingin dikembalikan: ");
        String judul = scanner.nextLine();

        MenuBuku buku = cariBuku(judul);
        if (buku != null && buku.isDipinjam()) {
            // Inputkan tanggal pengembalian
            System.out.print("Masukkan tanggal pengembalian (dd/MM/yyyy): ");
            String tanggalKembaliInput = scanner.nextLine();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate tanggalKembaliDate = LocalDate.parse(tanggalKembaliInput, formatter);
            LocalDate tanggalPinjamDate = LocalDate.parse(buku.getTanggalPinjam(), formatter);
            LocalDate tanggalKembaliAsli = LocalDate.parse(buku.getTanggalKembali(), formatter);

            long hariTerlambat = ChronoUnit.DAYS.between(tanggalKembaliAsli, tanggalKembaliDate);
            double denda = 0.0;

            if (hariTerlambat > 0) {
                denda = hariTerlambat * 1000; // Misalkan denda Rp 1000 per hari
                System.out.println(
                        "Anda terlambat mengembalikan buku selama " + hariTerlambat + " hari. Denda: Rp " + denda);

                // Tawarkan opsi pembayaran denda
                System.out.println("Apakah Anda ingin membayar denda? (ya/tidak)");
                String jawab = scanner.nextLine();
                if (jawab.equalsIgnoreCase("ya")) {
                    pembayaranDenda(scanner, denda);
                }
            } else {
                System.out.println("Pengembalian buku tepat waktu. Tidak ada denda.");
            }

            // Tambahkan stok kembali jika buku fisik
            if (buku instanceof BukuFisik) {
                BukuFisik bukuFisik = (BukuFisik) buku;
                bukuFisik.setStok(bukuFisik.getStok() + 1);
            }

            buku.setDipinjam(false);
            buku.setTanggalPinjam(null);
            buku.setTanggalKembali(null);
            System.out.println("Buku " + judul + " berhasil dikembalikan.");
        } else {
            System.out.println("Buku tidak ditemukan atau tidak sedang dipinjam.");
        }
    }

    private void pembayaranDenda(Scanner scanner, double denda) {
        System.out.println("\nMetode pembayaran denda:");
        System.out.println("1. Tunai");
        System.out.println("2. Kartu Kredit/Debit");
        System.out.println("3. Transfer Bank");
        System.out.print("Pilih metode pembayaran: ");
        int pilihanPembayaran = scanner.nextInt();
        scanner.nextLine();

        Pembayaran pembayaran = null;
        if (pilihanPembayaran == 1) {
            pembayaran = new PembayaranTunai(denda);
        } else if (pilihanPembayaran == 2) {
            System.out.print("Masukkan nomor kartu: ");
            String nomorKartu = scanner.nextLine();
            System.out.print("Masukkan nama bank: ");
            String namaBank = scanner.nextLine();
            pembayaran = new PembayaranKartu(denda, nomorKartu, namaBank);
        } else if (pilihanPembayaran == 3) {
            System.out.print("Masukkan nomor rekening tujuan: ");
            String nomorRekening = scanner.nextLine();
            System.out.print("Masukkan nama bank: ");
            String namaBank = scanner.nextLine();
            pembayaran = new PembayaranTransfer(denda, nomorRekening, namaBank);
        }

        if (pembayaran != null) {
            pembayaran.bayar();
            System.out.println("Pembayaran denda sebesar Rp " + denda + " berhasil.");
        }
    }

    private void beliBuku(Scanner scanner) {
        TerminalUtils.clearScreen();
        bukuDibeli.clear();
        totalHarga = 0.0;

        System.out.println("\nPilih jenis buku yang ingin dibeli:");
        System.out.println("1. Buku Digital");
        System.out.println("2. Buku Fisik");
        System.out.print("Pilih jenis buku: ");
        int jenisBuku = scanner.nextInt();
        scanner.nextLine();

        boolean isBukuDigital = (jenisBuku == 1);

        System.out.print("Masukkan jumlah buku yang ingin dibeli: ");
        int jumlahBuku = scanner.nextInt();
        scanner.nextLine();

        for (int i = 1; i <= jumlahBuku; i++) {
            boolean bukuDitemukan = false;
            MenuBuku bukuPilihan = null;

            while (!bukuDitemukan) {
                System.out.print("Masukkan judul buku " + (isBukuDigital ? "digital" : "fisik") + " ke-" + i + ": ");
                String judulBuku = scanner.nextLine();

                for (MenuBuku buku : bukuList) {
                    if (buku.getJudul().equalsIgnoreCase(judulBuku)) {
                        if ((isBukuDigital && buku instanceof BukuDigital) || 
                            (!isBukuDigital && buku instanceof BukuFisik)) {
                            if (!isBukuDigital) {
                                BukuFisik bukuFisik = (BukuFisik) buku;
                                if (bukuFisik.getStok() <= 0) {
                                    System.out.println("Maaf, stok buku " + judulBuku + " sedang kosong.");
                                    break;
                                }
                            }
                            bukuPilihan = buku;
                            bukuDitemukan = true;
                            break;
                        }
                    }
                }

                if (!bukuDitemukan) {
                    System.out.println("Buku dengan judul '" + judulBuku + "' tidak ditemukan atau tidak tersedia.");
                    System.out.println("Silakan pilih dari daftar buku yang tersedia di atas.");
                    // Tampilkan daftar buku yang tersedia sesuai jenis
                    System.out.println("\nDaftar buku " + (isBukuDigital ? "digital" : "fisik") + " yang tersedia:");
                    for (MenuBuku buku : bukuList) {
                        if ((isBukuDigital && buku instanceof BukuDigital) || 
                            (!isBukuDigital && buku instanceof BukuFisik)) {
                            if (isBukuDigital || (buku instanceof BukuFisik && ((BukuFisik) buku).getStok() > 0)) {
                                System.out.println("- " + buku.getJudul() + 
                                    (!isBukuDigital ? " (Stok: " + ((BukuFisik) buku).getStok() + ")" : ""));
                            }
                        }
                    }
                }
            }

            if (bukuPilihan != null) {
                bukuDibeli.add(bukuPilihan);
                totalHarga += bukuPilihan.getHarga();
                // Kurangi stok hanya untuk buku fisik
                if (!isBukuDigital && bukuPilihan instanceof BukuFisik) {
                    BukuFisik bukuFisik = (BukuFisik) bukuPilihan;
                    bukuFisik.setStok(bukuFisik.getStok() - 1);
                }
            }
        }

        if (!bukuDibeli.isEmpty()) {
            System.out.println("\nRingkasan Pembelian:");
            for (MenuBuku buku : bukuDibeli) {
                System.out.printf("- %s (Rp %.2f)%n", buku.getJudul(), buku.getHarga());
            }
            System.out.printf("Total Harga: Rp %.2f%n", totalHarga);

            // Cek membership sebelum pembayaran
            Keanggotaan member = cekMembership(scanner);
            if (member != null) {
                // Tampilkan opsi penukaran poin
                if (member.getPoinReward() >= 10) {  // Minimal 10 poin untuk bisa ditukar
                    double diskonPoin = member.hitungDiskon();
                    double hargaSetelahDiskonPoin = totalHarga * (1 - diskonPoin);
                    System.out.printf("\nAnda memiliki %d poin\n", member.getPoinReward());
                    System.out.printf("Tukarkan poin sekarang dan dapatkan diskon %.1f%%?\n", diskonPoin * 100);
                    System.out.printf("Pembelian Anda akan menjadi Rp %.2f\n", hargaSetelahDiskonPoin);
                    System.out.print("Tukarkan poin? (y/n): ");
                    String jawaban = scanner.nextLine();

                    if (jawaban.equalsIgnoreCase("y")) {
                        totalHarga = hargaSetelahDiskonPoin;
                        System.out.printf("Poin berhasil ditukarkan! Total harga menjadi Rp %.2f\n", totalHarga);
                        member.resetPoin(); // Reset poin menjadi 0
                        System.out.println("Poin Anda sekarang: 0");
                    } else {
                        // Jika tidak ditukar, hanya tambahkan poin baru tanpa diskon
                        member.tambahTransaksiBeli();
                    }
                } else {
                    // Jika poin kurang dari 10, langsung tambahkan poin baru tanpa diskon
                    member.tambahTransaksiBeli();
                    if (member.getPoinReward() >= 10) {
                        System.out.printf("Sekarang Anda memiliki %d poin dan bisa mendapatkan diskon di pembelian berikutnya!\n", 
                            member.getPoinReward());
                    }
                }
            }
            
            if (isBukuDigital) {
                pembayaranBukuDigital(scanner, totalHarga);
            } else {
                pembayaranBukuFisik(scanner, totalHarga);
            }
        }
    }

    private void pinjamBuku(Scanner scanner) {
        TerminalUtils.clearScreen();
        System.out.print("Masukkan judul buku yang ingin dipinjam: ");
        String judul = scanner.nextLine();

        MenuBuku buku = cariBuku(judul);
        if (buku != null && !buku.isDipinjam()) {
            // Cek membership
            Keanggotaan member = cekMembership(scanner);
            if (member == null) {
                System.out.println("Maaf, peminjaman buku hanya untuk member.");
                return;
            }

            if (buku instanceof BukuFisik) {
                BukuFisik bukuFisik = (BukuFisik) buku;
                if (bukuFisik.getStok() <= 0) {
                    System.out.println("Maaf, stok buku " + judul + " sedang kosong.");
                    return;
                }
                bukuFisik.setStok(bukuFisik.getStok() - 1);
            }

            System.out.print("Masukkan tanggal pinjam (dd/mm/yyyy): ");
            String tanggalPinjam = scanner.nextLine();
            System.out.print("Masukkan tanggal kembali (dd/mm/yyyy): ");
            String tanggalKembali = scanner.nextLine();

            buku.setDipinjam(true);
            buku.setTanggalPinjam(tanggalPinjam);
            buku.setTanggalKembali(tanggalKembali);

            // Tambah transaksi pinjam dan poin
            member.tambahTransaksiPinjam();
            System.out.println("Buku " + judul + " berhasil dipinjam.");
        } else {
            System.out.println("Buku tidak tersedia atau sudah dipinjam.");
        }
    }

    private MenuBuku cariBuku(String judul) {
        for (MenuBuku buku : bukuList) {
            if (buku.getJudul().equalsIgnoreCase(judul)) {
                return buku;
            }
        }
        return null;
    }

    private void pembayaranBuku(Scanner scanner, double harga) {
        System.out.println("\nMetode pembayaran:");
        System.out.println("1. Tunai");
        System.out.println("2. Kartu Kredit/Debit");
        System.out.println("3. Transfer Bank");
        System.out.print("Pilih metode pembayaran: ");
        int pilihanPembayaran = scanner.nextInt();
        scanner.nextLine();

        Pembayaran pembayaran = null;
        String metodePembayaran = "";
        String detailPembayaran = "";

        if (pilihanPembayaran == 1) {
            pembayaran = new PembayaranTunai(harga);
            metodePembayaran = "Tunai";
        } else if (pilihanPembayaran == 2) {
            System.out.print("Masukkan nomor kartu: ");
            String nomorKartu = scanner.nextLine();
            System.out.print("Masukkan nama bank: ");
            String namaBank = scanner.nextLine();
            pembayaran = new PembayaranKartu(harga, nomorKartu, namaBank);
            metodePembayaran = "Kartu " + namaBank;
            detailPembayaran = "No. Kartu: " + nomorKartu;
        } else if (pilihanPembayaran == 3) {
            System.out.print("Masukkan nomor rekening tujuan: ");
            String nomorRekening = scanner.nextLine();
            System.out.print("Masukkan nama bank: ");
            String namaBank = scanner.nextLine();
            pembayaran = new PembayaranTransfer(harga, nomorRekening, namaBank);
            metodePembayaran = "Transfer " + namaBank;
            detailPembayaran = "No. Rekening: " + nomorRekening;
        }

        if (pembayaran != null) {
            pembayaran.bayar();

            // Tambahkan opsi pengiriman struk
            System.out.println("\nPilih metode pengiriman struk:");
            System.out.println("1. Email");
            System.out.println("2. Nomor WhatsApp");
            System.out.print("Pilih metode pengiriman: ");
            int pilihanPengiriman = scanner.nextInt();
            scanner.nextLine();

            String infoPengiriman = "";
            if (pilihanPengiriman == 1) {
                System.out.print("Masukkan alamat email: ");
                String email = scanner.nextLine();
                infoPengiriman = "Email: " + email;
            } else if (pilihanPengiriman == 2) {
                System.out.print("Masukkan nomor WhatsApp: ");
                String nomorWhatsApp = scanner.nextLine();
                infoPengiriman = "WhatsApp: " + nomorWhatsApp;
            }

            buatStrukPembelian(this.bukuDibeli, this.totalHarga, metodePembayaran, detailPembayaran, infoPengiriman);
        }
    }

    private void buatStrukPembelian(List<MenuBuku> bukuDibeli, double totalHarga, String metodePembayaran, String detailPembayaran, String infoPengiriman) {
        try {
            // Konstanta untuk format struk
            final int LEBAR_STRUK = 42;
            final int LEBAR_JUDUL = 25;
            final int LEBAR_HARGA = 12;
            final String GARIS = "=".repeat(LEBAR_STRUK);
            final String GARIS_PUTUS = "-".repeat(LEBAR_STRUK);

            // Buat nama file dengan timestamp
            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String namaFile = "struk/struk_" + timestamp + ".txt";

            // Pastikan direktori struk ada
            new File("struk").mkdirs();

            FileWriter writer = new FileWriter(namaFile);
            
            // Header struk
            writer.write(GARIS + "\n");
            writer.write(centerText("TOKO BUKU LITERA LOKA", LEBAR_STRUK) + "\n");
            writer.write(GARIS + "\n");
            
            // Tanggal dan waktu
            String tanggal = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            writer.write("Tanggal: " + tanggal + "\n");
            writer.write(GARIS_PUTUS + "\n\n");
            
            // Daftar buku yang dibeli
            writer.write("DETAIL PEMBELIAN:\n");
            for (MenuBuku buku : bukuDibeli) {
                String judul = truncateString(buku.getJudul(), LEBAR_JUDUL);
                String harga = String.format("Rp %," + LEBAR_HARGA + ".2f", buku.getHarga());
                writer.write(String.format("%-" + LEBAR_JUDUL + "s %s\n", judul, harga));
            }
            
            writer.write("\n" + GARIS_PUTUS + "\n");
            
            // Total dengan format yang sejajar
            String totalText = "Total Harga:";
            String totalHargaStr = String.format("Rp %," + LEBAR_HARGA + ".2f", totalHarga);
            int totalSpasi = LEBAR_STRUK - totalText.length() - totalHargaStr.length();
            writer.write(totalText + " ".repeat(totalSpasi) + totalHargaStr + "\n");
            
            writer.write(GARIS_PUTUS + "\n\n");
            
            // Informasi pembayaran
            writer.write("INFORMASI PEMBAYARAN:\n");
            writer.write(String.format("Metode Pembayaran: %s\n", metodePembayaran));
            if (!detailPembayaran.isEmpty()) {
                writer.write(detailPembayaran + "\n");
            }

            // Informasi pengiriman
            writer.write("Pengiriman Struk: " + infoPengiriman + "\n");
            
            // Footer
            writer.write("\n" + GARIS + "\n");
            writer.write(centerText("Terima Kasih", LEBAR_STRUK) + "\n");
            writer.write(centerText("Selamat membaca dan belajar!", LEBAR_STRUK) + "\n");
            writer.write(GARIS + "\n");
            
            writer.close();
            
            System.out.println("\nStruk telah dicetak: " + namaFile);
            
        } catch (IOException e) {
            System.out.println("Error saat membuat struk: " + e.getMessage());
        }
    }

    // Helper method untuk memotong string jika terlalu panjang
    private String truncateString(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }

    // Helper method untuk membuat teks center-aligned
    private String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int leftPad = (width - text.length()) / 2;
        int rightPad = width - text.length() - leftPad;
        return " ".repeat(leftPad) + text + " ".repeat(rightPad);
    }

    private void cariBukuMenu(Scanner scanner) {
        System.out.println("\nPENCARIAN BUKU");
        for (int i = 0; i < strategiPencarian.size(); i++) {
            System.out.printf("%d. Cari berdasarkan %s\n", i + 1, strategiPencarian.get(i).getNamaPencarian());
        }
        
        System.out.print("Pilih metode pencarian: ");
        int pilihan = scanner.nextInt();
        scanner.nextLine();

        if (pilihan < 1 || pilihan > strategiPencarian.size()) {
            System.out.println("Pilihan tidak valid!");
            return;
        }

        System.out.print("Masukkan kata kunci: ");
        String keyword = scanner.nextLine();
        
        PencarianStrategy strategi = strategiPencarian.get(pilihan - 1);
        List<MenuBuku> hasilPencarian = strategi.cari(bukuList, keyword);
        
        tampilkanHasilPencarian(hasilPencarian);

        if (!hasilPencarian.isEmpty()) {
            System.out.print("\nApakah Anda ingin membeli buku? (ya/tidak): ");
            String jawaban = scanner.nextLine();
            if (jawaban.equalsIgnoreCase("ya")) {
                TerminalUtils.clearScreen();
                beliBuku(scanner);
            }
        }
    }

    private void tampilkanHasilPencarian(List<MenuBuku> hasilPencarian) {
        // Konstanta untuk lebar kolom
        final int LEBAR_JUDUL = 30;
        final int LEBAR_PENULIS = 20;
        final int LEBAR_GENRE = 15;
        final int LEBAR_HARGA = 15;

        String formatGaris = "+" + "-".repeat(LEBAR_JUDUL + 2) + "+" + 
                           "-".repeat(LEBAR_PENULIS + 2) + "+" + 
                           "-".repeat(LEBAR_GENRE + 2) + "+" + 
                           "-".repeat(LEBAR_HARGA + 2) + "+";

        System.out.println("\nHasil Pencarian:");
        System.out.println(formatGaris);
        System.out.printf("| %-" + LEBAR_JUDUL + "s | %-" + LEBAR_PENULIS + "s | %-" + LEBAR_GENRE + "s | %-" + LEBAR_HARGA + "s |\n",
                "Judul", "Penulis", "Genre", "Harga");
        System.out.println(formatGaris);

        if (hasilPencarian.isEmpty()) {
            System.out.println("Tidak ada buku yang sesuai dengan kata kunci yang dimasukkan");
        } else {
            for (MenuBuku buku : hasilPencarian) {
                System.out.printf("| %-" + LEBAR_JUDUL + "s | %-" + LEBAR_PENULIS + "s | %-" + LEBAR_GENRE + "s | Rp %-" + (LEBAR_HARGA-3) + ".2f |\n",
                    truncateString(buku.getJudul(), LEBAR_JUDUL),
                    truncateString(buku.getPenulis(), LEBAR_PENULIS),
                    truncateString(buku.getGenre(), LEBAR_GENRE),
                    buku.getHarga());
            }
        }
        System.out.println(formatGaris);
    }

    private void pembayaranBukuDigital(Scanner scanner, double harga) {
        System.out.println("\nMetode pembayaran untuk Buku Digital:");
        System.out.println("1. Kartu Kredit/Debit");
        System.out.println("2. Transfer Bank");
        System.out.print("Pilih metode pembayaran: ");
        int pilihanPembayaran = scanner.nextInt();
        scanner.nextLine();

        Pembayaran pembayaran = null;
        String metodePembayaran = "";
        String detailPembayaran = "";

        if (pilihanPembayaran == 1) {
            System.out.print("Masukkan nomor kartu: ");
            String nomorKartu = scanner.nextLine();
            System.out.print("Masukkan nama bank: ");
            String namaBank = scanner.nextLine();
            pembayaran = new PembayaranKartu(harga, nomorKartu, namaBank);
            metodePembayaran = "Kartu " + namaBank;
            detailPembayaran = "No. Kartu: " + nomorKartu;
        } else if (pilihanPembayaran == 2) {
            System.out.print("Masukkan nomor rekening tujuan: ");
            String nomorRekening = scanner.nextLine();
            System.out.print("Masukkan nama bank: ");
            String namaBank = scanner.nextLine();
            pembayaran = new PembayaranTransfer(harga, nomorRekening, namaBank);
            metodePembayaran = "Transfer " + namaBank;
            detailPembayaran = "No. Rekening: " + nomorRekening;
        }

        if (pembayaran != null) {
            pembayaran.bayar();

            // Tambahkan opsi pengiriman struk
            System.out.println("\nPilih metode pengiriman struk:");
            System.out.println("1. Email");
            System.out.println("2. Nomor WhatsApp");
            System.out.print("Pilih metode pengiriman: ");
            int pilihanPengiriman = scanner.nextInt();
            scanner.nextLine();

            String infoPengiriman = "";
            if (pilihanPengiriman == 1) {
                System.out.print("Masukkan alamat email: ");
                String email = scanner.nextLine();
                infoPengiriman = "Email: " + email;
            } else if (pilihanPengiriman == 2) {
                System.out.print("Masukkan nomor WhatsApp: ");
                String nomorWhatsApp = scanner.nextLine();
                infoPengiriman = "WhatsApp: " + nomorWhatsApp;
            }

            buatStrukPembelian(bukuDibeli, totalHarga, metodePembayaran, detailPembayaran, infoPengiriman);

            // Tambahkan transaksi ke laporan
            StringBuilder deskripsi = new StringBuilder("Pembelian Buku Digital:\n");
            for (MenuBuku buku : bukuDibeli) {
                deskripsi.append("- ").append(buku.getJudul()).append("\n");
            }
            deskripsi.append("Pembayaran: ").append(metodePembayaran);
            
            laporanService.tambahLaporanHarian(deskripsi.toString(), totalHarga);
        }
    }

    private void pembayaranBukuFisik(Scanner scanner, double harga) {
        System.out.println("\nMetode pembayaran untuk Buku Fisik:");
        System.out.println("1. Tunai");
        System.out.println("2. Kartu Kredit/Debit");
        System.out.println("3. Transfer Bank");
        System.out.print("Pilih metode pembayaran: ");
        int pilihanPembayaran = scanner.nextInt();
        scanner.nextLine();

        Pembayaran pembayaran = null;
        String metodePembayaran = "";
        String detailPembayaran = "";

        if (pilihanPembayaran == 1) {
            pembayaran = new PembayaranTunai(harga);
            metodePembayaran = "Tunai";
        } else if (pilihanPembayaran == 2) {
            System.out.print("Masukkan nomor kartu: ");
            String nomorKartu = scanner.nextLine();
            System.out.print("Masukkan nama bank: ");
            String namaBank = scanner.nextLine();
            pembayaran = new PembayaranKartu(harga, nomorKartu, namaBank);
            metodePembayaran = "Kartu " + namaBank;
            detailPembayaran = "No. Kartu: " + nomorKartu;
        } else if (pilihanPembayaran == 3) {
            System.out.print("Masukkan nomor rekening tujuan: ");
            String nomorRekening = scanner.nextLine();
            System.out.print("Masukkan nama bank: ");
            String namaBank = scanner.nextLine();
            pembayaran = new PembayaranTransfer(harga, nomorRekening, namaBank);
            metodePembayaran = "Transfer " + namaBank;
            detailPembayaran = "No. Rekening: " + nomorRekening;
        }

        if (pembayaran != null) {
            pembayaran.bayar();

            // Tambahkan opsi pengiriman struk
            System.out.println("\nPilih metode pengiriman struk:");
            System.out.println("1. Email");
            System.out.println("2. Nomor WhatsApp");
            System.out.print("Pilih metode pengiriman: ");
            int pilihanPengiriman = scanner.nextInt();
            scanner.nextLine();

            String infoPengiriman = "";
            if (pilihanPengiriman == 1) {
                System.out.print("Masukkan alamat email: ");
                String email = scanner.nextLine();
                infoPengiriman = "Email: " + email;
            } else if (pilihanPengiriman == 2) {
                System.out.print("Masukkan nomor WhatsApp: ");
                String nomorWhatsApp = scanner.nextLine();
                infoPengiriman = "WhatsApp: " + nomorWhatsApp;
            }

            buatStrukPembelian(bukuDibeli, totalHarga, metodePembayaran, detailPembayaran, infoPengiriman);

            // Tambahkan transaksi ke laporan
            StringBuilder deskripsi = new StringBuilder("Pembelian Buku Fisik:\n");
            for (MenuBuku buku : bukuDibeli) {
                deskripsi.append("- ").append(buku.getJudul()).append("\n");
            }
            deskripsi.append("Pembayaran: ").append(metodePembayaran);
            
            laporanService.tambahLaporanHarian(deskripsi.toString(), totalHarga);
            System.out.println("Transaksi pembelian berhasil dicatat dalam laporan.");
        }
    }

    private Keanggotaan cekMembership(Scanner scanner) {
        System.out.print("Apakah Anda memiliki kartu member? (y/n): ");
        String jawaban = scanner.nextLine();
        
        if (jawaban.equalsIgnoreCase("y")) {
            System.out.print("Masukkan nomor membership: ");
            String nomorMember = scanner.nextLine();
            
            for (Keanggotaan member : keanggotaanList) {
                if (member.getNomorKeanggotaan().equals(nomorMember)) {
                    return member;
                }
            }
            System.out.println("Nomor membership tidak ditemukan.");
        }
        return null;
    }
}