# PDF Summarizer Frontend

A React frontend application built with Vite and Tailwind CSS for summarizing PDF documents.

## Features

- Drag and drop file upload
- PDF file validation
- Loading spinner during processing
- Beautiful summary display in card format
- Responsive design

## Getting Started

### Prerequisites

- Node.js 18+ (Node 20+ recommended)
- npm or yarn

### Installation

```bash
# Install dependencies
npm install
```

### Development

```bash
# Start the development server
npm run dev
```

The frontend will be available at `http://localhost:5173` (or another port if 5173 is occupied).

### Building for Production

```bash
# Build for production
npm run build

# Preview production build
npm run preview
```

## Configuration

The frontend is configured to proxy API requests to the Spring Boot backend running on `http://localhost:8080`. Make sure the backend is running before using the frontend.

The proxy configuration can be found in `vite.config.js`.

## Usage

1. Start the Spring Boot backend on port 8080
2. Start the frontend development server
3. Open the application in your browser
4. Upload a PDF file either by clicking "Select PDF File" or dragging and dropping
5. Click "Summarize PDF" to get the AI-generated summary
6. View the summary in the card below

## Project Structure

```
src/
├── App.jsx              # Main application component
├── main.jsx             # Application entry point
├── index.css            # Global styles with Tailwind CSS
└── services/
    └── pdfApi.js        # API service for backend communication
```
