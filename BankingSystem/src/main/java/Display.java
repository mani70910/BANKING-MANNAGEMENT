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

@WebServlet("/DisplayData")
public class Display extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String SELECT_QUERY = "SELECT * FROM admin_dashboard";

    public Display() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        response.setContentType("text/html");

        pw.println("<!DOCTYPE html>");
        pw.println("<html>");
        pw.println("<head>");
        pw.println("<meta charset=\"UTF-8\">");
        pw.println("<title>Display Data</title>");
        pw.println("<style>");
        pw.println("table { border-collapse: collapse; width: 100%; }");
        pw.println("th, td { border: 1px solid #dddddd; text-align: left; padding: 12px; }");
        pw.println("th { background-color: #f2f2f2; font-weight: bold; }"); // Bold font for table headers
        pw.println("tr:nth-child(even) { background-color: #f8f8f8; }");
        pw.println("</style>");
        pw.println("</head>");
        pw.println("<body>");
        pw.println("<h2 style=\"text-align:center;\">Existing Data in Admin Dashboard</h2>");
        pw.println("<table>");
        pw.println("<tr><th>Full Name</th><th>Address</th><th>Mobile Number</th><th>Email</th><th>Account Type</th><th>Account No</th><th>ID Proof</th><th>ID Proof Number</th><th>Date of Birth</th></tr>");

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingsystem", "root", "BankingSystem@12");
             PreparedStatement ps = con.prepareStatement(SELECT_QUERY);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                pw.println("<tr>");
                pw.println("<td>" + rs.getString("full_name") + "</td>");
                pw.println("<td>" + rs.getString("address") + "</td>");
                pw.println("<td>" + rs.getString("mobile_no") + "</td>");
                pw.println("<td>" + rs.getString("email") + "</td>");
                pw.println("<td>" + rs.getString("account_type") + "</td>");
                pw.println("<td>" + rs.getString("account") + "</td>");
                pw.println("<td>" + rs.getString("id_proff") + "</td>");
                pw.println("<td>" + rs.getString("id_proff_number") + "</td>");
                pw.println("<td>" + rs.getString("dob") + "</td>");
                pw.println("</tr>");
            }
        } catch (SQLException se) {
            pw.println(se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            pw.println(e.getMessage());
            e.printStackTrace();
        }

        pw.println("</table>");
        pw.println("</body>");
        pw.println("</html>");
    }
}
