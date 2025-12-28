# AI PDF Summarizer

A full-stack application for summarizing PDF documents using Google Gemini API. Consists of a Spring Boot backend and a React frontend.

## Features

- Extract text from PDF files using Apache PDFBox
- Summarize extracted text using Google Gemini API
- RESTful API endpoint for PDF summarization

## Requirements

- Java 17
- Maven 3.6+

## Configuration

1. Set your Google Gemini API key in `application.properties` or as an environment variable:

```properties
google.gemini.api.key=your-api-key-here
```

Or set the environment variable:
```bash
export GEMINI_API_KEY=your-api-key-here
```

## Building and Running

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on port 8080 by default.

## API Endpoint

### POST /api/pdf/summarize

Summarizes a PDF file.

**Request:**
- Method: POST
- Content-Type: multipart/form-data
- Body: Form data with `file` parameter containing the PDF file

**Response:**
```json
{
  "summary": "Bullet point summary of the PDF content..."
}
```

**Example using cURL:**
```bash
curl -X POST http://localhost:8080/api/pdf/summarize \
  -F "file=@document.pdf"
```

## Frontend

The React frontend is located in the `frontend` directory. See [frontend/README.md](frontend/README.md) for details.

Quick start:
```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at `http://localhost:5173` by default.

## Project Structure

```
├── src/main/java/com/example/pdfsummarizer/
│   ├── PdfSummarizerApplication.java      # Main Spring Boot application
│   ├── controller/
│   │   └── PdfSummarizerController.java   # REST controller
│   ├── service/
│   │   ├── PdfExtractionService.java      # PDF text extraction service
│   │   └── GeminiApiService.java          # Google Gemini API service
│   └── config/
│       ├── RestTemplateConfig.java        # RestTemplate configuration
│       ├── ObjectMapperConfig.java        # ObjectMapper configuration
│       └── CorsConfig.java                # CORS configuration
└── frontend/
    └── src/
        ├── App.jsx                        # Main React component
        └── services/
            └── pdfApi.js                  # API service
```

