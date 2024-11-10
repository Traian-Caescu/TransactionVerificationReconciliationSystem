import React, { useEffect, useState } from 'react';
import AddTransaction from './AddTransaction';

const Transactions = () => {
    const [transactions, setTransactions] = useState([]);

    useEffect(() => {
        fetchTransactions();
    }, []);

    const fetchTransactions = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/transactions');
            const data = await response.json();
            setTransactions(data);
        } catch (error) {
            console.error('Error fetching transactions:', error);
        }
    };

    const addTransaction = async (transaction) => {
        try {
            const response = await fetch('http://localhost:8080/api/transactions', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(transaction),
            });

            if (!response.ok) {
                throw new Error(`Server error: ${response.status}`);
            }

            const addedTransaction = await response.json();

            // Update the transactions state by appending the new transaction
            setTransactions((prevTransactions) => [...prevTransactions, addedTransaction]);
        } catch (error) {
            console.error('Error adding transaction:', error);
        }
    };

    const deleteTransaction = async (id) => {
        try {
            await fetch(`http://localhost:8080/api/transactions/${id}`, { method: 'DELETE' });
            fetchTransactions(); // Refresh the list after deletion
        } catch (error) {
            console.error('Error deleting transaction:', error);
        }
    };

    return (
        <div>
            <h2>Transactions</h2>
            <ul>
                {transactions.map((tx) => (
                    <li key={tx.transactionId}>
                        {tx.transactionId} - {tx.price} - {tx.quantity} - {tx.status}
                        <button onClick={() => deleteTransaction(tx.transactionId)}>Delete</button>
                    </li>
                ))}
            </ul>

            {/* Pass addTransaction as a prop named onAddTransaction */}
            <AddTransaction onAddTransaction={addTransaction} />
        </div>
    );
};

export default Transactions;
