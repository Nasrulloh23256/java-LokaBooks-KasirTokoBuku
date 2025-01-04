package models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransaksiRepository {
    private List<Transaksi> transaksiList;

    public TransaksiRepository() {
        this.transaksiList = new ArrayList<>();
    }

    public void tambahTransaksi(Transaksi transaksi) {
        transaksiList.add(transaksi);
    }

    public List<Transaksi> getTransaksiByJenis(String jenis) {
        return transaksiList.stream()
                .filter(t -> t.getJenis().equals(jenis))
                .collect(Collectors.toList());
    }

    public List<Transaksi> getAllTransaksi() {
        return new ArrayList<>(transaksiList);
    }
}
