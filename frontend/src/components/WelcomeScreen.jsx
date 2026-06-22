import React from 'react'

export default function WelcomeScreen({ onContinue }) {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-slate-950 p-6 relative overflow-hidden">
      {/* Background decorations */}
      <div className="absolute top-[-10%] left-[-10%] w-96 h-96 bg-cyan-900/20 rounded-full blur-3xl pointer-events-none"></div>
      <div className="absolute bottom-[-10%] right-[-10%] w-96 h-96 bg-indigo-900/20 rounded-full blur-3xl pointer-events-none"></div>
      
      <div className="glass-panel max-w-3xl w-full p-10 md:p-14 text-center relative z-10 flex flex-col items-center border border-slate-800/60 shadow-2xl">
        
        <div className="mb-4 inline-flex items-center justify-center px-3 py-1 rounded-full bg-cyan-500/10 border border-cyan-500/30 text-cyan-400 text-sm font-medium tracking-wide">
          Trabajo de Fin de Grado
        </div>

        <h1 className="text-3xl md:text-4xl lg:text-5xl font-bold text-white mb-10 leading-tight">
          Plataforma web para la monitorización de equipos industriales controlados por PLC OMRON: <span className="text-cyan-400">integración de datos y análisis OEE</span>
        </h1>

        <div className="w-24 h-1 bg-gradient-to-r from-cyan-500 to-indigo-500 rounded-full mb-10"></div>

        <div className="flex flex-col md:flex-row gap-8 md:gap-16 mb-14 text-left w-full justify-center">
          <div className="flex flex-col items-center text-center">
            <span className="text-slate-500 text-sm uppercase tracking-widest font-semibold mb-2">Autor</span>
            <span className="text-slate-200 text-lg font-medium">Juan Pedro Jiménez Dato</span>
          </div>
          
          <div className="hidden md:block w-px h-12 bg-slate-800 self-center"></div>

          <div className="flex flex-col items-center text-center">
            <span className="text-slate-500 text-sm uppercase tracking-widest font-semibold mb-2">Tutor</span>
            <span className="text-slate-200 text-lg font-medium">Juan José López Jiménez</span>
          </div>
        </div>

        <button 
          onClick={onContinue}
          className="group relative inline-flex items-center justify-center px-8 py-4 text-lg font-bold text-white transition-all duration-300 bg-gradient-to-r from-cyan-600 to-indigo-600 rounded-xl hover:from-cyan-500 hover:to-indigo-500 focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:ring-offset-2 focus:ring-offset-slate-950 overflow-hidden shadow-[0_0_20px_rgba(34,211,238,0.3)] hover:shadow-[0_0_30px_rgba(34,211,238,0.5)] transform hover:-translate-y-1"
        >
          <span className="absolute inset-0 w-full h-full -mt-1 rounded-lg opacity-30 bg-gradient-to-b from-transparent via-transparent to-black"></span>
          <span className="relative flex items-center gap-2">
            Continuar al Proyecto
            <svg className="w-5 h-5 transition-transform duration-300 group-hover:translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path></svg>
          </span>
        </button>

      </div>
    </div>
  )
}
