package wtf.alexhanwow;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class setAdmin {
    public static int set(String UserID,String Password){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String url = "jdbc:mysql://127.0.0.1:3306/goods";
        String username = "user";
        String password = "asdf1122";

        String sql = "INSERT INTO `role` (`UserID`, `Role` , `password`) VALUES (?, ? , ?);";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置参数
            pstmt.setString(1, UserID);
            pstmt.setString(2, "Admin");
            pstmt.setString(3,Password);

            pstmt.executeUpdate();
            System.out.println("加载成功");
            return 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }
}
