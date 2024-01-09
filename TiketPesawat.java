import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TiketPesawat {
    private static final String DATABASE_PATH = "./tiketPesawat.db"; // Lokasi Database
    private static String username = "admin"; // Login Admin dengan username default (admin)
    private static String password = "admin"; // Login Admin dengan password default (admin)
    public static void main(String[] args) {
        try {
            /** Deklarasi Class Scanner dan SQL */
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_PATH);
            Statement s = conn.createStatement();
    
            /** Membuat Database untuk Admin */
            s.executeUpdate("CREATE TABLE IF NOT EXISTS Admin (" +
                "Username VALCHAR(255)," +
                "Password VALCHAR(255)" +
            ");");
    
            /** Membuat Database untuk Tiket */
            s.executeUpdate("CREATE TABLE IF NOT EXISTS Tiket (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Nama VALCHAR(255)," +
                "Kelas VALCHAR(255)," +
                "Tujuan VALCHAR(255)," +
                "Tanggal_Pesan VALCHAR(255)," +
                "Tanggal_Berangkat VALCHAR(255)," +
                "Jam INTEGER," +
                "Jumlah INTEGER," +
                "Harga INTEGER" +
            ");");
    
            /** Membuat Pilihan untuk Menu GUI */
            String[] options = {"Pesan Tiket", "Menu Admin", "Keluar"};
            int choice;

            do {
                /** Membuat GUI */
                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridwidth = GridBagConstraints.REMAINDER; // Mengatur Grid
                gbc.anchor = GridBagConstraints.CENTER; // Mengatur Posisi

                /** Membuat Judul GUI */
                String htmlTitle = "<html><center><h1>" + "Tiket Pesawat" + "</h1></center></html>";
                JLabel label = new JLabel(htmlTitle);
                panel.add(label, gbc);

                /** Menampilkan GUI */
                choice = JOptionPane.showOptionDialog(null, panel, "Tiket Pesawat", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    
                /** Mengatur Pilihan GUI */
                switch (choice) {
                    case 0: inputDataTiket(s); break;
                    case 1: loginAdmin(s); break;
                }
            } while (choice != 2);

            /** Menampilkan Informasi GUI Exit */
            JOptionPane.showMessageDialog(null, "Terima kasih telah menggunakan aplikasi ini", "Keluar", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Mengecek apakah database admin kosong atau tidak
     * Jika database kosong, maka akan membuat database admin dengan username dan password default
     */
    private static void databaseAdminNotNull(Statement s) {
        try {
            /** Mengecek Database Admin */
            ResultSet r = s.executeQuery("SELECT * FROM Admin;");

            /** Jika Database Admin Kosong, Maka Akan Membuat Database Admin */
            if (!r.next()) {
                String sql = "INSERT INTO Admin (Username, Password) VALUES ('" + username + "', '" + password + "');";
                s.execute(sql);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mengambil tanggal sekarang
     * @return tanggal sekarang
     */
    private static String getDate() {
        /** Membuat Array Bulan */
        String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        GregorianCalendar date = new GregorianCalendar();

        /** Mengambil Tanggal, Bulan, dan Tahun */
        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH);
        int year = date.get(Calendar.YEAR);

        return day + " " + months[month] + " " + year;
    }

    /**
     * Mengambil total tanggal pada bulan sekarang
     * @return total tanggal pada bulan sekarang
     */
    private static int getTotalDate() {
        GregorianCalendar date = new GregorianCalendar();

        int month = date.get(Calendar.MONTH);
        int year = date.get(Calendar.YEAR);

        YearMonth yearMonthObject = YearMonth.of(year, month + 1);
        int jumlahHari = yearMonthObject.lengthOfMonth();

        return jumlahHari;
    }

    /**
     * Mengecek apakah waktu keberangkatan sudah lewat atau belum
     * Membandingan waktu keberangkatan dengan waktu sekarang
     */
    private static void checkWaktuKeberangkatan(int jam, String tanggal) {
        GregorianCalendar date = new GregorianCalendar();
        
        /** Membuat Variable untuk Format Tanggal */
        String formatJam = String.valueOf(jam < 10 ? "0" + jam : jam) + ":00";
        String formatBulan = String.valueOf(date.get(Calendar.MONTH) + 1 < 10 ? "0" + (date.get(Calendar.MONTH) + 1) : (date.get(Calendar.MONTH) + 1));
        String formatTanggal = Integer.parseInt(tanggal.split(" ")[0]) < 10 ? "0" + tanggal.split(" ")[0] : tanggal.split(" ")[0];
        String formatTanggal2 = date.get(Calendar.YEAR) + "-" + formatBulan + "-" + formatTanggal;

        /** Parsing Waktu Keberangkatan dan Waktu Sekarang */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime waktuSekarang = LocalDateTime.now();
        LocalDateTime waktuKeberangkatan = LocalDateTime.parse(formatTanggal2 + " " + formatJam, formatter);

        /** Jika Waktu Sekarang Melebihi Waktu Keberangkatan Maka Akan Kembali Ke Main Menu */
        if (waktuSekarang.isAfter(waktuKeberangkatan)) {
            String[] options = {"Kembali"};

            /** Menampilkan Informasi GUI */
            int choice = JOptionPane.showOptionDialog(null, "Waktu keberangkatan sudah lewat", "Error",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            
            /** Mengatur Pilihan GUI */
            switch (choice) { 
                case 0: main(null); break;
            }
        }
    }

    /** Login Admin */
    private static void loginAdmin(Statement s) {
        try {
            /** Mengecek Database Admin */
            databaseAdminNotNull(s);
            
            /** Membuat Field Username & Password */
            JTextField usernameField = new JTextField();
            JTextField passwordField = new JTextField();

            Object[] fields = {"Username:", usernameField, "Password:", passwordField};

            /** Menampilkan GUI Login */
            int option = JOptionPane.showConfirmDialog(null, fields, "Login", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                /** Parsing Username & Password */
                String username = usernameField.getText();
                String password = passwordField.getText();

                /** 
                 * Mengecek Data Admin
                 * Jika Data Admin Kosong, Maka Akan Kembali Ke Main Menu
                 */
                if (username.equals("") || password.equals("")) {
                    JOptionPane.showMessageDialog(null, "Data tidak boleh kosong", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                /** Mengecek Data Admin */
                ResultSet r = s.executeQuery("SELECT * FROM Admin WHERE Username = '" + username + "' AND Password = '" + password + "';");

                /** Jika Data Admin Ditemukan, Maka Akan Masuk ke Menu Admin */
                if (r.next()) {
                    menuAdmin(s);
                } else {
                    /** Membuat Pilihan untuk Menu GUI */
                    String[] options = {"Coba Lagi", "Kembali"};

                    /** Menampilkan Informasi GUI */
                    int choice = JOptionPane.showOptionDialog(null, "Username atau password salah", "Error",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                    
                    /** Mengatur Pilihan GUI */
                    switch (choice) {
                        case 0: loginAdmin(s); break;
                        case 1: main(null); break;
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Menu Admin */
    private static void menuAdmin(Statement s) {
        /** Membuat Pilihan untuk Menu Admin */
        String[] options = {"Tampilkan Data Tiket", "Update Data Tiket", "Hapus Data Tiket", "Update Data Admin", "Kembali"};
        int choice;

        do {
            /** Membuat GUI */
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER; // Mengatur Grid
            gbc.anchor = GridBagConstraints.CENTER; // Mengatur Posisi

            /** Membuat Judul GUI */
            String htmlTitle = "<html><center><h1>" + "Menu Admin" + "</h1></center></html>";
            JLabel label = new JLabel(htmlTitle);
            panel.add(label, gbc);

            /** Menampilkan GUI */
            choice = JOptionPane.showOptionDialog(null, panel, "Menu Admin", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            /** Mengatur Pilihan GUI */
            switch (choice) {
                case 0: tampilkanDataTiket(s); break;
                case 1: updateDataTiket(s); break;
                case 2: hapusDataTiket(s); break;
                case 3: updateDataAdmin(s); break;
            }
        } while (choice != 4);
        main(null);
    }

    /** Input Data Tiket */
    private static void inputDataTiket(Statement s) {
        try {
            /** Membuat Pilihan Tujuan dan Jam */
            String[] tujuanArray = {"Jakarta", "Surabaya", "Bali", "Medan", "Makassar"};
            String[] jamArray = {"08.00", "14.00", "18.00", "22.00"};
            String[] tanggalArray = new String[getTotalDate()];
            String[] kelasArray = {"VIP", "REGULER"};

            /** Membuat Array Tanggal */
            for (int i = 0; i < tanggalArray.length; i++) {
                tanggalArray[i] = Integer.toString(i + 1) + " " + getDate().split(" ")[1] + " " + getDate().split(" ")[2];
            }
            
            /** Membuat ComboBox Tujuan, Jam, dan Tanggal */
            JComboBox<String> tujuanComboBox = new JComboBox<>(tujuanArray);
            JComboBox<String> jamComboBox = new JComboBox<>(jamArray);
            JComboBox<String> tanggalComboBox = new JComboBox<>(tanggalArray);
            JComboBox<String> kelasComboBox = new JComboBox<>(kelasArray);
            
            /** Membuat Field Nama, Tanggal, Kelas, dan Jumlah */
            JTextField namaField = new JTextField();
            JTextField jumlahField = new JTextField();

            Object[] fields = {"Tujuan:", tujuanComboBox, "Jam:", jamComboBox, "Tanggal:", tanggalComboBox,
                    "Nama:", namaField, "Kelas:", kelasComboBox, "Jumlah:", jumlahField};

            /** Menampilkan GUI Input Data Tiket */
            int option = JOptionPane.showConfirmDialog(null, fields, "Input Data Tiket", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                /** Parsing Tujuan dan Jam */
                String tujuan = (String) tujuanComboBox.getSelectedItem();
                int jam = Integer.parseInt(((String) jamComboBox.getSelectedItem()).split("\\.")[0]);
                String tanggal = (String) tanggalComboBox.getSelectedItem();

                /** Mengecek Waktu Keberangkatan */
                checkWaktuKeberangkatan(jam, tanggal);

                int harga = 0;
                /** Mengecek Tujuan */
                switch (tujuan) {
                    case "Jakarta":
                        harga = 500000;
                        break;
                    case "Surabaya":
                        harga = 400000;
                        break;
                    case "Bali":
                        harga = 600000;
                        break;
                    case "Medan":
                        harga = 700000;
                        break;
                    case "Makassar":
                        harga = 800000;
                        break;
                    default:
                        /** Menampilkan Informasi GUI */
                        JOptionPane.showMessageDialog(null, "Pilihan tidak tersedia", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                }

                /** Parsing Nama, Kelas, dan Jumlah */
                String nama = namaField.getText();
                String kelas = (String) kelasComboBox.getSelectedItem();
                int jumlah = Integer.parseInt(jumlahField.getText());

                /** Harga Menyesuaikan Kelas */
                harga = kelas.toLowerCase().equals("vip") ? harga * 2 : harga;

                /** 
                 * Mengecek Data Tiket
                 * Jika Data Tiket Kosong, Maka Akan Kembali Ke Main Menu
                 */
                if (nama.equals("") || kelas.equals("") || jumlah == 0) {
                    /** Menampilkan Informasi GUI */
                    JOptionPane.showMessageDialog(null, "Data tidak boleh kosong", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String tanggal2 = getDate();
                /** Memasukkan Data Tiket ke Database */
                String sql = "INSERT INTO Tiket (Nama, Kelas, Tujuan, Tanggal_Pesan, Tanggal_Berangkat, Jam, Jumlah, Harga) VALUES ('" + nama + "', '" + kelas + "', '" + tujuan + "', '" + tanggal + "', '" + tanggal2 + "', '" + jam + "', '" + jumlah + "', '" + harga + "');";
                s.execute(sql);

                /** Membuat Pilihan untuk Menu GUI */
                String[] options = {"Pesan Tiket Lagi", "Kembali"};

                /** Menampilkan Informasi GUI */
                int choice = JOptionPane.showOptionDialog(null, "Tiket berhasil dipesan", "Berhasil",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                
                /** Mengatur Pilihan GUI */
                switch (choice) {
                    case 0:
                        inputDataTiket(s);
                        break;
                    case 1:
                        main(null);
                        break;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Menampilkan Data Tiket */
    private static void tampilkanDataTiket(Statement s) {
        try {
            /** Membuat ResultSet untuk Menampilkan Data Tiket */
            ResultSet r = s.executeQuery("SELECT * FROM Tiket;");

            /** Membuat List Kolom Untuk GUI */
            String[] columnNames = {"ID", "Nama", "Kelas", "Tujuan", "Tanggal Pesan", "Tanggal Berangkat", "Jam", "Jumlah", "Harga", "Total Harga"};
            Object[][] data = new Object[100][10]; // Membuat Array 2D untuk Menyimpan Data Tiket

            int i = 0;
            /** Memasukkan Data Tiket ke Array 2D */
            while (r.next()) {
                data[i][0] = r.getInt("ID");
                data[i][1] = r.getString("Nama");
                data[i][2] = r.getString("Kelas");
                data[i][3] = r.getString("Tujuan");
                data[i][4] = r.getString("Tanggal_Pesan");
                data[i][5] = r.getString("Tanggal_Berangkat");
                data[i][6] = r.getInt("Jam");
                data[i][7] = r.getInt("Jumlah");
                data[i][8] = r.getInt("Harga");
                data[i][9] = r.getInt("Jumlah") * r.getInt("Harga");
                i++;
            }

            Object[][] data2 = new Object[i][8];
            for (int j = 0; j < i; j++) {
                data2[j] = data[j];
            }

            /** Membuat Tabel untuk Menampilkan Data Tiket */
            JTable table = new JTable(data2, columnNames);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Auto Resize Column

            /** Membuat ScrollPane untuk Menampilkan Data Tiket */
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new java.awt.Dimension(1000, 400)); // Ukuran ScrollPane

            /** Membuat Panel untuk Judul */
            JPanel titlePanel = new JPanel();
            TitledBorder titledBorder = BorderFactory.createTitledBorder("LIST TIKET PESAWAT"); // Mengatur Judu;
            titledBorder.setTitleJustification(TitledBorder.CENTER); // Mengatur Posisi Judul
            titlePanel.setBorder(titledBorder); // Mengatur Border Judul

            /** Membuat Panel untuk Tabel */
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.add(scrollPane, BorderLayout.CENTER);

            /** Membuat Panel untuk Menggabungkan Panel Judul dan Panel Tabel */
            JPanel combinedPanel = new JPanel(new BorderLayout());
            combinedPanel.add(titlePanel, BorderLayout.NORTH);
            combinedPanel.add(tablePanel, BorderLayout.CENTER);

            table.setFillsViewportHeight(true); // Mengatur Tabel agar Memenuhi Viewport

            /** Menampilkan GUI */
            JOptionPane.showMessageDialog(null, combinedPanel, "Data Tiket", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Update Data Tiket */
    private static void updateDataTiket(Statement s) {
        try {
            /** Membuat Field ID */
            JTextField idField = new JTextField();
            Object[] fields = {"Masukkan ID Tiket yang ingin diupdate:", idField}; // Mengatur Field ID

            /** Menampilkan GUI */
            int option = JOptionPane.showConfirmDialog(null, fields, "Update Data Tiket", JOptionPane.OK_CANCEL_OPTION);

            /** 
             * Mengecek Pilihan GUI
             * Jika Pilihan GUI Bukan OK_OPTION, Maka Akan Kembali Ke Menu Admin
             */
            if (option != JOptionPane.OK_OPTION) {
                menuAdmin(s);
            }

            /** Parsing ID Tiket */
            int id = Integer.parseInt(idField.getText());
            
            /** Membuat List untuk Memilih Data Tiket yang ingin diupdate */
            String[] columnNames = {"Nama", "Kelas", "Tujuan", "Tanggal Berangkat", "Jam", "Jumlah", "Harga"};

            /** Menampilkan GUI */
            int pilihan = JOptionPane.showOptionDialog(null, "Pilih Data Tiket yang ingin diupdate", "Update Data Tiket",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, columnNames, columnNames[0]);
                    
            /** Mengatur Pilihan GUI */
            switch (pilihan) {
                case 0:
                    String nama = JOptionPane.showInputDialog(null, "Masukkan Nama Baru:", "Update Data Tiket", JOptionPane.PLAIN_MESSAGE);
                    String sql = "UPDATE Tiket SET Nama = '" + nama + "' WHERE ID = " + id + ";";
                    s.execute(sql);
                    break;
                case 1:
                    String[] kelasArray = {"VIP", "REGULER"};

                    /** Membuat ComboBox Kelas */
                    JComboBox<String> kelasComboBox = new JComboBox<>(kelasArray);

                    Object[] fields1 = {"Kelas:", kelasComboBox};

                    /** Menampilkan GUI Update Kelas */
                    option = JOptionPane.showConfirmDialog(null, fields1, "Update Kelas", JOptionPane.OK_CANCEL_OPTION);

                    /** Parsing Kelas */
                    String kelas = (String) kelasComboBox.getSelectedItem();

                    if (option == JOptionPane.OK_OPTION) {
                        sql = "UPDATE Tiket SET Kelas = '" + kelas + "' WHERE ID = " + id + ";";
                        s.execute(sql);
                    }
                    break;
                case 2:
                    String tujuan = JOptionPane.showInputDialog(null, "Masukkan Tujuan Baru:", "Update Data Tiket", JOptionPane.PLAIN_MESSAGE);
                    sql = "UPDATE Tiket SET Tujuan = '" + tujuan + "' WHERE ID = " + id + ";";
                    s.execute(sql);
                    break;
                case 3:
                    String[] tanggalArray = new String[getTotalDate()];

                    /** Membuat Array Tanggal */
                    for (int i = 0; i < tanggalArray.length; i++) {
                        tanggalArray[i] = Integer.toString(i + 1) + " " + getDate().split(" ")[1] + " " + getDate().split(" ")[2];
                    }
                    
                    /** Membuat ComboBox Tanggal */
                    JComboBox<String> tanggalComboBox = new JComboBox<>(tanggalArray);

                    Object[] fields2 = {"Tanggal:", tanggalComboBox};

                    /** Menampilkan GUI Update Tanggal */
                    option = JOptionPane.showConfirmDialog(null, fields2, "Update Tanggal", JOptionPane.OK_CANCEL_OPTION);
                    int tanggal = Integer.parseInt(((String) tanggalComboBox.getSelectedItem()).split(" ")[0]);

                    if (option == JOptionPane.OK_OPTION) {
                        sql = "UPDATE Tiket SET Tanggal_Berangkat = '" + tanggal + "' WHERE ID = " + id + ";";
                        s.execute(sql);
                    }
                    break;
                case 4:
                    String[] jamArray = {"08.00", "14.00", "18.00", "22.00"};

                    /** Membuat ComboBox Jam */
                    JComboBox<String> jamComboBox = new JComboBox<>(jamArray);

                    Object[] fields3 = {"Jam:", jamComboBox};

                    /** Menampilkan GUI Update Jam */
                    option = JOptionPane.showConfirmDialog(null, fields3, "Update Jam", JOptionPane.OK_CANCEL_OPTION);
                    int jam = Integer.parseInt(((String) jamComboBox.getSelectedItem()).split("\\.")[0]);

                    if (option == JOptionPane.OK_OPTION) {
                        sql = "UPDATE Tiket SET Jam = '" + jam + "' WHERE ID = " + id + ";";
                        s.execute(sql);
                    }
                    break;
                case 5:
                    String jumlah = JOptionPane.showInputDialog(null, "Masukkan Jumlah Baru:", "Update Data Tiket", JOptionPane.PLAIN_MESSAGE);
                    sql = "UPDATE Tiket SET Jumlah = '" + jumlah + "' WHERE ID = " + id + ";";
                    s.execute(sql);
                    break;
                case 6:
                    String harga = JOptionPane.showInputDialog(null, "Masukkan Harga Baru:", "Update Data Tiket", JOptionPane.PLAIN_MESSAGE);
                    sql = "UPDATE Tiket SET Harga = '" + harga + "' WHERE ID = " + id + ";";
                    s.execute(sql);
                    break;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Update Data Admin */
    private static void updateDataAdmin(Statement s) {
        try {
            /** Membuat Field Username & Password */
            JTextField usernameField = new JTextField();
            JTextField passwordField = new JTextField();
            JTextField newPasswordField = new JTextField();

            Object[] fields = {"Username", usernameField, "Password", passwordField, "Password Baru", newPasswordField};

            /** Menampilkan GUI */
            int option = JOptionPane.showConfirmDialog(null, fields, "Update Data Admin", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                /** Parsing Username & Password */
                String username = usernameField.getText();
                String password = passwordField.getText();
                String newPassword = newPasswordField.getText();

                /** 
                 * Mengecek Data Admin
                 * Jika Data Admin Kosong, Maka Akan Kembali Ke Main Menu
                 */
                if (username.equals("") || password.equals("") || newPassword.equals("")) {
                    JOptionPane.showMessageDialog(null, "Data tidak boleh kosong", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                /** Mengecek Data Admin */
                ResultSet r = s.executeQuery("SELECT * FROM Admin WHERE Username = '" + username + "' AND Password = '" + password + "';");

                /** Jika Data Admin Ditemukan, Maka Akan Masuk ke Menu Admin */
                if (r.next()) {
                    String sql = "UPDATE Admin SET Password = '" + newPassword + "' WHERE Username = '" + username + "';";
                    s.execute(sql);

                    /** Membuat Pilihan untuk Menu GUI */
                    String[] options = {"Kembali"};

                    /** Menampilkan Informasi GUI */
                    int choice = JOptionPane.showOptionDialog(null, "Data admin berhasil diupdate", "Berhasil",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                    
                    /** Mengatur Pilihan GUI */
                    switch (choice) {
                        case 0: menuAdmin(s); break;
                    }
                } else {
                    /** Membuat Pilihan untuk Menu GUI */
                    String[] options = {"Coba Lagi", "Kembali"};

                    /** Menampilkan Informasi GUI */
                    int choice = JOptionPane.showOptionDialog(null, "Username atau password salah", "Error",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                    
                    /** Mengatur Pilihan GUI */
                    switch (choice) {
                        case 0: updateDataAdmin(s); break;
                        case 1: menuAdmin(s); break;
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Hapus Data Tiket */
    private static void hapusDataTiket(Statement s) {
        try {
            /** Membuat ResultSet untuk Menampilkan Data Tiket */
            ResultSet r = s.executeQuery("SELECT * FROM Tiket;");

            /** Membuat List Kolom Untuk GUI */
            String[] columnNames = {"ID", "Nama", "Kelas", "Tujuan", "Tanggal", "Jam", "Jumlah", "Harga", "Total Harga"};
            Object[][] data = new Object[100][9]; // Membuat Array 2D untuk Menyimpan Data Tiket

            int i = 0;
            /** Memasukkan Data Tiket ke Array 2D */
            while (r.next()) {
                data[i][0] = r.getInt("ID");
                data[i][1] = r.getString("Nama");
                data[i][2] = r.getString("Kelas");
                data[i][3] = r.getString("Tujuan");
                data[i][4] = r.getString("Tanggal");
                data[i][5] = r.getInt("Jam");
                data[i][6] = r.getInt("Jumlah");
                data[i][7] = r.getInt("Harga");
                data[i][8] = r.getInt("Jumlah") * r.getInt("Harga");
                i++;
            }

            /** Menghapus Data Tiket yang Kosong */
            Object[][] data2 = new Object[i][8];
            for (int j = 0; j < i; j++) {
                data2[j] = data[j];
            }

            /** Membuat Tabel untuk Menampilkan Data Tiket */
            JTable table = new JTable(data2, columnNames);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Auto Resize Column
            table.setFillsViewportHeight(true); // Mengatur Tabel agar Memenuhi Viewport

            /** Membuat ScrollPane untuk Menampilkan Data Tiket */
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new java.awt.Dimension(800, 400)); // Ukuran ScrollPane

            /** Menampilkan GUI */
            JOptionPane.showMessageDialog(null, scrollPane, "Data Tiket", JOptionPane.PLAIN_MESSAGE);

            /** Membuat Field ID */
            JTextField idField = new JTextField();
            Object[] fields = {"Masukkan ID Tiket yang ingin dihapus:", idField}; // Mengatur Field ID

            /** Menampilkan GUI */
            int option = JOptionPane.showConfirmDialog(null, fields, "Hapus Data Tiket", JOptionPane.OK_CANCEL_OPTION);

            /** 
             * Mengecek Pilihan GUI
             * Jika Pilihan GUI Bukan OK_OPTION, Maka Akan Kembali Ke Menu Admin
             */
            if (option != JOptionPane.OK_OPTION) {
                menuAdmin(s);
            }

            /** Parsing ID Tiket */
            int id = Integer.parseInt(idField.getText());

            /** Menghapus Data Tiket dari Database */
            String sql = "DELETE FROM Tiket WHERE ID = " + id + ";";
            s.execute(sql);

            /** Membuat Pilihan untuk Menu GUI */
            String[] options = {"Hapus Tiket Lagi", "Kembali"};

            /** Menampilkan Informasi GUI */
            int choice = JOptionPane.showOptionDialog(null, "Tiket berhasil dihapus", "Berhasil",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            
            /** Mengatur Pilihan GUI */
            switch (choice) {
                case 0: hapusDataTiket(s); break;
                case 1: main(null); break;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } 
    }
}
