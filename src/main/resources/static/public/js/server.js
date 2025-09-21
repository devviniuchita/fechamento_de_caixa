const http = require("http");
const fs = require("fs");
const path = require("path");

const mimeTypes = {
  ".html": "text/html",
  ".js": "text/javascript",
  ".css": "text/css",
  ".json": "application/json",
  ".png": "image/png",
  ".jpg": "image/jpg",
  ".gif": "image/gif",
  ".svg": "image/svg+xml",
  ".wav": "audio/wav",
  ".mp4": "video/mp4",
  ".woff": "application/font-woff",
  ".ttf": "application/font-ttf",
  ".eot": "application/vnd.ms-fontobject",
  ".otf": "application/font-otf",
  ".wasm": "application/wasm",
};

const server = http.createServer((req, res) => {
  // Normalize the URL by removing query strings and making it relative
  let filePath = "." + req.url.split("?")[0];

  // Handle root path
  if (filePath === "./") {
    filePath = "./index.html";
  }

  // Handle js paths to serve from public directory
  if (
    filePath.startsWith("./js/") ||
    filePath.startsWith("/js/") ||
    filePath.startsWith("js/")
  ) {
    filePath = "./public/" + filePath.replace(/^[\.\/]*js\//, "js/");
  }

  const extname = String(path.extname(filePath)).toLowerCase();
  const contentType = mimeTypes[extname] || "application/octet-stream";

  console.log(`Serving file: ${filePath}`); // Debug log

  fs.readFile(filePath, (error, content) => {
    if (error) {
      if (error.code === "ENOENT") {
        console.error(`File not found: ${filePath}`);
        res.writeHead(404);
        res.end("File not found");
      } else {
        console.error(`Server error: ${error.code}`);
        res.writeHead(500);
        res.end("Internal server error: " + error.code);
      }
    } else {
      res.writeHead(200, { "Content-Type": contentType });
      res.end(content, "utf-8");
    }
  });
});

const PORT = 8080;
server.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});
