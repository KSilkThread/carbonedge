import { Typography, Box, Container } from '@mui/material';
import loginImage from '../../assets/icons8-hand-einsteckkarte-96.png';


export default function Login() {
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
                    Please insert your credentials chip
                </Typography>
                <Box
                    component="img"
                    sx={{
                        mt: 4,
                        maxHeight: { xs: 400, md: 300 }, 
                        maxWidth: { xs: 600, md: 450 },
                    }}
                    alt="Please insert your credentials chip"
                    src={loginImage}
                />
            </Box>
        </Container>
    );
}