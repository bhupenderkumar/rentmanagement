import { ITenant } from 'app/entities/tenant/tenant.model';

export interface IGenerateBill {
  id?: number;
  amountPending?: number | null;
  sendNotification?: boolean | null;
  electricityUnit?: number | null;
  tenant?: ITenant;
}

export class GenerateBill implements IGenerateBill {
  constructor(
    public id?: number,
    public amountPending?: number | null,
    public sendNotification?: boolean | null,
    public electricityUnit?: number | null,
    public tenant?: ITenant
  ) {
    this.sendNotification = this.sendNotification ?? false;
  }
}

export function getGenerateBillIdentifier(generateBill: IGenerateBill): number | undefined {
  return generateBill.id;
}
