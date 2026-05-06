export interface auth{
  id: number;
  accessToken: string;
  refreshToken: string;
}

export interface response{
  id: number;
  email: string;
}

export interface refreshRequest{
  refreshToken: string;
}

export interface loginRequest{
  email: string;
  password: string;
}