/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      fontFamily: {
        sans: ['Plus Jakarta Sans', 'Inter', 'system-ui', 'sans-serif'],
      },
      colors: {
        darkBg: '#090D16',
        darkCard: 'rgba(17, 24, 39, 0.7)',
        glowPurple: '#8B5CF6',
        glowIndigo: '#6366F1',
        glowCyan: '#06B6D4',
      },
      boxShadow: {
        'glass': '0 8px 32px 0 rgba(0, 0, 0, 0.37)',
        'glass-hover': '0 8px 32px 0 rgba(99, 102, 241, 0.15)',
        'glow-indigo': '0 0 20px rgba(99, 102, 241, 0.4)',
        'glow-emerald': '0 0 20px rgba(16, 185, 129, 0.4)',
        'glow-rose': '0 0 20px rgba(244, 63, 94, 0.4)',
      },
      backdropBlur: {
        'glass': '12px',
      }
    },
  },
  plugins: [],
}
