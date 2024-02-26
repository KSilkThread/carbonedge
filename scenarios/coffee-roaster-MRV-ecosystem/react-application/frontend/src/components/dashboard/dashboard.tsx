import { Box, Typography, Button, Grid, Paper, Fade } from "@mui/material";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import useFetch from "../../hooks/useFetch";
import Indicator from "./indicator";
import Energysources from "./energysources";
import Timer from "./timer";
import Roastdata from "./roastdata";
import { useLogin } from "../../context/LoginContext";
import Counter from "./counter";

export default function Dashboard() {
  const apiUrl = import.meta.env.VITE_BACKEND_API_URL;
  const { loginStatus } = useLogin();
  const navigate = useNavigate();
  function refresh() {
    window.location.reload();
  }
  const { data, loading, error } = useFetch(
    `${apiUrl}/getCertificate?sensor=sensor3&org=org0-example-com`
  );
  const {
    data: emissionData,
    loading: emissionLoading,
    error: emissionError,
  } = useFetch(`${apiUrl}/getEmissionDataTest`);

  useEffect(() => {
    if (loginStatus === "nobody") {
      navigate("/");
    }
  }, [loginStatus, navigate]);

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
        <Button
          variant="contained"
          onClick={refresh}
          style={{ backgroundColor: "#FD6916" }}
        >
          Refresh
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
              <Indicator
                loading={loading}
                data={data}
                error={error}
                login={loginStatus}
              />
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
              <Counter
                headline={"Batch CO2 Emissions"}
                fieldname={"CO2_batch"}
                loading={emissionLoading}
                data={emissionData}
                error={emissionError}
                unit={"kg"}
                fn={(x) => Math.round(x / 1000)}
              />
            </Paper>
          </Fade>
        </Grid>
        <Grid item xs={3}>
          <Fade in={true} timeout={1000}>
            <Paper sx={{ p: 2, minHeight: "150px" }}>
              <Counter
                headline={"Batch Energy Consumtion"}
                fieldname={"BTU_batch"}
                loading={emissionLoading}
                data={emissionData}
                error={emissionError}
                unit={"BTU"}
                fn={(x) => Math.round(x)}
              />
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
