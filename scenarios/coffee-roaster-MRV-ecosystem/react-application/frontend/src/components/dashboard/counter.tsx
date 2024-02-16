import { Typography } from "@mui/material";
import { useEffect, useState } from "react";
interface CounterProps {
  headline: string;
  fieldname: string;
  data: any;
  error: any;
  loading: boolean;
  unit: string;
  fn: (x: any) => any;
}
export default function Counter({
  headline,
  fieldname,
  data,
  error,
  loading,
  unit,
  fn,
}: CounterProps) {
  const [value, setValue] = useState<number>(0);

  useEffect(() => {
    if (
      !loading &&
      data &&
      typeof data === "object" &&
      data[fieldname] !== undefined
    ) {
      setValue(data[fieldname]);
    } else if (!loading && data && data.response) {
      try {
        const parsedData = JSON.parse(data.response);
        console.log(parsedData);
        if (parsedData && parsedData[fieldname] !== undefined) {
          setValue(parsedData[fieldname]);
        } else {
          console.error("Fieldname not found in parsed data", fieldname);
        }
      } catch (error) {
        console.error("Failed to parse data", error);
      }
    } else {
      console.error("Fieldname not found in data", fieldname);
    }
  }, [loading, error, data, fieldname]);

  if (loading) return <Typography>Loading...</Typography>;
  if (error) return <Typography>Error: {error.message}</Typography>;
  return (
    <div>
      <Typography variant="h6">{headline}</Typography>
      <Typography variant="h4">
        {fn(value)} {unit}
      </Typography>
    </div>
  );
}
