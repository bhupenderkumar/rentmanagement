import { IBuilding } from 'app/entities/building/building.model';
import { ITenant } from 'app/entities/tenant/tenant.model';

export interface IRoom {
  id?: number;
  roomName?: string;
  floor?: string | null;
  building?: IBuilding;
  tenants?: ITenant | null;
}

export class Room implements IRoom {
  constructor(
    public id?: number,
    public roomName?: string,
    public floor?: string | null,
    public building?: IBuilding,
    public tenants?: ITenant | null
  ) {}
}

export function getRoomIdentifier(room: IRoom): number | undefined {
  return room.id;
}
