package services;

import models.MenuBuku;
import java.util.List;
import java.util.stream.Collectors;

public class PencarianJudul implements PencarianStrategy {
    @Override
    public List<MenuBuku> cari(List<MenuBuku> bukuList, String keyword) {
        return bukuList.stream()
                .filter(buku -> buku.getJudul().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public String getNamaPencarian() {
        return "Judul";
    }
} 