package view;

import controller.Admin;
import controller.Kasir;
import models.MenuBuku;
import models.BukuDigital;
import models.Transaksi;
import models.TransaksiRepository;
import services.LaporanService;
import models.BukuFisik;
import models.Keanggotaan;
import utils.TerminalUtils;

//Nama  : Muhammad Nasrulloh
//NIM   : 23051204256
//Kelas : TI 23 H


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;



public class AppBuku {
    private static ArrayList<MenuBuku> bukuList = new ArrayList<>();
    private static ArrayList<Keanggotaan> keanggotaanList = new ArrayList<>();
    private static Admin admin;
    private static Kasir kasir;

    public static void main(String[] args) {
        initializeData();
        admin = new Admin(bukuList, keanggotaanList);
        kasir = new Kasir(bukuList, keanggotaanList);
        
        Scanner scanner = new Scanner(System.in);
        
        boolean isRunning = true;
        
        while (isRunning) {
            try {
                TerminalUtils.clearScreen();
                System.out.println("\u001B[36m====================================================\u001B[0m");
                System.out.println("\u001B[35m                     LokaBooks                \u001B[0m");
                System.out.println("\u001B[36m====================================================\u001B[0m");
                System.out.println();
                System.out.println("\u001B[36m====================================================\u001B[0m");
                System.out.println("\u001B[32m                    Created By:                   \u001B[0m");
                System.out.println("\u001B[33m            NOVITA PUTRI ANGELLINA (252)              \u001B[0m");
                System.out.println("\u001B[33m            MUHAMMAD NASRULLOH     (256)              \u001B[0m");
                System.out.println("\u001B[33m            MUHAMMAD RIZAL FAUZAN  (257)              \u001B[0m");
                System.out.println("\u001B[33m            FARHAN YOGA            (267)              \u001B[0m");
                System.out.println("\u001B[33m            WILDAN GALIH RAMADHAN  (269)              \u001B[0m");
                System.out.println("\u001B[33m            BAGAS YUDO NUGROHO     (283)              \u001B[0m");
                System.out.println("\u001B[36m====================================================\u001B[0m");
                System.out.println();
                System.out.println("\u001B[36m====================================================\u001B[0m");
                System.out.println("\u001B[35m           SISTEM KASIR TOKO BUKU LITERA LOKA                \u001B[0m");
                System.out.println("\u001B[36m====================================================\u001B[0m");
                System.out.println("\u001B[34m1. Menu Admin\u001B[0m");
                System.out.println("\u001B[34m2. Menu Pembayaran\u001B[0m");
                System.out.println("\u001B[31m0. Keluar\u001B[0m");
                System.out.println("\u001B[36m====================================================\u001B[0m");
                System.out.print("Pilih menu: ");
                
                String input = scanner.nextLine();
                
                // Validasi input hanya berisi angka
                if (!input.matches("[0-2]")) {
                    System.out.println("\nError: Pilihan tidak valid!");
                    System.out.println("Silakan pilih menu 0-2");
                    System.out.println("Tekan Enter untuk melanjutkan...");
                    scanner.nextLine();
                    continue;
                }
                
                int pilihan = Integer.parseInt(input);
                
                switch (pilihan) {
                    case 1:
                        admin.tampilkanMenuAdmin();
                        break;
                    case 2:
                        kasir.tampilkanMenuPembayaran();
                        break;
                    case 0:
                        isRunning = false;
                        System.out.println("Terima kasih telah menggunakan sistem kasir Litera Loka.");
                        break;
                    default:
                        System.out.println("\nError: Pilihan tidak valid!");
                        System.out.println("Silakan pilih menu 0-2");
                        System.out.println("Tekan Enter untuk melanjutkan...");
                        scanner.nextLine();
                }
            } catch (Exception e) {
                System.out.println("\nError: Terjadi kesalahan input!");
                System.out.println("Detail error: " + e.getMessage());
                System.out.println("Silakan coba lagi.");
                System.out.println("Tekan Enter untuk melanjutkan...");
                scanner.nextLine(); // Clear buffer
                scanner.nextLine(); // Wait for Enter
            }
            
            // Clear screen (opsional, sesuaikan dengan sistem operasi)
            // System.out.print("\033[H\033[2J");
            // System.out.flush();
        }
        
        scanner.close();
    }
    
    private static void initializeData() {
        // Buku Digital
        bukuList.add(new BukuDigital("Rich Dad Poor Dad", "Robert Kiyosaki", 95000.0, "Keuangan", 5.5, "PDF"));
        bukuList.add(new BukuDigital("Atomic Habits", "James Clear", 120000.0, "Self-Improvement", 5.0, "PDF"));
        bukuList.add(new BukuDigital("Sapiens", "Yuval Noah Harari", 150000.0, "Sejarah", 12.5, "EPUB"));
        bukuList.add(new BukuDigital("Psychology of Money", "Morgan Housel", 85000.0, "Keuangan", 3.2, "PDF"));
        bukuList.add(new BukuDigital("Deep Work", "Cal Newport", 110000.0, "Produktivitas", 4.8, "EPUB"));
        
        // Buku Fisik
        bukuList.add(new BukuFisik("Bumi", "Tere Liye", 95000.0, "Fiksi", 10));
        bukuList.add(new BukuFisik("Laskar Pelangi", "Andrea Hirata", 85000.0, "Fiksi", 15));
        bukuList.add(new BukuFisik("Filosofi Teras", "Henry Manampiring", 98000.0, "Filsafat", 8));
        bukuList.add(new BukuFisik("Laut Bercerita", "Leila S. Chudori", 105000.0, "Fiksi", 12));
        bukuList.add(new BukuFisik("Pulang", "Tere Liye", 89000.0, "Fiksi", 20));
    }
}
