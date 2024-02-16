import { Typography } from "@mui/material";

import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import { Bar } from "react-chartjs-2";
import { EmissionData } from "../../types/emissionData";
ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

export default function Roastdata({ data, error, loading }: any) {
  const options = {
    plugins: {
      title: {
        display: false,
        text: "Energy consumtion and CO2 emission",
      },
    },
    responsive: true,
    interaction: {
      mode: "index" as const,
      intersect: false,
    },
    scales: {
      y: {
        type: "linear" as const,
        display: true,
        position: "left" as const,
      },
      y1: {
        type: "linear" as const,
        display: true,
        position: "right" as const,
        grid: {
          drawOnChartArea: false,
        },
      },
    },
  };

  const emissionData: EmissionData | null = data as EmissionData | null;

  const labels = ["Preheat", "Roast", "BBP", "Cooling"];
  const dataSet = emissionData
    ? {
        labels,
        datasets: [
          {
            label: "Energy Consumption",
            borderColor: "#FD6916",
            backgroundColor: "#FD6916",
            data: [
              emissionData.BTU_preheat,
              emissionData.BTU_roast,
              emissionData.BTU_bbp,
              emissionData.BTU_cooling,
            ],
            yAxisID: "y",
            lineTension: 0.4,
          },
          {
            label: "CO2 Emissions",
            data: [
              emissionData.CO2_preheat,
              emissionData.CO2_roast,
              emissionData.CO2_bbp,
              emissionData.CO2_cooling,
            ],
            borderColor: "#16AAFD",
            backgroundColor: "#16AAFD",
            yAxisID: "y1",
            lineTension: 0.4,
          },
        ],
      }
    : null;
  if (loading) return <Typography>Loading...</Typography>;
  if (error) return <Typography>Error: {error.message}</Typography>;
  return (
    <div>
      <Typography variant="h6">CO2 Emissions during Process</Typography>
      {dataSet && <Bar options={options} data={dataSet} />}
    </div>
  );
}
