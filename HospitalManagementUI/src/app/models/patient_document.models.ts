export interface documentResponse{
  id: number;
  fileName: string;
}

export interface documentResponseWithUrl{
  id: number;
  fileName: string;
  documentUrl: string;
}

export interface documentUploadStatus{
  fileName: string;
  progress: number;
  status: 'pending' | 'uploading' | 'success' | 'error';
}