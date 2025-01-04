package controller;

import models.BukuDigital;
import models.Keanggotaan;
import models.MenuBuku;
import models.Transaksi;
import models.TransaksiRepository;
import services.LaporanService;
import models.BukuFisik;
import utils.TerminalUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class Admin {
    private LaporanService laporanService;
    private ArrayList<MenuBuku> bukuList;
    private ArrayList<Keanggotaan> keanggotaanList;

    public Admin(ArrayList<MenuBuku> bukuList, ArrayList<Keanggotaan> keanggotaanList) {
        this.bukuList = bukuList;
        this.keanggotaanList = keanggotaanList;
        this.laporanService = LaporanService.getInstance();
    }

    public void tampilkanMenuAdmin() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n\tMENU ADMIN");
            System.out.println("1. Tampilkan Daftar Buku");
            System.out.println("2. Tambah Buku Baru");
            System.out.println("3. Hapus Buku");
            System.out.println("4. Cek Status Peminjaman");
            System.out.println("5. Laporan");
            System.out.println("6. Kelola Keanggotaan");
            System.out.println("0. Keluar");
            System.out.print("Pilih menu: ");
            
            int pilihan = scanner.nextInt();
            scanner.nextLine();

            if (pilihan == 0) {
                running = false;
                continue;
            }

            TerminalUtils.clearScreen();
            switch (pilihan) {
                case 1:
                    tampilkanDaftarBuku();
                    break;
                case 2:
                    tambahBukuBaru(scanner);
                    break;
                case 3:
                    hapusBuku(scanner);
                    break;
                case 4:
                    cekStatusPeminjaman();
                    break;
                case 5:
                    kelolaLaporan(scanner);
                    break;
                case 6:
                    kelolaKeanggotaan(scanner);
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
            System.out.println("\nTekan Enter untuk kembali ke menu...");
            scanner.nextLine();
            TerminalUtils.clearScreen();
        }
    }

    private void kelolaKeanggotaan(Scanner scanner) {
        boolean keanggotaanRunning = true;
        while (keanggotaanRunning) {
            System.out.println("\nMENU KEANGGOTAAN");
            System.out.println("1. Tambah Keanggotaan");
            System.out.println("2. Hapus Keanggotaan");
            System.out.println("3. Tampilkan Daftar Keanggotaan");
            System.out.println("0. Kembali ke Menu Utama");
            System.out.print("Pilih menu: ");
            
            int pilihan = scanner.nextInt();
            scanner.nextLine();

            if (pilihan == 0) {
                keanggotaanRunning = false;
                continue;
            }

            switch (pilihan) {
                case 1:
                    tambahKeanggotaan(scanner);
                    break;
                case 2:
                    hapusKeanggotaan(scanner);
                    break;
                case 3:
                    tampilkanDaftarKeanggotaan();
                    System.out.println("\nTekan Enter untuk kembali ke menu...");
                    scanner.nextLine();
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
            TerminalUtils.clearScreen();
        }
    }

    private void tambahKeanggotaan(Scanner scanner) {
        System.out.println("\nREGISTRASI MEMBERSHIP BARU");
        System.out.println("===========================");
        
        System.out.print("Masukkan nomor keanggotaan: ");
        String nomorKeanggotaan = scanner.nextLine();
        
        // Cek apakah nomor keanggotaan sudah ada
        boolean nomorExists = keanggotaanList.stream()
                .anyMatch(k -> k.getNomorKeanggotaan().equals(nomorKeanggotaan));
        if (nomorExists) {
            System.out.println("Nomor keanggotaan sudah terdaftar!");
            System.out.println("\nTekan Enter untuk melanjutkan...");
            scanner.nextLine();
            TerminalUtils.clearScreen();
            return;
        }

        System.out.print("Masukkan nama pelanggan: ");
        String namaPelanggan = scanner.nextLine();

        // Tanggal member otomatis hari ini
        LocalDate tanggalMember = LocalDate.now();

        // Buat objek keanggotaan baru
        Keanggotaan keanggotaanBaru = new Keanggotaan(nomorKeanggotaan, namaPelanggan, tanggalMember);
        keanggotaanList.add(keanggotaanBaru);

        System.out.println("\nMembership berhasil didaftarkan!");
        System.out.println("Detail Membership:");
        System.out.println("------------------");
        keanggotaanBaru.tampilkanInfoKeanggotaan();
        
        System.out.println("\nTekan Enter untuk melanjutkan...");
        scanner.nextLine();
        TerminalUtils.clearScreen();
    }
    

    private void hapusKeanggotaan(Scanner scanner) {
        System.out.print("Masukkan nomor keanggotaan yang ingin dihapus: ");
        String nomorKeanggotaan = scanner.nextLine();

        keanggotaanList.removeIf(keanggotaan -> keanggotaan.getNomorKeanggotaan().equals(nomorKeanggotaan));
        System.out.println("Keanggotaan dengan nomor " + nomorKeanggotaan + " berhasil dihapus.");
    }

    private void tampilkanDaftarKeanggotaan() {
        if (keanggotaanList.isEmpty()) {
            System.out.println("Belum ada member yang ditambahkan. Silakan tambahkan member terlebih dahulu.");
            return;
        }

        // Format tabel yang lebih rapi dengan lebar kolom yang disesuaikan
        String formatLine = "+---------------+------------------+----------------+------------------+------------------+--------------+-----------+";
        String formatHeader = "| %-13s | %-16s | %-14s | %-16s | %-16s | %-12s | %-9s |";
        String formatData = "| %-13s | %-16s | %-14s | %-16d | %-16d | %-12d | %7.1f%% |";

        System.out.println("\nDaftar Keanggotaan:");
        System.out.println(formatLine);
        System.out.printf(formatHeader,
            "No. Member",
            "Nama Pelanggan",
            "Tanggal Member",
            "Transaksi Beli",
            "Transaksi Pinjam",
            "Poin Reward",
            "Diskon");
        System.out.println();
        System.out.println(formatLine);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Keanggotaan member : keanggotaanList) {
            System.out.printf(formatData,
                member.getNomorKeanggotaan(),
                truncateString(member.getNamaPelanggan(), 16),
                member.getTanggalMember().format(formatter),
                member.getJumlahTransaksiBeli(),
                member.getJumlahTransaksiPinjam(),
                member.getPoinReward(),
                member.hitungDiskon() * 100
            );
            System.out.println();
        }
        System.out.println(formatLine);
    }
    

    private void kelolaLaporan(Scanner scanner) {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\nMENU LAPORAN");
            System.out.println("1. Tampilkan Laporan");
            System.out.println("2. Tambah Laporan");
            System.out.println("0. Kembali ke Menu Utama");
            System.out.print("Pilih menu: ");
            
            int pilihan = scanner.nextInt();
            scanner.nextLine();

            if (pilihan == 0) {
                isRunning = false;
                continue;
            }

            switch (pilihan) {
                case 1:
                    tampilkanLaporan(scanner);
                    break;
                case 2:
                    tambahLaporan(scanner);
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
            System.out.println("\nTekan Enter untuk kembali ke menu...");
            scanner.nextLine();
            TerminalUtils.clearScreen();
        }
    }

    private void tampilkanLaporan(Scanner scanner) {
        System.out.println("\nPilih jenis laporan yang ingin ditampilkan:");
        System.out.println("1. Laporan Harian");
        System.out.println("2. Laporan Mingguan");
        System.out.println("3. Laporan Bulanan");
        System.out.print("Pilih jenis laporan: ");
        int pilihan = scanner.nextInt();
        scanner.nextLine();

        List<Transaksi> laporan = new ArrayList<>();
        String formatLine = "+-------------+------------------------------------+-------------+";

        switch (pilihan) {
            case 1:
                System.out.print("Masukkan tanggal (dd/mm/yyyy): ");
                String tanggalInput = scanner.nextLine();
                String tanggal = LocalDate.parse(tanggalInput, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                           .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                laporan = laporanService.getLaporanHarian().stream()
                                        .filter(t -> t.getTanggal().equals(tanggal))
                                        .collect(Collectors.toList());
                System.out.println(formatLine);
                System.out.println("| Tanggal     | Deskripsi                          | Nominal     |");
                break;
            case 2:
                System.out.print("Masukkan minggu-ke (n): ");
                int mingguKe = scanner.nextInt();
                scanner.nextLine();
                String minggu = "Minggu-" + mingguKe;
                laporan = laporanService.getLaporanMingguan().stream()
                                        .filter(t -> t.getMinggu().equals(minggu))
                                        .collect(Collectors.toList());
                System.out.println(formatLine);
                System.out.println("| Minggu      | Deskripsi                          | Nominal     |");
                break;
            case 3:
                System.out.print("Masukkan bulan (mm/yyyy): ");
                String bulanInput = scanner.nextLine();
                String bulan = LocalDate.parse("01/" + bulanInput, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM"));
                laporan = laporanService.getLaporanBulanan().stream()
                                        .filter(t -> t.getBulan().equals(bulan))
                                        .collect(Collectors.toList());
                System.out.println(formatLine);
                System.out.println("| Bulan       | Deskripsi                          | Nominal     |");
                break;
            default:
                System.out.println("Pilihan tidak valid.");
                return;
        }

        // Tampilkan hasil laporan
        if (laporan.isEmpty()) {
            System.out.println("Belum ada transaksi untuk jenis laporan ini.");
        } else {
            System.out.println(formatLine);
            for (Transaksi t : laporan) {
                String key = pilihan == 1 ? t.getTanggal() : (pilihan == 2 ? t.getMinggu() : t.getBulan());
                String[] deskripsiLines = t.getDeskripsi().split("\n");
                
                // Print first line with key
                System.out.printf("| %-11s | %-34s | Rp %8.2f |\n", 
                    key, 
                    truncateString(deskripsiLines[0], 34), 
                    t.getNominal());
                
                // Print remaining lines if any
                for (int i = 1; i < deskripsiLines.length; i++) {
                    System.out.printf("| %-11s | %-34s | %-11s |\n",
                        "",
                        truncateString(deskripsiLines[i], 34),
                        "");
                }
                System.out.println(formatLine);
            }
        }

        System.out.println("\nTekan Enter untuk kembali...");
        scanner.nextLine();
        TerminalUtils.clearScreen();
    }

    private void tambahLaporan(Scanner scanner) {
        System.out.println("\nPilih Jenis Laporan yang akan ditambahkan:");
        System.out.println("1. Laporan Harian");
        System.out.println("2. Laporan Mingguan");
        System.out.println("3. Laporan Bulanan");
        System.out.print("Pilih jenis laporan: ");
        
        int pilihan = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Masukkan nominal transaksi: Rp ");
        double nominal = scanner.nextDouble();
        scanner.nextLine();

        switch (pilihan) {
            case 1:
                System.out.print("Masukkan deskripsi transaksi: ");
                String deskripsi = scanner.nextLine();
                laporanService.tambahLaporanHarian(deskripsi, nominal);
                System.out.println("Laporan Harian berhasil ditambahkan.");
                break;
            case 2:
                System.out.print("Masukkan minggu (contoh: Minggu-1): ");
                String minggu = scanner.nextLine();
                System.out.print("Masukkan deskripsi transaksi: ");
                String deskripsiMinggu = scanner.nextLine();
                laporanService.tambahLaporanMingguan(minggu, deskripsiMinggu, nominal);
                System.out.println("Laporan Mingguan berhasil ditambahkan.");
                break;
            case 3:
                System.out.print("Masukkan bulan (contoh: 2024-01): ");
                String bulan = scanner.nextLine();
                System.out.print("Masukkan deskripsi transaksi: ");
                String deskripsiBulan = scanner.nextLine();
                laporanService.tambahLaporanBulanan(bulan, deskripsiBulan, nominal);
                System.out.println("Laporan Bulanan berhasil ditambahkan.");
                break;
            default:
                System.out.println("Pilihan tidak valid.");
        }
    }

    private void tampilkanDaftarBuku() {
        String headerFormat = "| %-5s | %-30s | %-20s | %-15s | %-12s | %-10s | %-15s | %-12s |";
        String lineFormat = "+-%-5s-+-%-30s-+-%-20s-+-%-15s-+-%-12s-+-%-10s-+-%-15s-+-%-12s-+";
        
        String line = String.format(lineFormat,
                "-".repeat(5),
                "-".repeat(30),
                "-".repeat(20),
                "-".repeat(15),
                "-".repeat(12),
                "-".repeat(10),
                "-".repeat(15),
                "-".repeat(12));

        System.out.println("\nDaftar Buku Digital:");
        System.out.println(line);
        System.out.printf(headerFormat,
                "No",
                "Judul",
                "Penulis",
                "Genre",
                "Harga (Rp)",
                "Stok",
                "Ukuran (MB)",
                "Format");
        System.out.println();
        System.out.println(line);

        int counter = 1;
        for (MenuBuku buku : bukuList) {
            if (buku instanceof BukuDigital) {
                BukuDigital bukuDigital = (BukuDigital) buku;
                System.out.printf("| %-5d | %-30s | %-20s | %-15s | %-12.2f | %-10s | %-15.1f | %-12s |%n",
                        counter++,
                        truncateString(buku.getJudul(), 30),
                        truncateString(buku.getPenulis(), 20),
                        truncateString(buku.getGenre(), 15),
                        buku.getHarga(),
                        "∞",
                        bukuDigital.getUkuranFile(),
                        bukuDigital.getFormatFile());
                System.out.println(line);
            }
        }

        System.out.println("\nDaftar Buku Fisik:");
        System.out.println(line);
        System.out.printf(headerFormat,
                "No",
                "Judul",
                "Penulis",
                "Genre",
                "Harga (Rp)",
                "Stok",
                "Ukuran (MB)",
                "Format");
        System.out.println();
        System.out.println(line);

        counter = 1;
        for (MenuBuku buku : bukuList) {
            if (buku instanceof BukuFisik) {
                BukuFisik bukuFisik = (BukuFisik) buku;
                System.out.printf("| %-5d | %-30s | %-20s | %-15s | %-12.2f | %-10d | %-15s | %-12s |%n",
                        counter++,
                        truncateString(buku.getJudul(), 30),
                        truncateString(buku.getPenulis(), 20),
                        truncateString(buku.getGenre(), 15),
                        buku.getHarga(),
                        bukuFisik.getStok(),
                        "-",
                        "-");
                System.out.println(line);
            }
        }
    }

    private String truncateString(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }

    private void tambahBukuBaru(Scanner scanner) {
        System.out.println("\nPilih jenis buku:");
        System.out.println("1. Buku Digital");
        System.out.println("2. Buku Fisik");
        System.out.print("Pilihan: ");
        int jenisBuku = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Masukkan judul buku baru: ");
        String judul = scanner.nextLine();
        
        MenuBuku bukuExisting = cariBuku(judul);
        if (bukuExisting != null) {
            if (bukuExisting instanceof BukuFisik) {
                System.out.println("\nBuku dengan judul '" + judul + "' sudah ada dalam daftar.");
                System.out.print("Apakah Anda ingin menambah stok? (ya/tidak): ");
                String jawaban = scanner.nextLine();
                
                if (jawaban.equalsIgnoreCase("ya")) {
                    System.out.print("Masukkan jumlah stok yang akan ditambahkan: ");
                    int tambahStok = scanner.nextInt();
                    scanner.nextLine();
                    
                    BukuFisik bukuFisik = (BukuFisik) bukuExisting;
                    bukuFisik.setStok(bukuFisik.getStok() + tambahStok);
                    System.out.println("Stok buku '" + judul + "' berhasil ditambahkan.");
                    System.out.println("Stok saat ini: " + bukuFisik.getStok());
                }
            } else {
                System.out.println("Buku digital dengan judul tersebut sudah ada.");
            }
            return;
        }

        System.out.print("Masukkan nama penulis: ");
        String penulis = scanner.nextLine();
        System.out.print("Masukkan genre buku: ");
        String genre = scanner.nextLine();
        System.out.print("Masukkan harga buku: ");
        double harga = scanner.nextDouble();
        scanner.nextLine();

        MenuBuku bukuBaru;
        if (jenisBuku == 1) {
            System.out.print("Masukkan ukuran file (MB): ");
            double ukuranFile = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Masukkan format file (PDF/EPUB): ");
            String formatFile = scanner.nextLine();
            bukuBaru = new BukuDigital(judul, penulis, harga, genre, ukuranFile, formatFile);
        } else {
            System.out.print("Masukkan stok awal: ");
            int stok = scanner.nextInt();
            scanner.nextLine();
            bukuBaru = new BukuFisik(judul, penulis, harga, genre, stok);
        }

        bukuList.add(bukuBaru);
        System.out.println("Buku baru berhasil ditambahkan.");
    }

    private MenuBuku cariBuku(String judul) {
        return bukuList.stream()
                .filter(buku -> buku.getJudul().equalsIgnoreCase(judul))
                .findFirst()
                .orElse(null);
    }

    private void hapusBuku(Scanner scanner) {
        System.out.print("Masukkan judul buku yang ingin dihapus: ");
        String judul = scanner.nextLine();

        for (MenuBuku buku : bukuList) {
            if (buku.getJudul().equalsIgnoreCase(judul)) {
                bukuList.remove(buku);
                System.out.println("Buku " + judul + " berhasil dihapus.");
                return;
            }
        }
        System.out.println("Buku tidak ditemukan.");
    }

    private void cekStatusPeminjaman() {
        System.out.println("\nDaftar Buku yang Sedang Dipinjam:");
        boolean adaBukuDipinjam = false;

        for (MenuBuku buku : bukuList) {
            if (buku.isDipinjam()) {
                System.out.println("- " + buku.getJudul() + " (Pinjam: " + buku.getTanggalPinjam() + ", Kembali: " + buku.getTanggalKembali() + ")");
                adaBukuDipinjam = true;
            }
        }

        if (!adaBukuDipinjam) {
            System.out.println("Tidak ada buku yang sedang dipinjam.");
        }
    }
}
