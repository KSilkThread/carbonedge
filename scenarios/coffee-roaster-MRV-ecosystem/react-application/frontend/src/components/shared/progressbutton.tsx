import "./progressbutton.css";

export default function Progressbutton({ value, min, max }: any) {
  const widthPercentage = ((value - min) * 100) / (max - min);

  return (
    <div
      className="button__progress"
      style={{ width: `${widthPercentage}%` }}
    ></div>
  );
}
