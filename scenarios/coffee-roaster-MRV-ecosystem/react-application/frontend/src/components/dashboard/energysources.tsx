import { Typography, Box } from "@mui/material";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import { Doughnut } from "react-chartjs-2";
import { EmissionData } from "../../types/emissionData";
ChartJS.register(ArcElement, Tooltip, Legend);

export default function Energysources({ data, error, loading }: any) {
  const emissionData: EmissionData | null = data as EmissionData | null;
  const dataSet = emissionData
    ? {
        labels: ["LPG", "Natural Gas", "Electricity"],
        datasets: [
          {
            label: "Energy Source",
            data: [
              emissionData.BTU_LPG,
              emissionData.BTU_NG,
              emissionData.BTU_ELEC,
            ],
            backgroundColor: ["#FD6916", "#16AAFD", "rgba(255, 206, 86, 0.2)"],
            borderColor: ["#FD6916", "#16AAFD", "rgba(255, 206, 86, 1)"],
            borderWidth: 1,
          },
        ],
      }
    : null;

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
  if (loading) return <Typography>Loading...</Typography>;
  if (error) return <Typography>Error: {error.message}</Typography>;
  return (
    <>
      <Typography variant="h6">Energy used (in BTU)</Typography>
      <Box
        sx={{ display: "flex", justifyContent: "center", alignItems: "center" }}
      >
        <Box>{dataSet && <Doughnut data={dataSet} options={options} />}</Box>
      </Box>
    </>
  );
}
