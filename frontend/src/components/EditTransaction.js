// components/EditTransaction.js
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

const EditTransaction = () => {
    const { id } = useParams();
    const [transaction, setTransaction] = useState({ price: '', quantity: '', status: '' });
    const navigate = useNavigate();

    useEffect(() => {
        fetchTransaction();
    }, []);

    const fetchTransaction = async () => {
        const response = await fetch(`http://localhost:8080/api/transactions/${id}`);
        const data = await response.json();
        setTransaction(data);
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setTransaction((prev) => ({ ...prev, [name]: value }));
    };

    const updateTransaction = async (e) => {
        e.preventDefault();
        await fetch(`http://localhost:8080/api/transactions/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(transaction),
        });
        navigate('/transactions'); // Redirect to transactions list after updating
    };

    return (
        <div>
            <h2>Edit Transaction</h2>
            <form onSubmit={updateTransaction}>
                <input name="price" placeholder="Price" value={transaction.price} onChange={handleChange} required />
                <input name="quantity" placeholder="Quantity" value={transaction.quantity} onChange={handleChange} required />
                <input name="status" placeholder="Status" value={transaction.status} onChange={handleChange} required />
                <button type="submit">Update Transaction</button>
            </form>
        </div>
    );
};

export default EditTransaction;
