import database.DBConnection;
import org.hibernate.Session;

public class DatabaseFiller {
    public static void main(String[] args) {
        // TODO see https://howtodoinjava.com/security/java-aes-encryption-example/ for cipher
        System.out.println("Hello world !");


        DBConnection dbConnection = new DBConnection();
        Session session = dbConnection.getSessionFactory().getCurrentSession();
        session.close();
    }
}
