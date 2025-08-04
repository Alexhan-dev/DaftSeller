package wtf.alexhanwow.Costum;

import java.sql.*;
import java.util.*;

public class Goods {

    // 数据库连接信息
    public static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/goods";
    public static final String DB_USER = "user";
    public static final String DB_PASSWORD = "asdf1122";
    public static final String TABLE_NAME = "items";

    private static String SonOfABitch = "";
    private static Statement stmt = null;

    private static String FuckYou = "";
    private static PreparedStatement Nigger = null;

    public static Map count() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Map<String, Integer> goods = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // 获取表的所有列名
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, TABLE_NAME, null);

            // 构建动态SQL
            StringBuilder sqlBuilder = new StringBuilder("SELECT ");
            int columnCount = 0;

            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");

                if (columnCount++ > 0) sqlBuilder.append(", ");

                // 字符串类型使用特殊处理 - 修正后的表达式
                if (dataType.matches("CHAR|VARCHAR|TEXT|BLOB")) {
                    // 正确嵌套的NULLIF表达式
                    sqlBuilder.append("COUNT(NULLIF(TRIM(")
                            .append(columnName)
                            .append("), '')) AS ")
                            .append(columnName);
                } else {
                    // 非字符串类型
                    sqlBuilder.append("COUNT(")
                            .append(columnName)
                            .append(") AS ")
                            .append(columnName);
                }
            }

            if (columnCount == 0) {
                System.out.println("表中没有找到任何列");
                return null;
            }

            sqlBuilder.append(" FROM ").append(TABLE_NAME);
            String sql = sqlBuilder.toString();
            System.out.println("执行SQL: " + sql); // 调试输出

            // 执行查询
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                if (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = rs.getMetaData().getColumnName(i);
                        int count = rs.getInt(i);
                        goods.put(columnName, count);
                    }
                }
            }

            // 输出最终结果
            /*System.out.println("\n各列有效值统计结果:");
            for (Map.Entry<String, Integer> entry : goods.entrySet()) {
                System.out.printf("%-20s %d%n", entry.getKey(), entry.getValue());
            }*/
            return goods;
        } catch (SQLException e) {
            System.err.println("数据库操作出错:");
            e.printStackTrace();
        }
        return null;

    }
    public static int catCount(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // 方法一：使用DatabaseMetaData获取列数
            int columnCount = getColumnCountUsingMetaData(conn, "items");
            return columnCount;
        }catch (SQLException e) {
            System.err.println("数据库操作出错:");
            e.printStackTrace();
        }
        return 0;
    }
        private static int getColumnCountUsingMetaData(Connection conn, String tableName) throws SQLException {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
                int count = 0;
                while (columns.next()) {
                    count++;
                }
                return count;
            }
        }

    public static int addCat(String CatName,String howMuch,String Details){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String sql = "ALTER TABLE `items` ADD `"+ CatName +"` CHAR(255);";

            executeAlterStatement(sql, "基本列 '" + CatName + "'",Double.parseDouble(howMuch),CatName,Details);
            return 1;
    }

    // 通用方法：执行ALTER语句
    private static void executeAlterStatement(String sql, String description,Double howMuch,String CatName ,String Details) {


        String SqlG;
        if (Details == null){
            SqlG = "INSERT INTO `goods` (`Cat` , `howMuch` , `detail`) VALUES ('"+ CatName +"' , '"+ howMuch +"','这里并没有商品描述')";
        }else {
            SqlG= "INSERT INTO `goods` (`Cat` , `howMuch` , `detail`) VALUES ('"+ CatName +"' , '"+ howMuch +"' , '"+ Details +"')";
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            // 检查列是否已存在
            if (!columnExists(conn, TABLE_NAME, sql.split(" ")[4])) {
                int result = stmt.executeUpdate(sql);
                System.out.println(description + " 添加成功!");
            } else {
                System.out.println(description + " 已存在，跳过添加");
            }

        } catch (SQLException e) {
            handleSQLException(e, sql);
        }
        //添加金额
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            // 检查列是否已存在
            if (!columnExists(conn, TABLE_NAME, sql.split(" ")[4])) {
                int result = stmt.executeUpdate(SqlG);
                System.out.println(description + " 添加成功!");
            } else {
                System.out.println(description + " 已存在，跳过添加");
            }

        } catch (SQLException e) {
            handleSQLException(e, sql);
        }
    }
    // 检查列是否已存在
    private static boolean columnExists(Connection conn, String tableName, String columnName) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet columns = meta.getColumns(null, null, tableName, columnName);
            return columns.next(); // 如果找到列则返回true
        } catch (SQLException e) {
            System.err.println("检查列存在时出错: " + e.getMessage());
            return false;
        }
    }

    // 处理SQL异常
    private static void handleSQLException(SQLException e, String sql) {
        System.err.println("执行SQL时出错: " + sql);
        System.err.println("错误代码: " + e.getErrorCode());
        System.err.println("SQL状态: " + e.getSQLState());
        System.err.println("错误信息: " + e.getMessage());

        // 处理常见错误
        if (e.getErrorCode() == 1060) {
            System.err.println("错误原因: 列已存在");
        } else if (e.getErrorCode() == 1146) {
            System.err.println("错误原因: 表不存在");
        } else if (e.getErrorCode() == 1054) {
            System.err.println("错误原因: 未知列");
        }
    }
    public static int addGood(String Cat,String Goods){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            String sql = "INSERT INTO `items` (`"+ Cat +"`) VALUES ('"+ Goods +"')";

            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println(rowsAffected + " 行数据已插入");
            return 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int changeAmount(String CatName,Double Amount){
        //连接数据库
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try(Connection conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD)) {
            // 执行查询
            //实例化Statement对象
            Statement stmt = conn.createStatement();
            String sql;
            sql = "UPDATE `goods` SET `howMuch` = ? WHERE `Cat` = ?";//向login表里修改数据
            //注：几个问号几个ps.setString，上面的语句中有两个?,所以下面有两个ps.setString
            PreparedStatement ps = conn.prepareStatement(sql);//修改数据预处理
            ps.setDouble(1, Amount);//第1个问号的值"222222"
            ps.setString(2, CatName);//第2个问号的值"123"
            ps.executeUpdate();//执行修改数据
            // 完成后关闭
            ps.close();
            stmt.close();
            conn.close();
            return 1;
        }catch (SQLException e ){
            e.printStackTrace();
            return 0;
        }
    }
    public static double getAmount(String Cat,String Number){
        int count = 0;
        double howMuch = 0;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            // 执行SQL查询（只统计非空值）
            String sql = "SELECT COUNT("+Cat+") AS total_count FROM items";
            ResultSet rs = stmt.executeQuery(sql);

            // 获取结果
            if (rs.next()) {
                count = rs.getInt("total_count");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (count > Integer.parseInt(Number)){
            return 0;
        }else {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                // 准备SQL查询（防止SQL注入）
                String sql = "SELECT howMuch FROM goods WHERE Cat = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    // 设置查询参数
                    pstmt.setString(1, Cat);

                    // 执行查询
                    try (ResultSet rs = pstmt.executeQuery()) {
                        int resultCount = 0;
                        // 遍历所有结果
                        while (rs.next()) {
                            howMuch = rs.getDouble("howMuch");
                            resultCount++;
                        }

                        // 处理无结果的情况
                        if (resultCount == 0) {
                            return 0;
                        } else {
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Double.parseDouble(Number) * howMuch;
        }

    }




    public static boolean isAdmin(String UserID) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        // SQL查询语句
        String sql = "SELECT COUNT(*) FROM `role` WHERE UserID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置查询参数
            pstmt.setString(1, UserID);

            // 执行查询
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String deleteCat(String CatName) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            return e.toString();
        }
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            return e.toString();
        }
        // 执行删除列的SQL语句
             SonOfABitch ="ALTER TABLE items DROP COLUMN "+ CatName;
        try {
            Connection conn2 = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            return e.toString();
        }
        // 准备SQL删除语句
            String sql = "DELETE FROM goods WHERE Cat = ?";

        try {
            Nigger = conn.prepareStatement(sql);
        } catch (SQLException e) {
            return e.toString();
        }
        // 设置查询参数
        try {
            Nigger.setString(1, CatName);
        } catch (SQLException e) {
            return e.toString();
        }
        return "OK";
    }

    public static String Confirm(){
        try {
            stmt.executeUpdate(SonOfABitch);
            Nigger.executeUpdate();
            return "Success";
        } catch (SQLException e) {
            return "没有事情需要确认，或确认出现问题";
        }

    }

    public static int deleteGoods(String goods,String CatName) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            int updatedRows = updateColumnToEmpty(conn, "items", CatName, CatName, goods);
            System.out.println("成功更新 " + updatedRows + " 条记录");
        } catch (SQLException e) {
            System.err.println("数据库操作错误: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
    private static int updateColumnToEmpty(Connection conn, String tableName,
                                           String columnToUpdate, String conditionColumn,
                                           String conditionValue) throws SQLException {
        // 安全构建SQL语句（使用预编译防止SQL注入）
        String sql = "UPDATE " + tableName +
                " SET " + columnToUpdate + " = ? " +
                " WHERE " + conditionColumn + " = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 设置要更新的值（空字符串）
            pstmt.setString(1, "");

            // 设置条件值
            pstmt.setString(2, conditionValue);

            return pstmt.executeUpdate();
        }
    }

}
