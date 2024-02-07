import { Typography } from "@mui/material";

import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import { Line } from "react-chartjs-2";
import useFetch from "../../hooks/useFetch";
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

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

export default function Roastdata() {
  const { data, loading, error } = useFetch(
    "http://127.0.0.1:1880/getEmissionDataTest"
  );

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

  const options = {
    responsive: true,
    interaction: {
      mode: "index" as const,
      intersect: false,
    },
    stacked: false,
    plugins: {
      title: {
        display: false,
        text: "Energy consumtion and CO2 emission",
      },
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
  if (loading) return <Typography>Loading...</Typography>;
  if (error) return <Typography>Error: {error}</Typography>;
  return (
    <div>
      <Typography variant="h6">CO2 Emissions during Process</Typography>
      {dataSet && <Line options={options} data={dataSet} />}
    </div>
  );
}
