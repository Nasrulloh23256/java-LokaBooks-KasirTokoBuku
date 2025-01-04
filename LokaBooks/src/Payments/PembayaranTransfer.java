package Payments;

public class PembayaranTransfer extends Pembayaran {
    private String nomorRekening, bank;

    public PembayaranTransfer(double jumlah, String nomorRekening, String bank) {
        super(jumlah);
        this.nomorRekening = nomorRekening;
        this.bank = bank;
    }

    @Override
    public void bayar() {
        System.out.println("Pembayaran transfer ke rekening " + nomorRekening + " di bank " + bank + " sebesar Rp"
                + jumlah + " berhasil.");
    }
}