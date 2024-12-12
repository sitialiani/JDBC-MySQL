public class Pesanan extends Produk {
    private int jumlahBeli;

    // Constructor
    public Pesanan(String kodeBarang, String namaBarang, double hargaBarang, int stok, int jumlahBeli) {
        super(kodeBarang, namaBarang, hargaBarang, stok);
        this.jumlahBeli = jumlahBeli;
    }

    // Getter dan Setter
    public int getJumlahBeli() {
        return jumlahBeli;
    }

    public void setJumlahBeli(int jumlahBeli) {
        this.jumlahBeli = jumlahBeli;
    }

    @Override
    public String toString() {
        return super.toString() + ", Jumlah Beli: " + jumlahBeli;
    }
}
