import { Box, Typography, Button, CircularProgress } from "@mui/material";
import { useState, useEffect } from "react";
import usePost from "../../hooks/usePost";

export default function Indicator({ loading, error, data }: any) {
  const [circleColor, setCircleColor] = useState<string>("transparent");
  const [isCalibrating, setIsCalibrating] = useState<boolean>(false);
  const [statusText, setStatusText] = useState<string>("Loading");
  const [buttonPressedTime, setButtonPressedTime] = useState<number>(0);
  const {
    data: postData,
    error: postError,
    loading: postLoading,
    post,
  } = usePost();

  useEffect(() => {
    if (loading) {
      setCircleColor("transparent");
      setStatusText("Loading");
      return;
    }
    if (error) {
      setCircleColor("grey");
      setStatusText("Error Fetching");
      return;
    }

    try {
      const parsedData = data ? JSON.parse(data.response) : null;
      const today = new Date();
      const expiryDate = parsedData
        ? new Date(parsedData.expirydate)
        : new Date();
      const twoWeeksAway = new Date(today.getTime() + 12096e5);

      if (expiryDate > twoWeeksAway) {
        setCircleColor("green");
        setStatusText("Calibrated");
      } else if (expiryDate <= today) {
        setCircleColor("red");
        setStatusText("Expired");
      } else {
        setCircleColor("yellow");
        setStatusText("Calibration due soon");
      }
    } catch (error) {
      console.error("Failed to parse data", error);
      setCircleColor("grey");
      setStatusText("Error Fetching");
    }
  }, [loading, error, data]);

  useEffect(() => {
    let interval: number | null = null;

    if (isCalibrating) {
      setStatusText("Calibrating...");
      interval = setInterval(() => {
        setCircleColor((prevColor) =>
          prevColor === "yellow" ? "gray" : "yellow"
        );
      }, 500);
    }

    return () => {
      if (interval) clearInterval(interval);
    };
  }, [isCalibrating]);

  const handleCalibratePress = () => {
    handleCalibrationStartRequest();
    setIsCalibrating(true);
    setButtonPressedTime(new Date().getTime());
  };

  const handleCalibrateRelease = () => {
    const pressDuration = new Date().getTime() - buttonPressedTime;
    if (pressDuration >= 3000) {
      setIsCalibrating(false);
      setCircleColor("yellow");
      setStatusText("Calibration ended");
      handleCalibrationEndRequest();
    }
  };

  const handleCalibrationStartRequest = async () => {
    const url = "http://127.0.0.1:1880/startCertification";
    const body = {};

    await post(url, body);
  };

  const handleCalibrationEndRequest = async () => {
    const url = "http://127.0.0.1:1880/endCertification";
    const body = {};

    await post(url, body);
  };

  return (
    <Box sx={{ display: "flex", alignItems: "center", gap: 2, my: 2 }}>
      {loading ? (
        <CircularProgress />
      ) : (
        <Box
          sx={{
            width: 70,
            height: 70,
            borderRadius: "50%",
            backgroundColor: circleColor,
          }}
        />
      )}
      <Box>
        <Typography variant="h6">{statusText}</Typography>
        <Button
          variant="contained"
          style={{ backgroundColor: "#FD6916" }}
          onMouseDown={handleCalibratePress}
          onMouseUp={handleCalibrateRelease}
          onTouchStart={handleCalibratePress}
          onTouchEnd={handleCalibrateRelease}
        >
          Calibrate
        </Button>
      </Box>
    </Box>
  );
}
