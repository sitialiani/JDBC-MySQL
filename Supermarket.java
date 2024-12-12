import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Supermarket {
    static final String DB_URL = "jdbc:mysql://localhost:3306/Supermarket"; // Ganti dengan nama DB yang sesuai
    static final String USER = "root";
    static final String PASSWORD = "password";
    private static int fakturCounter = 1; // Counter untuk membuat nomor faktur otomatis

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Simulasi Login
        boolean loginBerhasil = false;
        while (!loginBerhasil) {
            System.out.println("+-----------------------------------------------------+");
            System.out.print("Username : ");
            String username = scanner.nextLine();

            System.out.print("Password  : ");
            String password = scanner.nextLine();

            // Generate Captcha secara random
            String captchaGenerated = generateCaptcha();
            System.out.print("Captcha (" + captchaGenerated + ") : ");
            String captchaInput = scanner.nextLine();

            // Validasi login
            if (username.equalsIgnoreCase("siti") && password.equals("Siti123") && captchaGenerated.equals(captchaInput)) {
                loginBerhasil = true;
                System.out.println("Login berhasil!");
            } else {
                System.out.println("Login gagal, silakan coba lagi.\n");
            }
        }

        System.out.println("+----------------------------------------------------+");
        // Menampilkan header Supermarket
        System.out.println("Selamat Datang di Supermarket SuperBasudara");
        System.out.println("Tanggal dan Waktu : " + getCurrentDateTime());
        System.out.println("+----------------------------------------------------+");

        // Generate nomor faktur otomatis
        String noFaktur = "FTR" + String.format("%04d", fakturCounter++);
        System.out.println("No. Faktur      : " + noFaktur);

        // Input data transaksi dan produk
        String lanjut = "";  // Inisialisasi variabel lanjut
        do {
            try {
                System.out.println("=== Program Transaksi ===");

                System.out.print("Masukkan Kode Barang: ");
                String kodeBarang = scanner.nextLine();

                System.out.print("Masukkan Nama Barang: ");
                String namaBarang = scanner.nextLine();

                System.out.print("Masukkan Harga Barang: ");
                double hargaBarang = scanner.nextDouble();
                if (hargaBarang <= 0) {
                    throw new IllegalArgumentException("Harga barang harus lebih dari 0.");
                }

                System.out.print("Masukkan Jumlah Beli: ");
                int jumlah = scanner.nextInt();
                if (jumlah <= 0) {
                    throw new IllegalArgumentException("Jumlah beli harus lebih dari 0.");
                }

                scanner.nextLine(); // Membersihkan buffer input

                // Membuat objek Produk dan Pesanan
                Produk produk = new Produk(kodeBarang, namaBarang, hargaBarang, jumlah);
                Pesanan pesanan = new Pesanan(kodeBarang, namaBarang, hargaBarang, jumlah, jumlah);

                // Menambahkan produk ke database
                createProduct(produk);

                // Menampilkan Semua Produk
                readProducts();

                // Memperbarui Harga Produk
                updateProductPrice(kodeBarang, hargaBarang + 5000);  // Update harga produk setelah penjualan

                // Menampilkan Pesanan
                System.out.println("Pesanan: " + pesanan);

                // Menanyakan apakah pengguna ingin menambah pesanan lain
                System.out.print("Masih ada pesanan lain? (ya/tidak): ");
                lanjut = scanner.nextLine().trim().toLowerCase();
            } catch (Exception e) {
                System.out.println("Terjadi kesalahan: " + e.getMessage());
                scanner.nextLine(); // Membersihkan buffer input
            }
        } while (lanjut.equals("ya")); // Perulangan berhenti jika jawaban bukan "ya"

        // Menghapus Produk
        deleteProduct("P001"); // Contoh penghapusan produk berdasarkan kode barang

        scanner.close();
        System.out.println("Program telah selesai digunakan.");
    }

    // CREATE: Menambahkan Produk
    public static void createProduct(Produk produk) {
        String insertSQL = "INSERT INTO produk (kode_barang, nama_barang, harga_barang, stok) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, produk.getKodeBarang());
            pstmt.setString(2, produk.getNamaBarang());
            pstmt.setDouble(3, produk.getHargaBarang());
            pstmt.setInt(4, produk.getStok());
            pstmt.executeUpdate();
            System.out.println("Produk " + produk.getNamaBarang() + " berhasil ditambahkan.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ: Menampilkan Semua Produk
    public static void readProducts() {
        String selectSQL = "SELECT * FROM produk";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                String kodeBarang = rs.getString("kode_barang");
                String namaBarang = rs.getString("nama_barang");
                double hargaBarang = rs.getDouble("harga_barang");
                int stok = rs.getInt("stok");
                System.out.println("Kode Barang: " + kodeBarang + ", Nama Barang: " + namaBarang +
                        ", Harga Barang: " + hargaBarang + ", Stok: " + stok);
            }
        } catch (SQLException e)            {
            e.printStackTrace();
        }
    }

    // UPDATE: Memperbarui Harga Produk
    public static void updateProductPrice(String kodeBarang, double newPrice) {
        String updateSQL = "UPDATE produk SET harga_barang = ? WHERE kode_barang = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setDouble(1, newPrice);
            pstmt.setString(2, kodeBarang);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Harga produk dengan kode " + kodeBarang + " berhasil diperbarui.");
            } else {
                System.out.println("Produk dengan kode " + kodeBarang + " tidak ditemukan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE: Menghapus Produk
    public static void deleteProduct(String kodeBarang) {
        String deleteSQL = "DELETE FROM produk WHERE kode_barang = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setString(1, kodeBarang);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Produk dengan kode " + kodeBarang + " berhasil dihapus.");
            } else {
                System.out.println("Produk dengan kode " + kodeBarang + " tidak ditemukan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fungsi untuk mendapatkan waktu dan tanggal saat ini
    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(new Date());
    }

    // Fungsi untuk menghasilkan Captcha secara acak
    public static String generateCaptcha() {
        String captchaChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int randomIndex = (int) (Math.random() * captchaChars.length());
            captcha.append(captchaChars.charAt(randomIndex));
        }
        return captcha.toString();
    }
}
