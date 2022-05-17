import dayjs from 'dayjs/esm';
import { IGenerateBill } from 'app/entities/generate-bill/generate-bill.model';
import { ILocation } from 'app/entities/location/location.model';
import { IRoom } from 'app/entities/room/room.model';

export interface ITenant {
  id?: number;
  tenantName?: string;
  addressProffContentType?: string;
  addressProff?: string;
  numberofFamilyMembers?: number | null;
  phoneNumber?: string;
  rentStartDate?: dayjs.Dayjs;
  rentAmount?: number;
  electricityUnitRate?: number;
  startingElectricityUnit?: number;
  anyOtherDetails?: string | null;
  sendNotification?: boolean | null;
  emailAddress?: string | null;
  emergencyContactNumber?: string | null;
  outStandingAmount?: number | null;
  monthEndCalculation?: boolean | null;
  calculateOnDate?: boolean | null;
  tenant?: IGenerateBill | null;
  location?: ILocation | null;
  rooms?: IRoom[];
}

export class Tenant implements ITenant {
  constructor(
    public id?: number,
    public tenantName?: string,
    public addressProffContentType?: string,
    public addressProff?: string,
    public numberofFamilyMembers?: number | null,
    public phoneNumber?: string,
    public rentStartDate?: dayjs.Dayjs,
    public rentAmount?: number,
    public electricityUnitRate?: number,
    public startingElectricityUnit?: number,
    public anyOtherDetails?: string | null,
    public sendNotification?: boolean | null,
    public emailAddress?: string | null,
    public emergencyContactNumber?: string | null,
    public outStandingAmount?: number | null,
    public monthEndCalculation?: boolean | null,
    public calculateOnDate?: boolean | null,
    public tenant?: IGenerateBill | null,
    public location?: ILocation | null,
    public rooms?: IRoom[]
  ) {
    this.sendNotification = this.sendNotification ?? false;
    this.monthEndCalculation = this.monthEndCalculation ?? false;
    this.calculateOnDate = this.calculateOnDate ?? false;
  }
}

export function getTenantIdentifier(tenant: ITenant): number | undefined {
  return tenant.id;
}
