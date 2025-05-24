import daisyui from 'daisyui'

export default {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}',
  ],
  theme: {
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
  plugins: [daisyui], // ✅ import 방식 통일
  daisyui: {
    themes: ["light", "dark", "diningbom"], // 기본 테마도 포함
    base: false, // DaisyUI의 기본 스타일 비활성화 (선택사항)
  },
}
