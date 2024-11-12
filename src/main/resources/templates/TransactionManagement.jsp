<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Transaction Management System</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1, h2 { color: #333; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 8px 12px; border: 1px solid #ddd; text-align: left; }
        th { background-color: #f2f2f2; }
        form { margin-bottom: 20px; }
        label { display: block; margin-top: 10px; }
    </style>
</head>
<body>
<h1>Transaction Management System</h1>

<!-- Section for Adding a New Transaction -->
<h2>Add New Transaction</h2>
<form id="addTransactionForm" action="/api/transactions" method="POST">
    <label>Transaction ID: <input type="text" name="transactionId" required /></label>
    <label>Symbol: <input type="text" name="symbol" required /></label>
    <label>Asset Class: <input type="text" name="assetClass" required /></label>
    <label>Quantity: <input type="number" name="quantity" required /></label>
    <label>Price: <input type="number" name="price" required /></label>
    <label>Strategy: <input type="text" name="strategy" required /></label>
    <label>Status:
        <select name="status" required>
            <option value="PENDING">Pending</option>
            <option value="COMPLETED">Completed</option>
            <option value="REJECTED">Rejected</option>
        </select>
    </label>
    <button type="submit">Add Transaction</button>
</form>

<!-- Section for Viewing All Transactions -->
<h2>All Transactions</h2>
<table>
    <thead>
    <tr>
        <th>Transaction ID</th>
        <th>Symbol</th>
        <th>Asset Class</th>
        <th>Quantity</th>
        <th>Price</th>
        <th>Strategy</th>
        <th>Status</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="transaction" items="${transactions}">
        <tr>
            <td>${transaction.transactionId}</td>
            <td>${transaction.symbol}</td>
            <td>${transaction.assetClass}</td>
            <td>${transaction.quantity}</td>
            <td>${transaction.price}</td>
            <td>${transaction.strategy}</td>
            <td>${transaction.status}</td>
            <td>
                <button onclick="editTransaction('${transaction.transactionId}')">Edit</button>
                <button onclick="deleteTransaction('${transaction.transactionId}')">Delete</button>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<!-- Script for Handling Edit and Delete Actions -->
<script>
    function editTransaction(transactionId) {
        const url = `/api/transactions/${transactionId}`;
        fetch(url, { method: 'GET' })
            .then(response => response.json())
            .then(data => {
                document.querySelector('input[name="transactionId"]').value = data.transactionId;
                document.querySelector('input[name="symbol"]').value = data.symbol;
                document.querySelector('input[name="assetClass"]').value = data.assetClass;
                document.querySelector('input[name="quantity"]').value = data.quantity;
                document.querySelector('input[name="price"]').value = data.price;
                document.querySelector('input[name="strategy"]').value = data.strategy;
                document.querySelector('select[name="status"]').value = data.status;
            });
    }

    function deleteTransaction(transactionId) {
        const url = `/api/transactions/${transactionId}`;
        fetch(url, { method: 'DELETE' })
            .then(response => {
                if (response.ok) {
                    alert('Transaction deleted');
                    location.reload();
                } else {
                    alert('Failed to delete transaction');
                }
            });
    }
</script>
</body>
</html>
