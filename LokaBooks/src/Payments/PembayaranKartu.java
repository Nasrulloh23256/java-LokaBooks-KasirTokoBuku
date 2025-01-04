package Payments;

public class PembayaranKartu extends Pembayaran {
    private String nomorKartu, bank;

    public PembayaranKartu(double jumlah, String nomorKartu, String bank) {
        super(jumlah);
        this.nomorKartu = nomorKartu;
        this.bank = bank;
    }

    @Override
    public void bayar() {
        System.out.println("Pembayaran kartu dengan bank " + bank + " berhasil sebesar Rp" + jumlah);
    }
}
