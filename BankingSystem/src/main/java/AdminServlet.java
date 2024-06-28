import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/bankingsystem";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "BankingSystem@12"; // Replace with your actual database password

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "SELECT * FROM admin_dashboard";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Generate HTML table with the retrieved data
            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>Full Name</th><th>Address</th><th>Mobile Number</th><th>Email</th><th>Account Type</th><th>Actions</th></tr>");
            while (resultSet.next()) {
                out.println("<tr>");
                out.println("<td>" + resultSet.getInt("id") + "</td>");
                out.println("<td>" + resultSet.getString("full_name") + "</td>");
                out.println("<td>" + resultSet.getString("address") + "</td>");
                out.println("<td>" + resultSet.getString("mobile_no") + "</td>");
                out.println("<td>" + resultSet.getString("email") + "</td>");
                out.println("<td>" + resultSet.getString("account_type") + "</td>");
                out.println("<td><button onclick=\"deleteCustomer(" + resultSet.getInt("id") + ")\">Delete</button>");
                out.println("<button onclick=\"modifyCustomer(" + resultSet.getInt("id") + ")\">Modify</button></td>");
                out.println("</tr>");
            }
            out.println("</table>");

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response); // Handle POST requests the same way as GET requests
    }
}
