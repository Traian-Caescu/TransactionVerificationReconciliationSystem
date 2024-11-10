// App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import Transactions from './components/Transactions';
import AddTransaction from './components/AddTransaction';
import EditTransaction from './components/EditTransaction';

function App() {
    return (
        <Router>
            <div>
                <Routes>
                    <Route path="/" element={<Login />} />
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/transactions" element={<Transactions />} />
                    <Route path="/transactions/add" element={<AddTransaction />} />
                    <Route path="/transactions/edit/:id" element={<EditTransaction />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
