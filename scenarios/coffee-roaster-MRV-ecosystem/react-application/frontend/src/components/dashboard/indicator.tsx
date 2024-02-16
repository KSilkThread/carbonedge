import { Box, Typography, Button, CircularProgress } from "@mui/material";
import { useState, useEffect } from "react";
import usePost from "../../hooks/usePost";
import Progressbutton from "../shared/progressbutton";

export default function Indicator({ loading, error, data, login }: any) {
  const [circleColor, setCircleColor] = useState<string>("transparent");
  const [isCalibrating, setIsCalibrating] = useState<boolean>(false);
  const [isPressing, setIsPressing] = useState<boolean>(false); // Track if the button is currently being pressed
  const [statusText, setStatusText] = useState<string>("Loading");
  const [buttonPressedTime, setButtonPressedTime] = useState<number>(0);
  const [progressValue, setProgressValue] = useState(0);

  const {
    data: postData,
    error: postError,
    loading: postLoading,
    post,
  } = usePost();

  /* This useEffect sets the circle color and status text to the respective state
    if data is loading: transparent circle, Loading text
    if error occurs: grey circle, error text
    
    afterwards the data is parsed, and the expiryDate of the data is matched against the current date to determine
    whether the circle should be
    green: more than 2 Weeks until expiry
    orange: less than 2 Weeks until expiry, but not yet expired 
    red: time expired
    There is an additionaly catch block to color the circle grey, if there is an error

    This first useEffect reacts to changes in data, error and the loading state
  */
  useEffect(() => {
    if (loading) {
      setCircleColor("transparent");
      setStatusText("Loading");
      return;
    }
    if (error) {
      console.error("Failed to fetch data", error);
      setCircleColor("grey");
      setStatusText("Error Fetching (See Console)");
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
        setCircleColor("orange");
        setStatusText("Calibration due soon");
      }
    } catch (error) {
      console.error("Failed to parse data", error);
      setCircleColor("grey");
      setStatusText("Error Parsing (See Console)");
    }
  }, [loading, error, data]);

  /* This useEffect handles the blinking effect of the circle if it is the calibrating state. 
      Given this, it sets the status text to calibrating, and lets the circle blink in a 500ms interval, on cleanup it clears the interval
      and resets the color of the circle

  */

  useEffect(() => {
    let interval: any = null;

    if (isCalibrating && !isPressing) {
      setStatusText("Calibrating");
      interval = setInterval(() => {
        setCircleColor((prevColor) =>
          prevColor === "yellow" ? "gray" : "yellow"
        );
      }, 500);
    }

    return () => {
      if (interval) clearInterval(interval);
      if (!isPressing) {
        // Reset to yellow when leaving calibration mode or not pressing
        setCircleColor("yellow");
      }
    };
  }, [isCalibrating, isPressing]);

  /* This useEffect handles the progress of the button

      if the state is calibrating, and the button is currently pressed, the status text is calibrating, and maintaining the
      blinking in the same manner as before (this was necessary, because blinking stopped when the button was pressed, for user feedback reasons
      it is still maintained while the button is pressed). 

  */
  useEffect(() => {
    let progressInterval: any = null;
    let interval: any = null;
    if (isCalibrating && isPressing) {
      setStatusText("Calibrating");
      interval = setInterval(() => {
        setCircleColor((prevColor) =>
          prevColor === "yellow" ? "gray" : "yellow"
        );
      }, 500);
      progressInterval = setInterval(() => {
        const currentTime = new Date().getTime();
        const duration = currentTime - buttonPressedTime;
        const progress = (duration / 3000) * 100;

        setProgressValue(progress > 100 ? 100 : progress);

        if (progress >= 100) {
          clearInterval(progressInterval); // Stop the interval once 100% is reached
        }
      }, 10);
    } else {
      setProgressValue(0); // Reset progress when not pressing
    }

    return () => {
      if (progressInterval) clearInterval(progressInterval);
      if (interval) clearInterval(interval);
      if (!isPressing) {
        setCircleColor("yellow");
      }
    };
  }, [isCalibrating, buttonPressedTime, isPressing]);

  /*
  This function handles the inital Press of the Button when its not in calibration state, it invokes the handleCalibrationStartRequest function
  and sets the calibrating state to true. It sets the isPressing state to true and starts counting 
  */
  const handleCalibratePress = () => {
    if (!isCalibrating) {
      handleCalibrationStartRequest();
      setIsCalibrating(true);
    }
    setButtonPressedTime(new Date().getTime());
    setIsPressing(true);
  };

  /*
  This function handles the release of the calibrationbutton.
  It updates the state "isPressing" to false and checks whether the button has been currently pressed more than 3 seconds.
  IF that is the case, and the state currently suggests the sensor to be calibrating, the calibration is ended/finished with an invocation of the
  handleCalibrationEndRequest function.
  */
  const handleCalibrateRelease = () => {
    setIsPressing(false);
    const pressDuration = new Date().getTime() - buttonPressedTime;
    if (pressDuration >= 3000 && isCalibrating) {
      setIsCalibrating(false);
      setCircleColor("yellow");
      setStatusText("Calibration finished");
      handleCalibrationEndRequest();
    }
  };

  /*
  This function sends a startCertification request to the backend. @FutureDevs: Change the Body accordingly whenever the backend functionality
  is implemented.
  */
  const handleCalibrationStartRequest = async () => {
    const url = "http://127.0.0.1:1880/startCertification";
    const body = {};

    await post(url, body);
  };

  /*
  This function sends a endCertification request to the backend. @FutureDevs: Change the Body accordingly whenever the backend functionality
  is implemented.
  */
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
      </Box>
      {login === "inspector" && (
        <Button
          variant="contained"
          sx={{
            backgroundColor: "#FD6916",
            position: "relative",
            overflow: "hidden",
            minHeight: "30px",
          }}
          onMouseDown={handleCalibratePress}
          onMouseUp={handleCalibrateRelease}
          onTouchStart={handleCalibratePress}
          onTouchEnd={handleCalibrateRelease}
        >
          Calibrate
          {isPressing && (
            <Progressbutton min={0} max={100} value={progressValue} />
          )}
        </Button>
      )}
    </Box>
  );
}
