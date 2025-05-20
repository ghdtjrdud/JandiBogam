import daisyui from 'daisyui'

export default {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}', // 모든 하위 폴더!
  ],
  theme: {
    container: {
      center: true,
      padding: "2rem",
      screens: {
        "2xl": "1600px",
      },
    },
    extend: {
      colors: {
        brand: {
          primary: "#6A7D73",
          secondary: "#9E8C7F",
          border: "#B29888",
          muted: "#BAA89B",
          lightbg: "#F6FAF7",
          accent: "#C7D7CB",
          hover: "#5A6B63",
        },
      },
    },
  },
  plugins: [require("daisyui")],
  daisyui: {
    themes: [
      {
        diningbom: {
          primary: "#6A7D73",
          secondary: "#9E8C7F",
          accent: "#BAA89B",
          neutral: "#B29888",
          "base-100": "#ffffff",
        },
      },
    ],
  },
}
