import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/TransactionServlet")
public class TransactionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/bankingsystem";
    private static final String JDBC_USER = "your_username";
    private static final String JDBC_PASSWORD = "your_password";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("deposit".equals(action)) {
            handleDeposit(request, response);
        } else if ("withdraw".equals(action)) {
            handleWithdraw(request, response);
        }
    }

    private void handleDeposit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String depositAmountStr = request.getParameter("deposit_amount");
        double depositAmount = Double.parseDouble(depositAmountStr);

        // Your database logic to record deposit transaction here
        // Sample code to illustrate database interaction
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingsystem", "root", "BankingSystem@12")) {
            String sql = "INSERT INTO transactions (account, transaction_type, amount) VALUES (?, 'deposit', ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, request.getParameter("accountno"));
                statement.setDouble(2, depositAmount);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ServletException("Database operation failed", e);
        }

        PrintWriter out = response.getWriter();
        out.println("Deposit of " + depositAmount + " successful");
    }

    private void handleWithdraw(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNo = request.getParameter("accountno");
        String withdrawAmountStr = request.getParameter("withdraw_amount");
        double withdrawAmount = Double.parseDouble(withdrawAmountStr);
        String password = request.getParameter("password");

        // Your database logic to verify password and record withdrawal transaction here
        // Sample code to illustrate database interaction
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingsystem", "root", "BankingSystem@12")) {
            // Verify password
            String verifyPasswordSql = "SELECT * FROM admin_dashboard WHERE account = ? AND password = ?";
            try (PreparedStatement verifyStatement = connection.prepareStatement(verifyPasswordSql)) {
                verifyStatement.setString(1, accountNo);
                verifyStatement.setString(2, password);
                if (!verifyStatement.executeQuery().next()) {
                    PrintWriter out = response.getWriter();
                    out.println("Invalid account number or password");
                    return;
                }
            }

            // Record withdrawal transaction
            String withdrawSql = "INSERT INTO transactions (account, transaction_type, amount) VALUES (?, 'withdraw', ?)";
            try (PreparedStatement withdrawStatement = connection.prepareStatement(withdrawSql)) {
                withdrawStatement.setString(1, accountNo);
                withdrawStatement.setDouble(2, withdrawAmount);
                withdrawStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ServletException("Database operation failed", e);
        }

        PrintWriter out = response.getWriter();
        out.println("Withdrawal of " + withdrawAmount + " successful");
    }
}
