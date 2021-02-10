package me.braydon.example;

import me.braydon.database.impl.mysql.MySQLDatabase;
import me.braydon.database.impl.mysql.MySQLProperties;
import me.braydon.database.impl.mysql.MySQLRepository;
import me.braydon.database.impl.mysql.data.Column;
import me.braydon.database.impl.mysql.data.Table;
import me.braydon.database.impl.mysql.data.impl.BooleanColumn;
import me.braydon.database.impl.mysql.data.impl.DoubleColumn;
import me.braydon.database.impl.mysql.data.impl.IntegerColumn;
import me.braydon.database.impl.mysql.data.impl.VarcharColumn;

import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Braydon
 */
public class MySQLExample {
    public static void main(String[] args) {
        // Connecting to MySQL using the provided host, port, username, password, and database name.
        // For the sake of testing, we have the debugging mode enabled using the #withDebugging method
        MySQLDatabase database = (MySQLDatabase) new MySQLDatabase().connect(new MySQLProperties(
                "127.0.0.1", 3306,
                "username",
                "password",
                "database").withDebugging());

        // Creating the Table object
        // First we define the columns that will be in our table, and then we define the primary key(s) of the table.
        // In this case, the "firstName" column will be our primary key
        Table table = new Table("people", new Column[] {
                new VarcharColumn("firstName", 255, false),
                new VarcharColumn("lastName", 255, false),
                new IntegerColumn("age", false),
                new DoubleColumn("bankAccount", false),
                new BooleanColumn("deceased", false)
        }, new String[] { "firstName" });

        // Fetching a MySQL repository and inserting the table we created
        // NOTE: Fetching a dummy repository for a database will return a
        //       new instance each time. It's best to keep a reference to
        //       the repository so it can be re-used in the future
        MySQLRepository repository = database.getDummyRepository();
        repository.executeQuery(table.getCreateQuery(true));

        // Inserting a person named "John Doe" into the table we created above
        // (this will not insert the person if it already exists)
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        repository.executeInsert("INSERT IGNORE INTO `people` " +
                "(`firstName`, `lastName`, `age`, `bankAccount`, `deceased`) VALUES " +
                "(?, ?, ?, ?, '0');", new Column[] {
                new VarcharColumn("firstName", "John"),
                new VarcharColumn("lastName", "Doe"),
                new IntegerColumn("age", threadLocalRandom.nextInt(5, 100)),
                new DoubleColumn("bankAccount", 0D)
        });

        // Updating John's bank account with a random number
        repository.executeInsert("UPDATE `people` SET `bankAccount` = ? WHERE `firstName` = ?;", new Column[] {
                new DoubleColumn("bankAccount", Math.floor(threadLocalRandom.nextDouble(100, 100_000))),
                new VarcharColumn("firstName", "John")
        });

        // Selecting all of the rows in the table we created above and printing the values out into the terminal
        repository.executeQuery("SELECT * FROM " + table.getName(), resultSet -> {
            try {
                while (resultSet.next()) {
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    int age = resultSet.getInt("age");
                    double bankAccount = resultSet.getDouble("bankAccount");
                    boolean deceased = resultSet.getBoolean("deceased");

                    System.out.println("--------------");
                    System.out.println("firstName = " + firstName);
                    System.out.println("lastName = " + lastName);
                    System.out.println("age = " + age);
                    System.out.println("bankAccount = " + bankAccount);
                    System.out.println("deceased = " + deceased);
                    System.out.println("--------------");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }
}