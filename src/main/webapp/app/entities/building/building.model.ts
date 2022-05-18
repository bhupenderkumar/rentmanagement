import { ILocation } from 'app/entities/location/location.model';
import { IRoom } from 'app/entities/room/room.model';

export interface IBuilding {
  id?: number;
  buildingName?: string;
  address?: ILocation | null;
  buildings?: IRoom[];
}

export class Building implements IBuilding {
  constructor(public id?: number, public buildingName?: string, public address?: ILocation | null, public buildings?: IRoom[]) {}
}

export function getBuildingIdentifier(building: IBuilding): number | undefined {
  return building.id;
}
