import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    public static Connection connect() {
        try {
            // 1. MySQL Driver එක Load කිරීම
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Database Connection එක (මෙහි student_system යනු ඔබේ Database නමයි)
            // ඔබේ MySQL Password එකක් තිබේ නම් "" වෙනුවට එය ඇතුළත් කරන්න
            String url = "jdbc:mysql://localhost:3306/student_system";
            String user = "root"; 
            String password = ""; 

            return DriverManager.getConnection(url, user, password);

        } catch (ClassNotFoundException e) {
            System.out.println("Driver Error: MySQL Connector JAR එක නැත!");
            return null;
        } catch (SQLException e) {
            System.out.println("Connection Error: Database එකට සම්බන්ධ විය නොහැක!");
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}