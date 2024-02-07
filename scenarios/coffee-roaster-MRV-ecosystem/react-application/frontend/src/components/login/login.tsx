import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Typography, Box, Container } from '@mui/material';
import loginImage from '../../assets/icons8-hand-einsteckkarte-96.png';
import loginSuccessImage from '../../assets/loginsuccess.png';

export default function Login() {
    const navigate = useNavigate();
    const [socket, setSocket] = useState<any>(null);
    const [retryTimeout, setRetryTimeout] = useState<any>(null);
    const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false);
    const connectWebSocket = () => {
        
        if (socket) {
            socket.close();
        }

        const newSocket = new WebSocket('ws://127.0.0.1:1880/ws/loginstatus');

        newSocket.onmessage = (event) => {
            const data = JSON.parse(event.data);
            if (data.loggedIn) {
                setIsLoggedIn(true);
                setTimeout(() => {
                    navigate('/dashboard');
                }, 3000);
            }
        };

        newSocket.onopen = () => {
            console.log('WebSocket Connected');
            clearTimeout(retryTimeout);
        };

        newSocket.onerror = () => {
            console.log('WebSocket Error, retrying...');
            setRetryTimeout(setTimeout(connectWebSocket, 5000));
        };

        setSocket(newSocket);
    };

    useEffect(() => {
        connectWebSocket();

        return () => {
            if (socket) {
                socket.close();
            }
            clearTimeout(retryTimeout);
        };
    }, [navigate]);

    return (
        <Container component="main" maxWidth="lg" sx={{ height: '100vh' }}>
            <Box
                sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    height: '100%',
                }}
            >
                <Typography component="h1" variant="h1" sx={{ fontWeight: 800 }}>
                    Carbon<span style={{ color: '#FD6916' }}>Edge</span> Dashboard
                </Typography>
                <Typography component="h3" variant="h3" sx={{ mt: 1, fontWeight: 500 }}>
                {isLoggedIn ? 'Login Successful' : 'Please insert your credentials chip'}
                </Typography>
                <Box
                    component="img"
                    sx={{
                        mt: 4,
                        maxHeight: { xs: 400, md: 300 }, 
                        maxWidth: { xs: 600, md: 450 },
                    }}
                    alt={isLoggedIn ? 'Login Successful' : 'Please insert your credentials chip'}
                    src={isLoggedIn ? loginSuccessImage : loginImage}
                />
            </Box>
        </Container>
    );
}
