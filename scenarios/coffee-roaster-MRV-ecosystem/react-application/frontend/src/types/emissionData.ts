 /* c8 ignore next 23 */
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
export type {EmissionData}