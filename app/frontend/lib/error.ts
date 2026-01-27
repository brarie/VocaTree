import { ErrorResponse, ApiError } from '@/types/api/error';

export async function handleApiResponse(
  response: Response,
  defaultMessage: string = 'An error occurred',
): Promise<Response> {
  if (!response.ok) {
    const errorResponse = await response
      .json()
      .then((data) => data as ErrorResponse)
      .catch(() => ({
        status: response.status,
        code: 'UNKNOWN_ERROR',
        message: defaultMessage,
      }));

    throw new ApiError(errorResponse, response);
  }

  return response;
}

export function isApiError(error: unknown): error is ApiError {
  return error instanceof ApiError;
}

export function getErrorMessage(
  error: unknown,
  fallback: string = '알 수 없는 오류가 발생했습니다.',
): string {
  if (isApiError(error)) {
    return error.message;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return fallback;
}
