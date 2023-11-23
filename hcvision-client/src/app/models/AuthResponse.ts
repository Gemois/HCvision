export class AuthResponse {
  constructor(
    public name: string,
    public email: string,
    public role: string,
    public confirmed: boolean,
    public access_token: string,
    public issued_at: string,
    public expires_at: string,
  ) {
  }
}
