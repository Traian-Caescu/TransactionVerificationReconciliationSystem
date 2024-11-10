// components/Dashboard.js
import React from 'react';
import { Link } from 'react-router-dom';

function Dashboard() {
    return (
        <div>
            <h1>Dashboard</h1>
            <ul>
                <li><Link to="/transactions">View Transactions</Link></li>
                <li><Link to="/transactions/add">Add New Transaction</Link></li>
            </ul>
        </div>
    );
}

export default Dashboard;
