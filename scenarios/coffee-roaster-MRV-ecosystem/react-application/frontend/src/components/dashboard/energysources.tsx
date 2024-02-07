import { Typography, Box } from "@mui/material";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import { Doughnut } from "react-chartjs-2";

ChartJS.register(ArcElement, Tooltip, Legend);

export default function Energysources() {
  const data = {
    labels: ["LPG", "Natural Gas", "Electricity"],
    datasets: [
      {
        label: "Energy Source",
        data: [12, 19, 3],
        backgroundColor: ["#FD6916", "#16AAFD", "rgba(255, 206, 86, 0.2)"],
        borderColor: ["#FD6916", "#16AAFD", "rgba(255, 206, 86, 1)"],
        borderWidth: 1,
      },
    ],
  };
  const options = {
    responsive: false,
    maintainAspectRatio: true,
    plugins: {
      legend: {
        display: true,
        position: "right" as const,
      },
    },
  };
  return (
    <>
      <Typography variant="h6">Energy used</Typography>
      <Box
        sx={{ display: "flex", justifyContent: "center", alignItems: "center" }}
      >
        <Box>
          <Doughnut data={data} options={options} />
        </Box>
      </Box>
    </>
  );
}
