const API_BASE_URL = '/api/pdf';

export const summarizePdf = async (file) => {
  const formData = new FormData();
  formData.append('file', file);

  const response = await fetch(`${API_BASE_URL}/summarize`, {
    method: 'POST',
    body: formData,
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || 'Failed to summarize PDF');
  }

  return response.json();
};
