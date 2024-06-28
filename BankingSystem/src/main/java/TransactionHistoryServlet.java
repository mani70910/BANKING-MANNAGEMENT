import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/TransactionHistoryServlet")
public class TransactionHistoryServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/bankingsystem";
        String user = "root";
        String passwordDB = "BankingSystem@12";

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Connect to the database
            Connection conn = DriverManager.getConnection(url, user, passwordDB);

            // Retrieve the last row from the customer_login table
            PreparedStatement lastRowStmt = conn.prepareStatement("SELECT * FROM customer_login ORDER BY id DESC LIMIT 1");
            ResultSet lastRowRs = lastRowStmt.executeQuery();
            String accountNumber = "";

            if (lastRowRs.next()) {
                accountNumber = lastRowRs.getString("account");
            }

            // Retrieve transaction history for the last account
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM transaction_history WHERE account_number = ?");
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            // Write HTML response
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<title>Transaction History</title>");
            out.println("<style>");
            out.println("table { border-collapse: collapse; width: 100%; }");
            out.println("th, td { border: 1px solid #dddddd; text-align: left; padding: 8px; }");
            out.println("tr:nth-child(even) { background-color: #f2f2f2; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>Transaction History</h2>");
            out.println("<table>");
            out.println("<tr><th>Transaction ID</th><th>Transaction Type</th><th>Amount</th><th>Current Balance</th><th>Transaction Date</th></tr>");

            // Populate table rows with transaction data
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("transaction_id") + "</td>");
                out.println("<td>" + rs.getString("transaction_type") + "</td>");
                out.println("<td>" + rs.getDouble("amount") + "</td>");
                out.println("<td>" + rs.getDouble("balance") + "</td>");
                out.println("<td>" + rs.getTimestamp("transaction_date") + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</body>");
            out.println("</html>");

            // Close resources
            rs.close();
            pstmt.close();
            lastRowRs.close();
            lastRowStmt.close();
            conn.close();
        } catch (Exception e) {
            out.println("Exception occurred: " + e.getMessage());
        }
    }
}
