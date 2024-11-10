import React, { useState } from 'react';

const AddTransaction = ({ onAddTransaction }) => {
    const [newTransaction, setNewTransaction] = useState({
        transactionId: '',
        price: '',
        quantity: '',
        status: ''
    });

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setNewTransaction((prevTransaction) => ({
            ...prevTransaction,
            [name]: value,
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log("Attempting to add transaction:", newTransaction);
        console.log("onAddTransaction function:", onAddTransaction);

        if (typeof onAddTransaction === 'function') {
            onAddTransaction(newTransaction); // Call the function passed as a prop
            setNewTransaction({
                transactionId: '',
                price: '',
                quantity: '',
                status: ''
            }); // Clear the form fields
        } else {
            console.error('onAddTransaction is not a function');
        }
    };


    return (
        <div>
            <h3>Add New Transaction</h3>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    name="transactionId"
                    placeholder="Transaction ID"
                    value={newTransaction.transactionId}
                    onChange={handleInputChange}
                    required
                />
                <input
                    type="number"
                    name="price"
                    placeholder="Price"
                    value={newTransaction.price}
                    onChange={handleInputChange}
                    required
                />
                <input
                    type="number"
                    name="quantity"
                    placeholder="Quantity"
                    value={newTransaction.quantity}
                    onChange={handleInputChange}
                    required
                />
                <input
                    type="text"
                    name="status"
                    placeholder="Status"
                    value={newTransaction.status}
                    onChange={handleInputChange}
                    required
                />
                <button type="submit">Add Transaction</button>
            </form>
        </div>
    );
};

export default AddTransaction;
