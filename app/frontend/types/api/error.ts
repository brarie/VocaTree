export interface ErrorResponse {
  status: number;
  code: string;
  message: string;
}

export class ApiError extends Error {
  public readonly status: number;
  public readonly code: string;
  public readonly response?: Response;

  constructor(errorResponse: ErrorResponse, response?: Response) {
    super(errorResponse.message);
    this.name = 'ApiError';
    this.status = errorResponse.status;
    this.code = errorResponse.code;
    this.response = response;

    Object.setPrototypeOf(this, ApiError.prototype);
  }

  isStatus(status: number): boolean {
    return this.status === status;
  }

  isCode(code: string): boolean {
    return this.code === code;
  }
}
