import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/CustomerDetailsServlet")
public class CustomerDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve login credentials from the login form
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Use the login credentials to fetch details from the admin_dashboard table
        Map<String, String> customerDetails = getCustomerDetails(username, password);

        // Generate HTML response with the customer details
        StringBuilder htmlResponse = new StringBuilder();
        htmlResponse.append("<html><head><title>Customer Details</title></head><body>");
        htmlResponse.append("<h1>Customer Details</h1>");
        htmlResponse.append("<ul>");
        for (Map.Entry<String, String> entry : customerDetails.entrySet()) {
            htmlResponse.append("<li>").append(entry.getKey()).append(": ").append(entry.getValue()).append("</li>");
        }
        htmlResponse.append("</ul>");
        htmlResponse.append("</body></html>");

        // Set content type and send response
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(htmlResponse.toString());
    }

    // Method to fetch customer details from the admin_dashboard table based on login credentials
    private Map<String, String> getCustomerDetails(String username, String password) {
        Map<String, String> customerDetails = new HashMap<>();

        try {
            // Establish connection to the database
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingsystem", "root", "UjjawalSah@12");

            // Prepare and execute SQL query
            String query = "SELECT * FROM admin_dashboard WHERE full_name=? AND dob=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            // Fetch data from result set
            if (resultSet.next()) {
                customerDetails.put("Full Name", resultSet.getString("full_name"));
                customerDetails.put("Date of Birth", resultSet.getString("dob"));
                // Add more details as needed
            }

            // Close connections
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return customerDetails;
    }
}
