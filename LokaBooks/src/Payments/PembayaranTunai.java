package Payments;

public class PembayaranTunai extends Pembayaran {
    public PembayaranTunai(double jumlah) {
        super(jumlah);
    }

    @Override
    public void bayar() {
        System.out.println("Pembayaran tunai sejumlah Rp" + jumlah + " berhasil.");
    }
}