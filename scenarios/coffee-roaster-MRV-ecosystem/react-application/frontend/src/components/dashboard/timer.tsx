import { Typography, CircularProgress } from "@mui/material";

export default function Timer({ loading, error, data }: any) {
  const parsedData = data ? JSON.parse(data.response) : null;
  const today = new Date();
  const expiryDate = parsedData ? new Date(parsedData.expirydate) : new Date();

  function calculateTimeUntilExpiry(): string {
    if (!parsedData || isNaN(expiryDate.getTime())) return "No Data";

    const difference = expiryDate.getTime() - today.getTime();
    if (difference < 0) return "Expired";

    const days = Math.floor(difference / (1000 * 60 * 60 * 24));

    return `${days} days`;
  }

  return (
    <div>
      <Typography variant="h6">Calibration expires in</Typography>
      {loading ? (
        <CircularProgress />
      ) : error ? (
        <Typography variant="h3" color="error">
          Error loading data
        </Typography>
      ) : (
        <Typography variant="h3">{calculateTimeUntilExpiry()}</Typography>
      )}
    </div>
  );
}
