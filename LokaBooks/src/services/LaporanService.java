package services;

import models.Transaksi;
import models.TransaksiRepository;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LaporanService {
    private static LaporanService instance;
    private TransaksiRepository transaksiRepository;

    private LaporanService() {
        this.transaksiRepository = new TransaksiRepository();
    }

    public static LaporanService getInstance() {
        if (instance == null) {
            instance = new LaporanService();
        }
        return instance;
    }

    public void tambahLaporanHarian(String deskripsi, double nominal) {
        String tanggal = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Transaksi transaksiHarian = new Transaksi(deskripsi, nominal, tanggal, "", "", "1");
        transaksiRepository.tambahTransaksi(transaksiHarian);

        LocalDateTime now = LocalDateTime.now();
        int weekOfMonth = (now.getDayOfMonth() - 1) / 7 + 1;
        String minggu = "Minggu-" + weekOfMonth;
        Transaksi transaksiMingguan = new Transaksi(deskripsi, nominal, "", minggu, "", "2");
        transaksiRepository.tambahTransaksi(transaksiMingguan);

        String bulan = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Transaksi transaksiBulanan = new Transaksi(deskripsi, nominal, "", "", bulan, "3");
        transaksiRepository.tambahTransaksi(transaksiBulanan);
    }

    public void tambahLaporanMingguan(String minggu, String deskripsi, double nominal) {
        Transaksi transaksi = new Transaksi(deskripsi, nominal, "", minggu, "", "2");
        transaksiRepository.tambahTransaksi(transaksi);
    }

    public void tambahLaporanBulanan(String bulan, String deskripsi, double nominal) {
        Transaksi transaksi = new Transaksi(deskripsi, nominal, "", "", bulan, "3");
        transaksiRepository.tambahTransaksi(transaksi);
    }

    public List<Transaksi> getLaporanHarian() {
        return transaksiRepository.getTransaksiByJenis("1");
    }

    public List<Transaksi> getLaporanMingguan() {
        return transaksiRepository.getTransaksiByJenis("2");
    }

    public List<Transaksi> getLaporanBulanan() {
        return transaksiRepository.getTransaksiByJenis("3");
    }
}
