import React, { useState } from 'react'
import WelcomeScreen from './components/WelcomeScreen'
import DashboardSCADA from './components/DashboardSCADA'
import DashboardOEE from './components/DashboardOEE'
import DashboardResumenOEE from './components/DashboardResumenOEE'

function App() {
  const [activeTab, setActiveTab] = useState('welcome')
  const [selectedBono, setSelectedBono] = useState(null)

  const handleBonoSelect = (bonoId) => {
    setSelectedBono(bonoId)
    setActiveTab('oee')
  }

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col font-sans selection:bg-cyan-500/30">
      {activeTab !== 'welcome' && (
        <nav className="glass-panel z-10 relative">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="flex h-16">
              <div className="flex space-x-8">
                <button
                  onClick={() => setActiveTab('scada')}
                  className={`${
                    activeTab === 'scada' 
                      ? 'border-cyan-500 text-cyan-400 drop-shadow-[0_0_8px_rgba(34,211,238,0.5)]' 
                      : 'border-transparent text-slate-400 hover:text-slate-300 hover:border-slate-500'
                  } inline-flex items-center px-1 pt-1 border-b-4 text-sm font-bold tracking-wider uppercase transition-all duration-300`}
                >
                  Tiempo Real (Planta)
                </button>
                <button
                  onClick={() => setActiveTab('oee')}
                  className={`${
                    activeTab === 'oee' 
                      ? 'border-indigo-500 text-indigo-400 drop-shadow-[0_0_8px_rgba(99,102,241,0.5)]' 
                      : 'border-transparent text-slate-400 hover:text-slate-300 hover:border-slate-500'
                  } inline-flex items-center px-1 pt-1 border-b-4 text-sm font-bold tracking-wider uppercase transition-all duration-300`}
                >
                  Análisis OEE (Bono)
                </button>
                <button
                  onClick={() => setActiveTab('resumenOee')}
                  className={`${
                    activeTab === 'resumenOee' 
                      ? 'border-emerald-500 text-emerald-400 drop-shadow-[0_0_8px_rgba(16,185,129,0.5)]' 
                      : 'border-transparent text-slate-400 hover:text-slate-300 hover:border-slate-500'
                  } inline-flex items-center px-1 pt-1 border-b-4 text-sm font-bold tracking-wider uppercase transition-all duration-300`}
                >
                  Resumen OEE (Todos)
                </button>
              </div>
            </div>
          </div>
        </nav>
      )}

      <main className="flex-1 overflow-x-hidden">
        {activeTab === 'welcome' && <WelcomeScreen onContinue={() => setActiveTab('scada')} />}
        {activeTab === 'scada' && <DashboardSCADA />}
        {activeTab === 'oee' && <DashboardOEE initialBonoId={selectedBono} />}
        {activeTab === 'resumenOee' && <DashboardResumenOEE onBonoSelect={handleBonoSelect} />}
      </main>
    </div>
  )
}

export default App