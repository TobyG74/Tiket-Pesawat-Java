# Tiket Pesawat

-   Project ini menggunakan Database SQLite & Menggunakan CRUD
-   Library yang digunakan berupa Java GUI (javax.swing) dan sql
-   Pemesanan Tiket menggunakan waktu secara realtime

## Set UP

-   Lokasi Database SQLite

```java
private static final String DATABASE_PATH = "./tiketPesawat.db"
```

-   Username & Password (Default)

```java
private static String username = "admin";
private static String password = "admin";
```

## Function

### Main

-   Membuat Tabel SQLite berupa `admin` dan `tiket`
-   Menampilkan Menu GUI berupa `Pesan Tiket` dan `Menu Admin`

```java
public static void main(String[] args) {
    ...
}
```

### databaseAdminNotNull

-   Melakukan Pengecekan Database Admin

```java
private static void databaseAdminNotNull(Statement s) {
    ...
}
```

### getDate

-   Mendapatkan Tanggal Terupdate

```java
private static String getDate() {
    ...
}
```

### checkWaktuKeberangkatan

-   Mengecek Waktu Keberangkatan dengan Waktu Saat Melakukan Order

```java
private static void checkWaktuKeberangkatan(int jam) {
    ...
}
```

### loginAdmin

-   Melakukan Verifikasi Login Admin sebelum masuk ke Menu Admin

```java
private static void loginAdmin(Statement s) {
    ...
}
```

### menuAdmin

-   Menampilkan Menu Admin
-   Menu (Read, Update, Delete) hanya bisa di Menu Admin

```java
private static void menuAdmin(Statement s) {
    ...
}
```

### inputDataTiket

-   Melakukan Input Data Tiket dan Memasukannya ke Database

```java
private static void inputDataTiket(Statement s) {
    ...
}
```

### tampilkanDataTiket

-   Menampilkan Data dari Pesananan Tiket berupa Tabel

```java
private static void tampilkanDataTiket(Statement s) {
    ...
}
```

### updateDataTiket

-   Memperbarui Data Tiket di Database Berdasarkan ID Tiket
-   Menampilkan Menu Pembaruan dan Mengupdate nya ke Database

```java
private static void updateDataTiket(Statement s) {
    ...
}
```

### updateDataAdmin

-   Memperbarui Data Admin di Database seperti mengganti Password Lama dengan Password Baru

```java
private static void updateDataAdmin(Statement s) {
    ...
}
```

### hapusDataTiket

-   Menghapus Data Tiket dari Database berdasarkan ID Tiket

```java
private static void hapusDataTiket(Statement s) {
    ...
}
```
