export interface RequestDummy {
  name: string;
  description: string;
  serialNumber: string;
  createdAt: Date;
}

export interface ResponseDummy {
  id?: number;
  name: string;
  description: string;
  serialNumber: string;
  createdAt: Date;
}

export interface Dummy {
  id?: string;
  name: string;
  description: string;
  serialNumber: string;
  createdAt: Date; //dd-MM-yyyy
}

