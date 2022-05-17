export interface IOwner {
  id?: number;
  phoneNumber?: string;
  emailAddress?: string | null;
}

export class Owner implements IOwner {
  constructor(public id?: number, public phoneNumber?: string, public emailAddress?: string | null) {}
}

export function getOwnerIdentifier(owner: IOwner): number | undefined {
  return owner.id;
}
