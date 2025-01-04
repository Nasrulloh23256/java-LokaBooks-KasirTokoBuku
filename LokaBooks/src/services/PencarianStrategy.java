package services;

import models.MenuBuku;
import java.util.List;

public interface PencarianStrategy {
    List<MenuBuku> cari(List<MenuBuku> bukuList, String keyword);
    String getNamaPencarian();
} 