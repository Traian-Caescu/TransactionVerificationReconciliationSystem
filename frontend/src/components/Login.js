import React, { useState } from 'react';

function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);

    const handleLogin = async (e) => {
        e.preventDefault();

        const credentials = btoa(`${username}:${password}`);
        try {
            const response = await fetch('http://localhost:8080/api/authenticate', {
                method: 'POST',
                headers: {
                    'Authorization': `Basic ${credentials}`,
                    'Content-Type': 'application/json',
                },
                credentials: 'include', // Ensures cookies are included in cross-origin requests
            });


            if (response.ok) {
                console.log('Login successful');
                setError(null);
                // Redirect to dashboard or handle login success
                window.location.href = "/dashboard";
            } else {
                setError('Invalid credentials');
            }

        } catch (error) {
            console.error('Error:', error);
            setError('Network error');
        }
    };



    return (
        <div>
            <h2>Login</h2>
            <form onSubmit={handleLogin}>
                <input
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="Username"
                    required
                />
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Password"
                    required
                />
                <button type="submit">Login</button>
            </form>
            {error && <p>{error}</p>}
        </div>
    );
}

export default Login;
