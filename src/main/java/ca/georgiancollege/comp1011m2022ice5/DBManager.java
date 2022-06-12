package ca.georgiancollege.comp1011m2022ice5;


import javafx.scene.chart.XYChart;

import java.sql.*;
import java.util.ArrayList;

/*Singleton

 */
public class DBManager {
    /***************************************************************************************************/
    //Step 1. -  private static instance member
    private static DBManager m_instance = null;

    //Step 2. - Make the default constructor private
    private DBManager() {
    }

    //Step 3. - Create an public static access method that returns an instance of the class
    public static DBManager Instance()
    {
        // step 4. -ensure that your instance member variable is null
        if (m_instance == null) {

            //Step 5. - Instatantiate a new DBManager instance
            m_instance = new DBManager();
        }
        return m_instance;

    }
/***************************************************************************************************/
private String m_user = "root";
private String m_password = "1999@Deep";
    private String m_connectURL = "jdbc:mysql://localhost:3306/comp1011m2022";

    /**this method insert  a vector2D OBJECT TO THE DATABASE
     *
     * @param vector2D
     * @return
     * @throws SQLException
     */
    public int insertVector2D(Vector2D vector2D) throws SQLException {
        int vectorID = -1;
        // initializing the result set object
        ResultSet resultSet = null;


        // create a query string
        String sql = "INSERT INTO vectors(X, Y) VALUES(?, ?);";
                try
                        (
                                /* head of try/ catch block */
                                Connection connection = DriverManager.getConnection(m_connectURL, m_user, m_password);
                         PreparedStatement statement = connection.prepareStatement(sql, new String[] {"vectorID"});
                        )
                {

                   // Configure prepared statement
                   statement.setFloat(1, vector2D.getX());
                    statement.setFloat(2, vector2D.getY());

                    // run the command on the Database

                    statement.executeUpdate();
                    //get the VetorID

                    resultSet = statement.getGeneratedKeys();
                    while (resultSet.next())
                    {
                        // get the vectorID from the Database
                        vectorID = resultSet.getInt(1);
                    }
                }
                catch(Exception e)
        {
                    e.printStackTrace();

    }
                finally {
                    if (resultSet != null)
                    {
                        resultSet.close();
                    }
                }

                return vectorID;
    }

    /**
     * This method reads the vectors table from the MySQL database
     * and returns an ArrayList of Vector2D type
     * @return
     */
    public ArrayList<Vector2D> readVectorTable()
    {
        // Instantiates an ArrayList collection of type Vector2D (empty container)
        ArrayList<Vector2D> vectors = new ArrayList<Vector2D>();

        String sql = "SELECT vectors.vectorID, X, Y FROM vectors GROUP BY vectors.vectorID";

        try
                (
                        Connection connection = DriverManager.getConnection(m_connectURL, m_user, m_password);
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                )
        {
            // while there is another record...loop
            while(resultSet.next())
            {
                // deserialize (decode) the data from database table
                int vectorID = resultSet.getInt("vectorID");
                float X = resultSet.getFloat("X");
                float Y = resultSet.getFloat("Y");

                // create an anonymous Vector2D object with the data from the database
                // then add the new Vector2D object to the vectors ArrayList
                vectors.add( new Vector2D(vectorID, X, Y));
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }

        return vectors;
    }

    /**
     * This method returns the Bar Chart Data from the Database
     * @return
     */
    public XYChart.Series<String, Float> getMagnitude()
    {
        // Step 1. Create a Series
        XYChart.Series<String, Float> magnitudes = new XYChart.Series<>();
        magnitudes.setName("2022");

        // Step 2. Get the Data from the Database
        ArrayList<Vector2D> vectors = readVectorTable();

        // mock data example
        /*
        magnitudes.getData().add(new XYChart.Data<>("0, 0", 0.0f));
        magnitudes.getData().add(new XYChart.Data<>("10, 20", 10.0f));
        magnitudes.getData().add(new XYChart.Data<>("30, 40", 20.0f));
        magnitudes.getData().add(new XYChart.Data<>("50, 80", 30.0f));
         */

        // Step 3. For Each Vector in the Data...Loop and add it to the Series
        for (var vector : vectors)
        {
            var chartData = new XYChart.Data<>(vector.toOneDecimalString(), vector.getMagnitude());
            magnitudes.getData().add(chartData);
        }

        return magnitudes;
    }
}