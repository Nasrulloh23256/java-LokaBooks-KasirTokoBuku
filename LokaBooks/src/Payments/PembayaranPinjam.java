package Payments;

// Pembayaran denda untuk peminjaman buku
public class PembayaranPinjam extends Pembayaran {
    private String tanggalPinjam, tanggalKembali;
    private double denda;

    public PembayaranPinjam(double jumlah, String tanggalPinjam, String tanggalKembali, double denda) {
        super(jumlah);
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembali = tanggalKembali;
        this.denda = denda;
    }

    @Override
    public void bayar() {
        double totalPembayaran = jumlah + denda;
        System.out
                .println("Total yang harus dibayar untuk peminjaman (termasuk denda jika ada): Rp " + totalPembayaran);
    }
}
