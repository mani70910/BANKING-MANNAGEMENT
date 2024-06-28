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

@WebServlet("/BankingOperations")
public class BankingOperations extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // Retrieve form data
        String accountNumber = request.getParameter("accountNumber");
        String amount = request.getParameter("amount");
        String withdrawAmount = request.getParameter("withdrawAmount");
        String mobileNumber = request.getParameter("mobileNumber");
        
        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/bankingsystem";
        String user = "root";
        String passwordDB = "BankingSystem@12";
        
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            
            // Connect to the database
            Connection conn = DriverManager.getConnection(url, user, passwordDB);
            
            // Check if the account number and mobile number exist in admin_dashboard
            PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM admin_dashboard WHERE account = ? AND mobile_no = ?");
            checkStmt.setString(1, accountNumber);
            checkStmt.setString(2, mobileNumber);
            ResultSet checkResult = checkStmt.executeQuery();
            
            if (!checkResult.next()) {
                response.setContentType("text/html");
                PrintWriter out1 = response.getWriter();
                out1.println("<html><head>");
                out1.println("<script src=\"https://cdn.jsdelivr.net/npm/sweetalert2@11\"></script>");
                out1.println("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css\">");
                out1.println("<title>Error</title></head><body>");
                out1.println("<script>");
                out1.println("Swal.fire({");
                out1.println("  title: 'Error',");
                out1.println("  text: 'Error: Account number or mobile number does not exist!',");
                out1.println("  icon: 'error'");
                out1.println("}).then(() => { history.back(); });"); // Using history.back() to go back to the previous page
                out1.println("</script>");
                out1.println("</body></html>");
                out1.close();
                conn.close();
                return;
            }
            
            double balance = checkResult.getDouble("balance");
            
            // Prepare SQL statement based on deposit or withdrawal
            PreparedStatement pstmt;
            if (amount != null && !amount.isEmpty()) {
                // Deposit operation
                double depositAmount = Double.parseDouble(amount);
                pstmt = conn.prepareStatement("UPDATE admin_dashboard SET balance = balance + ? WHERE account = ?");
                pstmt.setDouble(1, depositAmount);
                pstmt.setString(2, accountNumber);
                
                // Insert transaction history record for deposit
                PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO transaction_history (account_number, transaction_type, amount, balance) VALUES (?, 'Deposit', ?, (SELECT balance FROM admin_dashboard WHERE account = ?) + ?)");
                insertStmt.setString(1, accountNumber);
                insertStmt.setDouble(2, depositAmount);
                insertStmt.setString(3, accountNumber);
                insertStmt.setDouble(4, depositAmount);
                insertStmt.executeUpdate();
            } else if (withdrawAmount != null && !withdrawAmount.isEmpty()) {
                // Withdrawal operation
                double withdrawAmt = Double.parseDouble(withdrawAmount);
                
                // Check if withdrawal amount is valid
                if (withdrawAmt > 0 && withdrawAmt <= balance) {
                    pstmt = conn.prepareStatement("UPDATE admin_dashboard SET balance = balance - ? WHERE account = ?");
                    pstmt.setDouble(1, withdrawAmt);
                    pstmt.setString(2, accountNumber);
                    
                    // Insert transaction history record for withdrawal
                    PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO transaction_history (account_number, transaction_type, amount, balance) VALUES (?, 'Withdrawal', ?, (SELECT balance FROM admin_dashboard WHERE account = ?) - ?)");
                    insertStmt.setString(1, accountNumber);
                    insertStmt.setDouble(2, withdrawAmt);
                    insertStmt.setString(3, accountNumber);
                    insertStmt.setDouble(4, withdrawAmt);
                    insertStmt.executeUpdate();
                } else {
                    // Invalid withdrawal amount
                    response.setContentType("text/html");
                    PrintWriter out1 = response.getWriter();
                    out1.println("<html><head>");
                    out1.println("<script src=\"https://cdn.jsdelivr.net/npm/sweetalert2@11\"></script>");
                    out1.println("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css\">");
                    out1.println("<title>Error</title></head><body>");
                    out1.println("<script>");
                    out1.println("Swal.fire({");
                    out1.println("  title: 'Error',");
                    out1.println("  text: 'Insufficient Balance!',");
                    out1.println("  icon: 'error'");
                    out1.println("}).then(() => { history.back(); });"); // Using history.back() to go back to the previous page
                    out1.println("</script>");
                    out1.println("</body></html>");
                    out1.close();
                    conn.close();
                    return;
                }
            } else {
                out.println("<h3>Error: Invalid Operation!</h3>");
                conn.close();
                return; // End processing
            }
            
            // Execute the update query
            int rowsAffected = pstmt.executeUpdate();
            
            // Check if any rows were affected
            response.setContentType("text/html");
            PrintWriter out2 = response.getWriter();
            out2.println("<html><head>");
            out2.println("<script src=\"https://cdn.jsdelivr.net/npm/sweetalert2@11\"></script>");
            out2.println("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css\">");
            out2.println("<title>Transaction Status</title></head><body>");
            out2.println("<script>");

            if (rowsAffected > 0) {
                out2.println("Swal.fire({");
                out2.println("  title: 'Success',");
                out2.println("  text: 'Transaction Successful!',");
                out2.println("  icon: 'success'");
                out2.println("}).then(() => { history.back(); });");
            } else {
                out2.println("Swal.fire({");
                out2.println("  title: 'Failed',");
                out2.println("  text: 'Transaction Failed! Please check your account details.',");
                out2.println("  icon: 'error'");
                out2.println("}).then(() => { history.back(); });");
            }

            out2.println("</script>");
            out2.println("</body></html>");
            out2.close();

            
            // Close the database connection
            conn.close();
        } catch (Exception e) {
            out.println("<h3>Exception occurred: " + e.getMessage() + "</h3>");
        }
    }
}
