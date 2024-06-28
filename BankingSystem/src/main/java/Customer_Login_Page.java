import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Customer_Login_Page")
public class Customer_Login_Page extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String PASSWORD_QUERY = "SELECT username, email FROM password_management WHERE username=? AND password=?";
    private static final String CUSTOMER_QUERY = "SELECT * FROM admin_dashboard WHERE full_name=? AND email=?";
    private static final String INSERT_QUERY = "INSERT INTO customer_login (full_name, address, account, mobile_no, email, account_type, balance, id_proff_number, dob) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public Customer_Login_Page() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Connection conn = null;
        PreparedStatement passwordStmt = null;
        PreparedStatement customerStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet passwordRs = null;
        ResultSet customerRs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingsystem", "root", "BankingSystem@12");

            // Check credentials in password_management table
            passwordStmt = conn.prepareStatement(PASSWORD_QUERY);
            passwordStmt.setString(1, username);
            passwordStmt.setString(2, password);
            passwordRs = passwordStmt.executeQuery();

            if (passwordRs.next()) {
                // Username and password are correct, retrieve email
                String email = passwordRs.getString("email");

                // Fetch customer details from admin_dashboard
                customerStmt = conn.prepareStatement(CUSTOMER_QUERY);
                customerStmt.setString(1, username);
                customerStmt.setString(2, email);
                customerRs = customerStmt.executeQuery();

                if (customerRs.next()) {
                    // Retrieve customer details
                    String fullName = customerRs.getString("full_name");
                    String address = customerRs.getString("address");
                    String account = customerRs.getString("account");
                    String mobileNo = customerRs.getString("mobile_no");
                    String accountType = customerRs.getString("account_type");
                    double balance = customerRs.getDouble("balance");
                    String dobStr = customerRs.getString("dob");
                    String id_proff_number = customerRs.getString("id_proff_number");
                    String id_proff = customerRs.getString("id_proff");

                    // Convert dob string to Date object
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date dob = sdf.parse(dobStr);

                    // Insert into customer_login table
                    insertStmt = conn.prepareStatement(INSERT_QUERY);
                    insertStmt.setString(1, fullName);
                    insertStmt.setString(2, address);
                    insertStmt.setString(3, account);
                    insertStmt.setString(4, mobileNo);
                    insertStmt.setString(5, email);
                    insertStmt.setString(6, accountType);
                    insertStmt.setDouble(7, balance);
                    insertStmt.setString(8, id_proff);
                    insertStmt.setDate(9, new java.sql.Date(dob.getTime()));
                    insertStmt.executeUpdate();

                    // Encode customer details into URL
                    String encodedFullName = URLEncoder.encode(fullName, "UTF-8");
                    String encodedAccount = URLEncoder.encode(account, "UTF-8");
                    String encodedAddress = URLEncoder.encode(address, "UTF-8");
                    String encodedMobileNo = URLEncoder.encode(mobileNo, "UTF-8");
                    String encodedEmail = URLEncoder.encode(email, "UTF-8");
                    String encodedAccountType = URLEncoder.encode(accountType, "UTF-8");
                    String encodedBalance = URLEncoder.encode(String.valueOf(balance), "UTF-8");
                    String encodedIdProff = URLEncoder.encode(id_proff, "UTF-8");
                    String encodedIdProffNumber = URLEncoder.encode(id_proff_number, "UTF-8");
                    String encodedDob = URLEncoder.encode(dobStr, "UTF-8");

                    // Redirect to customer dashboard with customer details in URL
                    response.sendRedirect("Customer_Dashboard.html?fullName=" + encodedFullName + "&account=" + encodedAccount + "&address=" + encodedAddress + "&mobileNo=" + encodedMobileNo + "&email=" + encodedEmail + "&accountType=" + encodedAccountType + "&balance=" + encodedBalance + "&id_proff=" + encodedIdProff + "&id_proff_number=" + encodedIdProffNumber + "&dob=" + encodedDob);

                } else {
                    out.println("Error: Customer details not found");
                }
            } else {
            	response.setContentType("text/html");
            	PrintWriter out1 = response.getWriter();
            	out1.println("<!DOCTYPE html>");
            	out1.println("<html>");
            	out1.println("<head>");
            	out1.println("<title>Error</title>");
            	out1.println("<script src=\"https://cdn.jsdelivr.net/npm/sweetalert2@11\"></script>");
            	out1.println("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css\">");
            	out1.println("</head>");
            	out1.println("<body>");
            	out1.println("<script>");
            	out1.println("Swal.fire({");
            	out1.println("  title: 'Error',");
            	out1.println("  text: 'Invalid username or password',");
            	out1.println("  icon: 'error'");
            	out1.println("}).then(() => { window.location.href='index.html'; });");
            	out1.println("</script>");
            	out1.println("</body>");
            	out1.println("</html>");
            	out1.close();
    }
        } catch (ClassNotFoundException | SQLException | ParseException e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (passwordRs != null) passwordRs.close();
                if (customerRs != null) customerRs.close();
                if (passwordStmt != null) passwordStmt.close();
                if (customerStmt != null) customerStmt.close();
                if (insertStmt != null) insertStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
