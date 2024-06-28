import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.sql.*;

@WebServlet("/Close_Account")
public class Close_Account extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Retrieving form data
        String fullName = request.getParameter("full_name");
        String accountNumber = request.getParameter("account_number");
        String mobileNumber = request.getParameter("mobile_number");

        // Establishing database connection
        Connection connection = null;
        try {
            connection = Database_Connector.connect();
        } catch (Database_Connector.DatabaseConnectionException e) {
            e.printStackTrace();
        }

        if (connection != null) {
            try {
                // Query to delete account if it exists and balance is 0
                String deleteAccountQuery = "DELETE FROM admin_dashboard WHERE full_name=? AND Account=? AND Mobile_no=? AND balance=0";
                PreparedStatement deleteAccountStatement = connection.prepareStatement(deleteAccountQuery);
                deleteAccountStatement.setString(1, fullName);
                deleteAccountStatement.setString(2, accountNumber);
                deleteAccountStatement.setString(3, mobileNumber);

                int rowsAffected = deleteAccountStatement.executeUpdate();

                if (rowsAffected > 0) {
                    // Account deleted successfully
                    out.println("<script>alert('Account deleted successfully.'); window.location.href='index.html';</script>");

                    // Query to delete username and email from password_management
                    String deletePasswordQuery = "DELETE FROM password_management WHERE username=? OR email=?";
                    PreparedStatement deletePasswordStatement = connection.prepareStatement(deletePasswordQuery);
                    deletePasswordStatement.setString(1, fullName);
                    deletePasswordStatement.setString(2, mobileNumber);

                    // Execute the delete query for password_management
                    deletePasswordStatement.executeUpdate();

                    // Closing delete password statement
                    deletePasswordStatement.close();
                } else {
                    // Account doesn't exist, balance is not 0, or other error occurred
                    out.println("<script>alert('Unable to delete account. Please check your details or ensure balance is 0.'); window.location.href='index.html';</script>");
                }

                // Closing delete account statement
                deleteAccountStatement.close();

            } catch (SQLException e) {
                out.println("<h2>Database error: " + e.getMessage() + "</h2>");
            } finally {
                // Closing database connection
                Database_Connector.closeConnection(connection);
            }
        } else {
            out.println("<h2>Failed to establish database connection.</h2>");
        }
    }
}