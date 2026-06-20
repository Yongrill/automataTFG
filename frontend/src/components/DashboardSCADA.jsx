import React, { useState, useEffect } from "react";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import { Bar } from "react-chartjs-2";
import { Activity, Zap, PlayCircle, StopCircle, AlertTriangle, Settings2, Hash } from "lucide-react";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const CircularProgress = ({ value, label, colorClass, shadowClass }) => {
  const radius = 36;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference - (value / 100) * circumference;

  return (
    <div className="flex flex-col items-center justify-center">
      <div className="relative w-24 h-24 flex items-center justify-center">
        {/* Background Circle */}
        <svg className="absolute inset-0 w-full h-full transform -rotate-90">
          <circle
            cx="48"
            cy="48"
            r={radius}
            stroke="currentColor"
            strokeWidth="8"
            fill="transparent"
            className="text-slate-700/50"
          />
          {/* Progress Circle */}
          <circle
            cx="48"
            cy="48"
            r={radius}
            stroke="currentColor"
            strokeWidth="8"
            fill="transparent"
            strokeDasharray={circumference}
            strokeDashoffset={strokeDashoffset}
            className={`${colorClass} transition-all duration-1000 ease-out ${shadowClass}`}
            strokeLinecap="round"
          />
        </svg>
        <span className="text-xl font-bold text-white shadow-sm">{value.toFixed(1)}%</span>
      </div>
      <span className="mt-3 text-xs font-semibold text-slate-400 uppercase tracking-widest">{label}</span>
    </div>
  );
};

const DashboardSCADA = () => {
  const [maquinas, setMaquinas] = useState([]);
  const [oeeGlobal, setOeeGlobal] = useState({ disp: 0, rend: 0, cal: 0, oee: 0 });
  const [loading, setLoading] = useState(true);

  const fetchData = async () => {
    try {
      const [resScada, resOee] = await Promise.all([
        fetch("/api/scada/estado-actual"),
        fetch("/api/oee/resumen")
      ]);

      if (resScada.ok) {
        const scadaData = await resScada.json();
        setMaquinas(scadaData);
      }

      if (resOee.ok) {
        const oeeData = await resOee.json();
        if (oeeData && oeeData.length > 0) {
          const avg = (key) => oeeData.reduce((acc, curr) => acc + (curr[key] || 0), 0) / oeeData.length;
          setOeeGlobal({
            disp: avg('disponibilidad'),
            rend: avg('rendimiento'),
            cal: avg('calidad'),
            oee: avg('oeeTotal')
          });
        }
      }
    } catch (error) {
      console.error("Error fetching data", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 5000);
    return () => clearInterval(interval);
  }, []);

  const getStatusStyle = (idEstado) => {
    switch (idEstado) {
      case 0: return { bg: "bg-emerald-500", glow: "shadow-[0_0_15px_rgba(16,185,129,0.5)]", text: "text-emerald-400", label: "AUTOMÁTICO", icon: <PlayCircle size={18} /> };
      case 1: return { bg: "bg-amber-500", glow: "shadow-[0_0_15px_rgba(245,158,11,0.5)]", text: "text-amber-400", label: "MANUAL", icon: <Settings2 size={18} /> };
      case 2: return { bg: "bg-rose-500", glow: "shadow-[0_0_15px_rgba(225,29,72,0.5)]", text: "text-rose-400", label: "PARO", icon: <StopCircle size={18} /> };
      default: return { bg: "bg-slate-600", glow: "", text: "text-slate-400", label: "DESCONECTADO", icon: <AlertTriangle size={18} /> };
    }
  };

  const maquinasActivas = maquinas.filter(m => m.estado === 0).length;
  const maquinasParadas = maquinas.filter(m => m.estado === 1 || m.estado === 2).length;

  const chartData = {
    labels: maquinas.map((m) => `MÁQ-${m.id.toString().padStart(2, '0')}`),
    datasets: [
      {
        label: "Tiempo de Ciclo Actual (s)",
        data: maquinas.map((m) => m.tiempoCiclo),
        backgroundColor: "rgba(6, 182, 212, 0.8)", // Cyan-500
        borderColor: "rgba(8, 145, 178, 1)", // Cyan-600
        borderWidth: 1,
        borderRadius: 4,
        hoverBackgroundColor: "rgba(34, 211, 238, 1)", // Cyan-400
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      title: { display: false },
      tooltip: {
        backgroundColor: 'rgba(15, 23, 42, 0.9)',
        titleColor: '#e2e8f0',
        bodyColor: '#e2e8f0',
        borderColor: 'rgba(51, 65, 85, 0.5)',
        borderWidth: 1,
        padding: 12,
        cornerRadius: 8,
      }
    },
    scales: {
      y: { 
        grid: { color: "rgba(51, 65, 85, 0.5)", borderDash: [5, 5] }, 
        ticks: { color: "#94a3b8", font: { family: 'Inter' } },
        beginAtZero: true
      },
      x: { 
        grid: { display: false }, 
        ticks: { color: "#94a3b8", font: { family: 'Inter', size: 11 } } 
      }
    }
  };

  if (loading) {
    return (
      <div className="flex h-screen w-full items-center justify-center bg-slate-950 text-white">
        <div className="flex flex-col items-center gap-4">
          <Activity className="animate-bounce text-cyan-500" size={48} />
          <h2 className="text-xl font-medium text-slate-300 tracking-widest uppercase">Estableciendo conexión...</h2>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen w-full p-6 lg:p-8 font-sans selection:bg-cyan-500/30">
      <header className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-black tracking-tight text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 to-blue-500 uppercase drop-shadow-sm flex items-center gap-3">
            <Zap className="text-cyan-400" size={32} />
            Monitorización en Tiempo Real
          </h1>
          <p className="text-slate-400 mt-2 text-sm font-medium tracking-wide">PANEL SCADA Y RENDIMIENTO GLOBAL DE PLANTA</p>
        </div>
      </header>

      {/* ZONA SUPERIOR: KPIs y OEE Global */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 mb-8">
        
        {/* Resumen Máquinas */}
        <div className="lg:col-span-4 flex flex-col gap-6">
          <div className="glass-panel rounded-2xl p-6 flex items-center justify-between group">
            <div>
              <h3 className="text-slate-400 text-xs font-bold uppercase tracking-widest mb-1">En Producción</h3>
              <p className="text-5xl font-black text-emerald-400 drop-shadow-[0_0_10px_rgba(16,185,129,0.3)]">{maquinasActivas}</p>
            </div>
            <div className="w-16 h-16 rounded-full bg-emerald-500/10 flex items-center justify-center border border-emerald-500/20 group-hover:scale-110 transition-transform">
              <PlayCircle className="text-emerald-500" size={32} />
            </div>
          </div>

          <div className="glass-panel rounded-2xl p-6 flex items-center justify-between group">
            <div>
              <h3 className="text-slate-400 text-xs font-bold uppercase tracking-widest mb-1">Máquinas Paradas</h3>
              <p className="text-5xl font-black text-rose-500 drop-shadow-[0_0_10px_rgba(225,29,72,0.3)]">{maquinasParadas}</p>
            </div>
            <div className="w-16 h-16 rounded-full bg-rose-500/10 flex items-center justify-center border border-rose-500/20 group-hover:scale-110 transition-transform">
              <StopCircle className="text-rose-500" size={32} />
            </div>
          </div>
        </div>

        {/* OEE Global Circular Progress */}
        <div className="lg:col-span-8 glass-panel rounded-2xl p-8 flex flex-col justify-center">
          <h3 className="text-slate-300 text-sm font-bold uppercase tracking-widest mb-8 flex items-center gap-2">
            <Activity size={18} className="text-cyan-400"/> Rendimiento OEE Global (Media Planta)
          </h3>
          <div className="flex flex-wrap justify-around items-center gap-6">
            <CircularProgress value={oeeGlobal.disp} label="Disponibilidad" colorClass="text-emerald-400" shadowClass="drop-shadow-[0_0_8px_rgba(52,211,153,0.5)]" />
            <CircularProgress value={oeeGlobal.rend} label="Rendimiento" colorClass="text-blue-400" shadowClass="drop-shadow-[0_0_8px_rgba(96,165,250,0.5)]" />
            <CircularProgress value={oeeGlobal.cal} label="Calidad" colorClass="text-amber-400" shadowClass="drop-shadow-[0_0_8px_rgba(251,191,36,0.5)]" />
            <div className="hidden md:block w-px h-20 bg-slate-700/50"></div>
            <CircularProgress value={oeeGlobal.oee} label="OEE Total" colorClass="text-cyan-400" shadowClass="drop-shadow-[0_0_12px_rgba(34,211,238,0.8)]" />
          </div>
        </div>
      </div>

      {/* GRID DE MÁQUINAS */}
      <h3 className="text-slate-300 text-sm font-bold uppercase tracking-widest mb-4 flex items-center gap-2 mt-4">
        <Hash size={18} className="text-slate-400"/> Detalle por Máquina
      </h3>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {maquinas.map((maq) => {
          const style = getStatusStyle(maq.estado);
          return (
            <div key={maq.id} className="glass-card rounded-2xl p-5 relative overflow-hidden group">
              {/* Decoración superior sutil */}
              <div className={`absolute top-0 left-0 w-full h-1 ${style.bg} ${style.glow}`}></div>
              
              <div className="flex justify-between items-start mb-6">
                <div>
                  <h2 className="text-xl font-bold text-slate-100 mb-1.5 tracking-wide">MAQ-{maq.id.toString().padStart(2, '0')}</h2>
                  <div className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-slate-900/80 border border-slate-700 ${style.text} text-[10px] font-bold tracking-wider`}>
                    {style.icon}
                    {style.label}
                  </div>
                </div>
                <div className={`w-4 h-4 rounded-full ${style.bg} ${style.glow} border border-slate-900 mt-1`}></div>
              </div>

              <div className="space-y-4">
                <div className="bg-slate-900/50 rounded-lg p-3 border border-slate-800/50 flex justify-between items-center">
                  <span className="text-slate-400 text-xs font-semibold uppercase tracking-wider">T. Ciclo</span>
                  <div className="text-right">
                    <span className="text-xl font-mono text-slate-100 font-bold">{maq.tiempoCiclo ? Number(maq.tiempoCiclo).toFixed(2) : "0.00"}</span>
                    <span className="text-slate-500 text-[10px] ml-1">seg</span>
                  </div>
                </div>
                
                <div className="bg-slate-900/50 rounded-lg p-3 border border-slate-800/50 flex justify-between items-center">
                  <span className="text-slate-400 text-xs font-semibold uppercase tracking-wider">Piezas</span>
                  <div className="text-right">
                    <span className="text-xl font-mono text-cyan-400 font-bold">{maq.piezas}</span>
                    <span className="text-slate-500 text-[10px] ml-1">uds</span>
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* GRÁFICA INFERIOR */}
      <h3 className="text-slate-300 text-sm font-bold uppercase tracking-widest mb-4 flex items-center gap-2 mt-4">
        <Activity size={18} className="text-slate-400"/> Análisis de Tiempos de Ciclo
      </h3>
      <div className="glass-panel rounded-2xl p-6 h-80">
          {maquinas && maquinas.length > 0 ? (
            <Bar data={chartData} options={chartOptions} />
          ) : (
            <div className="h-full flex items-center justify-center text-slate-500 font-medium tracking-wide">
              Esperando datos de planta...
            </div>
          )}
      </div>
    </div>
  );
};

export default DashboardSCADA;