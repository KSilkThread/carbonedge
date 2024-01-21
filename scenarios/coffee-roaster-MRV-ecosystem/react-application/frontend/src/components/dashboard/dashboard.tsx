import { Box, Typography, Button, Grid, Paper } from '@mui/material';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
  } from 'chart.js';
  import { Bar } from 'react-chartjs-2';

  ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend
  );

export default function Dashboard() {
const labels = ['Januar', 'Februar', 'März', 'April', 'Mai', 'Juni', 'Juli', 'August', 'September', 'Oktober', 'November', 'Dezember'];

const data = {
  labels,
  datasets: [
    {
      label: 'Dataset 1',
      data: [1100, 1250, 350, 200, 220, 400, 600, 700, 900, 1100, 1700, 1900],
      backgroundColor: '#FD6916',
    }
  ],
};

const options = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top' as const,
      },
      title: {
        display: true,
        text: 'Chart.js Bar Chart',
      },
    },
  };


  return (
    <Box sx={{ flexGrow: 1, paddingX: 4 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', p: 2 }}>
                <Typography variant="h2" sx={{ fontWeight: 800 }}>
                    Carbon<span style={{ color: '#FD6916' }}>Edge</span> Dashboard
                </Typography>
                <Button variant="contained" style={{ backgroundColor: '#FD6916' }}>
                    Logout
                </Button>
            </Box>
            <Grid container spacing={8}>
            <Grid item xs={6}>
                <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column',  minHeight: "200px" }}>
                    <Typography variant="h6">Calibration Status</Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, my: 2 }}>
                        <Box sx={{ width: 90, height: 90, borderRadius: '50%', backgroundColor: 'green' }} />
                        <Typography variant="h6">Calibrated</Typography>
                        <Button variant="contained" style={{ backgroundColor: '#FD6916' }}>Calibrate</Button>
                    </Box>
                </Paper>
            </Grid>

                <Grid item xs={6}>
                    <Paper sx={{ p: 2, minHeight: "200px" }}>
                        <Typography variant="h6">CO2 Amount Released</Typography>
                        <Typography variant="h3">123 kg</Typography>
                    </Paper>
                </Grid>
                <Grid item xs={6}>
                    <Paper sx={{ p: 2, minHeight: "200px" }}>
                        <Typography variant="h6">Time Since Last Calibration</Typography>
                        <Typography variant="h3">67 days</Typography>
                    </Paper>
                </Grid>
                <Grid item xs={6}>
                    <Paper sx={{ p: 2, minHeight: "200px" }}>
                        <Typography variant="h6">Emissions by Month</Typography>
                        <Bar options={options} data={data}/>
                    </Paper>
                </Grid>
            </Grid>
        </Box>
  )
}
