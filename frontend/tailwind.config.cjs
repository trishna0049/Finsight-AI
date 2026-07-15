/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        surface: "#111522",
        panel: "#171c2b",
        accent: "#00d1b2",
        warning: "#ffb020",
        critical: "#ff5f6d"
      },
      boxShadow: {
        panel: "0 8px 40px rgba(0, 0, 0, 0.28)"
      }
    }
  },
  plugins: []
};
