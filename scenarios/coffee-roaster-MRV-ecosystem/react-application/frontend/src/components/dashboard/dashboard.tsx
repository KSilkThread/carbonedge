import { Box, Typography, Button, Grid, Paper, Fade } from "@mui/material";

import useFetch from "../../hooks/useFetch";
import Indicator from "./indicator";
import Energysources from "./energysources";
import Timer from "./timer";
import Roastdata from "./roastdata";

export default function Dashboard() {
  interface CertificateData {
    sensorid: string;
    ownerorg: string;
    inspectororganisation: string;
    expirydate: string;
    firstauth: boolean;
  }

  const { data, loading, error } = useFetch(
    "http://127.0.0.1:1880/getCertificate?sensor=sensor3&org=org0-example-com"
  );
  const {
    data: emissionData,
    loading: emissionLoading,
    error: emissionError,
  } = useFetch("http://127.0.0.1:1880/getEmissionDataTest");
  return (
    <Box sx={{ flexGrow: 1, paddingX: 4 }}>
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          p: 2,
        }}
      >
        <Typography variant="h2" sx={{ fontWeight: 800 }}>
          Carbon<span style={{ color: "#FD6916" }}>Edge</span> Dashboard
        </Typography>
        <Button variant="contained" style={{ backgroundColor: "#FD6916" }}>
          Logout
        </Button>
      </Box>
      <Grid container spacing={1}>
        <Grid item xs={3}>
          <Fade in={true} timeout={1000}>
            <Paper
              sx={{
                p: 2,
                display: "flex",
                flexDirection: "column",
                minHeight: "150px",
              }}
            >
              <Typography variant="h6">Calibration</Typography>
              <Indicator loading={loading} data={data} error={error} />
            </Paper>
          </Fade>
        </Grid>
        <Grid item xs={3}>
          <Fade in={true} timeout={1000}>
            <Paper sx={{ p: 2, minHeight: "150px" }}>
              <Timer loading={loading} data={data} error={error} />
            </Paper>
          </Fade>
        </Grid>
        <Grid item xs={3}>
          <Fade in={true} timeout={1000}>
            <Paper sx={{ p: 2, minHeight: "150px" }}>
              <Typography variant="h6">CO2 Amount</Typography>
              <Typography variant="h3">123 kg</Typography>
            </Paper>
          </Fade>
        </Grid>
        <Grid item xs={3}>
          <Fade in={true} timeout={1000}>
            <Paper sx={{ p: 2, minHeight: "150px" }}>
              <Typography variant="h6">CO2 Amount</Typography>
              <Typography variant="h3">123 kg</Typography>
            </Paper>
          </Fade>
        </Grid>
        <Grid item xs={6}>
          <Fade in={true} timeout={1000}>
            <Paper
              sx={{
                p: 2,
                minHeight: "200px",
              }}
            >
              <Energysources
                data={emissionData}
                error={emissionError}
                loading={emissionLoading}
              />
            </Paper>
          </Fade>
        </Grid>

        <Grid item xs={6}>
          <Paper sx={{ p: 2, minHeight: "150px" }}>
            <Roastdata
              data={emissionData}
              error={emissionError}
              loading={emissionLoading}
            />
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
}
