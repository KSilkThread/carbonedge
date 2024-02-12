import { Typography, Box } from "@mui/material";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import { Doughnut } from "react-chartjs-2";

ChartJS.register(ArcElement, Tooltip, Legend);
interface EmissionData {
  BTU_batch: number;
  BTU_batch_per_green_kg: number;
  CO2_batch: number;
  BTU_preheat: number;
  CO2_preheat: number;
  BTU_bbp: number;
  CO2_bbp: number;
  BTU_cooling: number;
  CO2_cooling: number;
  BTU_roast: number;
  BTU_roast_per_green_kg: number;
  CO2_roast: number;
  CO2_batch_per_green_kg: number;
  CO2_roast_per_green_kg: number;
  BTU_LPG: number;
  BTU_NG: number;
  BTU_ELEC: number;
  KWH_batch_per_green_kg: number;
  KWH_roast_per_green_kg: number;
}
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
  if (error) return <Typography>Error: {error}</Typography>;
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
