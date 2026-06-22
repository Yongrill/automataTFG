import React, { useState, useEffect, useCallback } from 'react';
import { Bar, Doughnut } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
} from 'chart.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
);

export default function DashboardOEE({ initialBonoId }) {
  const [searchMode, setSearchMode] = useState('bono'); // 'bono', 'orden', 'maquina'
  const [bonoId, setBonoId] = useState('');
  const [ordenId, setOrdenId] = useState('');
  const [maquinaId, setMaquinaId] = useState('');
  const [oeeData, setOeeData] = useState(null);
  const [fichajes, setFichajes] = useState([]);
  const [oeeListByOrden, setOeeListByOrden] = useState(null);
  const [oeeListByMaquina, setOeeListByMaquina] = useState(null);
  const [expandedBono, setExpandedBono] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const loadBonoData = useCallback(async (id) => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`/api/oee/bono/${id}`);
      if (!response.ok) {
        if (response.status === 404) throw new Error('Bono no encontrado o no tiene datos.');
        throw new Error('Error al obtener datos');
      }
      const data = await response.json();
      setOeeData(data);
      setOeeListByOrden(null);
      setOeeListByMaquina(null);

      // Fetch fichajes
      try {
        const resFichajes = await fetch(`/api/fichajes/bono/${id}`);
        if (resFichajes.ok) {
          const dataFichajes = await resFichajes.json();
          setFichajes(dataFichajes);
        }
      } catch (errFichajes) {
        console.error("Error cargando fichajes", errFichajes);
      }

    } catch (err) {
      setError(err.message);
      setOeeData(null);
      setFichajes([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (initialBonoId) {
      setSearchMode('bono');
      setBonoId(initialBonoId);
      loadBonoData(initialBonoId);
    }
  }, [initialBonoId, loadBonoData]);

  const fetchOEEBono = async (e) => {
    e.preventDefault();
    if (!bonoId.trim()) return;
    loadBonoData(bonoId);
  };

  const fetchOEEByOrden = async (e) => {
    e.preventDefault();
    if (!ordenId.trim()) return;

    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`/api/oee/orden/${ordenId}`);
      if (!response.ok) throw new Error('Error al obtener datos de orden');
      const data = await response.json();
      setOeeListByOrden(data);
      setOeeData(null);
      setOeeListByMaquina(null);
      setExpandedBono(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const fetchOEEByMaquina = async (e) => {
    e.preventDefault();
    if (!maquinaId.trim()) return;

    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`/api/oee/maquina/${maquinaId}`);
      if (!response.ok) throw new Error('Error al obtener datos de máquina');
      const data = await response.json();
      setOeeListByMaquina(data);
      setOeeData(null);
      setOeeListByOrden(null);
      setExpandedBono(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const getKPIColor = (value) => {
    if (value >= 85) return 'text-green-400';
    if (value >= 65) return 'text-yellow-400';
    return 'text-red-500';
  };

  const getFichajeBadgeColor = (tipo) => {
    switch (tipo.toLowerCase()) {
      case 'verde': return 'bg-green-500 text-white';
      case 'amarillo': return 'bg-yellow-500 text-black';
      case 'rojo': return 'bg-red-500 text-white';
      case 'pausa': return 'bg-orange-500 text-white';
      case 'fin_pausa': return 'bg-blue-500 text-white';
      default: return 'bg-gray-500 text-white';
    }
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        labels: { color: '#e5e7eb' }
      }
    },
    scales: {
      y: { ticks: { color: '#9ca3af' }, grid: { color: '#374151' } },
      x: { ticks: { color: '#9ca3af' }, grid: { color: '#374151' } }
    }
  };

  const doughnutOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { labels: { color: '#e5e7eb' } }
    },
    cutout: '70%',
  };

  const renderBonoCard = (bono, index) => {
    const isExpanded = expandedBono === index;
    return (
      <div key={index} className="bg-gray-800 p-6 rounded-lg shadow-lg border-l-4 border-blue-500 mb-4">
        <button 
          onClick={() => setExpandedBono(isExpanded ? null : index)}
          className="w-full flex justify-between items-center"
        >
          <div className="flex justify-between items-center flex-grow pr-4">
            <div>
              <p className="text-xs text-gray-400 uppercase tracking-widest">ID Documento Bono</p>
              <p className="text-lg font-bold">{bono.idDocBono || 'N/A'}</p>
            </div>
            <div>
              <p className="text-xs text-gray-400 uppercase tracking-widest">Máquina</p>
              <p className="text-lg font-bold text-blue-300">{bono.idMaquina || 'N/A'}</p>
            </div>
            <div>
              <p className="text-xs text-gray-400 uppercase tracking-widest">Lote</p>
              <p className="text-lg font-bold text-gray-300">{bono.numLote || 'N/A'}</p>
            </div>
            <div>
              <p className="text-xs text-gray-400 uppercase tracking-widest">OEE Total</p>
              <p className={`text-2xl font-extrabold ${getKPIColor(bono.oeeTotal)}`}>
                {bono.oeeTotal.toFixed(2)}%
              </p>
            </div>
          </div>
          <span className="text-gray-400 text-2xl">
            {isExpanded ? '▼' : '▶'}
          </span>
        </button>

        {isExpanded && (
          <div className="mt-6 space-y-6 border-t border-gray-700 pt-6">
            {/* KPI Totales */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="bg-gray-900 p-4 rounded">
                <p className="text-gray-400 text-sm font-semibold uppercase tracking-widest mb-1">Disponibilidad</p>
                <p className={`text-2xl font-bold ${getKPIColor(bono.disponibilidad)}`}>
                  {bono.disponibilidad.toFixed(2)}%
                </p>
              </div>
              <div className="bg-gray-900 p-4 rounded">
                <p className="text-gray-400 text-sm font-semibold uppercase tracking-widest mb-1">Rendimiento</p>
                <p className={`text-2xl font-bold ${getKPIColor(bono.rendimiento)}`}>
                  {bono.rendimiento.toFixed(2)}%
                </p>
              </div>
              <div className="bg-gray-900 p-4 rounded">
                <p className="text-gray-400 text-sm font-semibold uppercase tracking-widest mb-1">Calidad</p>
                <p className={`text-2xl font-bold ${getKPIColor(bono.calidad)}`}>
                  {bono.calidad.toFixed(2)}%
                </p>
              </div>
            </div>

            {/* Desglose Turnos */}
            {bono.turnos && bono.turnos.length > 0 && (
              <div>
                <h4 className="text-gray-300 font-semibold uppercase tracking-wide mb-4">Desglose por Turno</h4>
                <div className="space-y-3">
                  {bono.turnos.map((turno, tIdx) => (
                    <div key={tIdx} className="bg-gray-700 p-4 rounded border-l-2 border-indigo-400">
                      <p className="text-indigo-300 font-bold text-sm uppercase tracking-widest mb-3">
                        {turno.turno}
                      </p>
                      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-sm">
                        <div>
                          <p className="text-gray-400">OEE</p>
                          <p className={`font-bold text-lg ${getKPIColor(turno.oeeTotal)}`}>
                            {turno.oeeTotal.toFixed(2)}%
                          </p>
                        </div>
                        <div>
                          <p className="text-gray-400">Dispo.</p>
                          <p className="font-bold text-white">{turno.disponibilidad.toFixed(2)}%</p>
                        </div>
                        <div>
                          <p className="text-gray-400">Rend.</p>
                          <p className="font-bold text-white">{turno.rendimiento.toFixed(2)}%</p>
                        </div>
                        <div>
                          <p className="text-gray-400">Calidad</p>
                          <p className="font-bold text-white">{turno.calidad.toFixed(2)}%</p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Gráfico OEE Desglose */}
            <div className="bg-gray-900 p-4 rounded">
              <h4 className="text-gray-300 font-semibold mb-3 uppercase tracking-wide">Desglose de OEE</h4>
              <div className="h-64">
                <Bar 
                  options={chartOptions} 
                  data={{
                    labels: ['Disponibilidad', 'Rendimiento', 'Calidad', 'OEE Final'],
                    datasets: [{
                      label: '% Porcentaje',
                      data: [bono.disponibilidad, bono.rendimiento, bono.calidad, bono.oeeTotal],
                      backgroundColor: [
                        'rgba(59, 130, 246, 0.8)',
                        'rgba(16, 185, 129, 0.8)',
                        'rgba(245, 158, 11, 0.8)',
                        'rgba(99, 102, 241, 0.9)'
                      ],
                      borderColor: [
                        'rgb(59, 130, 246)',
                        'rgb(16, 185, 129)',
                        'rgb(245, 158, 11)',
                        'rgb(99, 102, 241)'
                      ],
                      borderWidth: 1
                    }]
                  }} 
                />
              </div>
            </div>

            {/* Gráficos Inyectadas y Tiempo */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
              <div className="bg-gray-900 p-4 rounded flex flex-col items-center">
                <h4 className="text-gray-300 font-semibold mb-3 uppercase tracking-wide">Inyectadas: Teoría vs Real</h4>
                <div className="h-48 w-full max-w-xs relative">
                  <Doughnut 
                    options={doughnutOptions}
                    data={{
                      labels: ['Iny. Reales', 'Brecha Perdida'],
                      datasets: [{
                        data: [
                          bono.inyectadasReales, 
                          Math.max(0, bono.inyectadasTeoricas - bono.inyectadasReales)
                        ],
                        backgroundColor: ['rgba(16, 185, 129, 0.8)', 'rgba(239, 68, 68, 0.8)'],
                        borderWidth: 0,
                      }]
                    }}
                  />
                </div>
                <div className="mt-3 text-center w-full text-sm">
                  <p className="text-gray-400">Teóricas: <span className="text-white font-mono">{bono.inyectadasTeoricas}</span></p>
                  <p className="text-gray-400">Reales: <span className="text-green-400 font-mono">{bono.inyectadasReales}</span></p>
                </div>
              </div>

              <div className="bg-gray-900 p-4 rounded flex flex-col items-center">
                <h4 className="text-gray-300 font-semibold mb-3 uppercase tracking-wide">Tiempo (Minutos)</h4>
                <div className="h-48 w-full max-w-xs relative">
                  <Doughnut 
                    options={doughnutOptions}
                    data={{
                      labels: ['Min Automático', 'Min Paro/Manual'],
                      datasets: [{
                        data: [
                          bono.minutosEnAutomatico, 
                          Math.max(0, bono.minutosTotalesAutomata - bono.minutosEnAutomatico)
                        ],
                        backgroundColor: ['rgba(59, 130, 246, 0.8)', 'rgba(245, 158, 11, 0.8)'],
                        borderWidth: 0,
                      }]
                    }}
                  />
                </div>
                <div className="mt-3 text-center w-full text-sm">
                  <p className="text-gray-400">Total SCADA: <span className="text-white font-mono">{bono.minutosTotalesAutomata.toFixed(1)}</span></p>
                  <p className="text-gray-400">Auto: <span className="text-blue-400 font-mono">{bono.minutosEnAutomatico.toFixed(1)}</span></p>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    );
  };

  return (
    <div className="p-6 text-white min-h-full">
      <header className="mb-8 border-b border-gray-800 pb-4">
        <h1 className="text-3xl font-extrabold tracking-widest text-white uppercase shadow-sm">
          ANÁLISIS OEE (Overall Equipment Effectiveness)
        </h1>
      </header>

      {/* Tabs de Búsqueda */}
      <div className="mb-8 flex gap-2 border-b border-gray-700">
        <button
          onClick={() => setSearchMode('bono')}
          className={`px-6 py-3 font-semibold uppercase text-sm tracking-widest transition-colors ${
            searchMode === 'bono'
              ? 'border-b-2 border-blue-500 text-blue-400 bg-gray-800'
              : 'text-gray-400 hover:text-white'
          }`}
        >
          Por Bono
        </button>
        <button
          onClick={() => setSearchMode('orden')}
          className={`px-6 py-3 font-semibold uppercase text-sm tracking-widest transition-colors ${
            searchMode === 'orden'
              ? 'border-b-2 border-blue-500 text-blue-400 bg-gray-800'
              : 'text-gray-400 hover:text-white'
          }`}
        >
          Por Orden
        </button>
        <button
          onClick={() => setSearchMode('maquina')}
          className={`px-6 py-3 font-semibold uppercase text-sm tracking-widest transition-colors ${
            searchMode === 'maquina'
              ? 'border-b-2 border-blue-500 text-blue-400 bg-gray-800'
              : 'text-gray-400 hover:text-white'
          }`}
        >
          Por Máquina
        </button>
      </div>

      {/* Formularios de Búsqueda */}
      {searchMode === 'bono' && (
        <form onSubmit={fetchOEEBono} className="mb-8 bg-gray-800 p-4 rounded-lg shadow-md flex gap-4 items-end max-w-xl">
          <div className="flex-grow">
            <label className="block text-gray-400 text-xs font-semibold mb-2 uppercase">ID Documento Bono</label>
            <input 
              type="text" 
              value={bonoId}
              onChange={(e) => setBonoId(e.target.value)}
              className="w-full bg-gray-900 border border-gray-700 rounded px-4 py-2 text-white focus:outline-none focus:border-blue-500"
              placeholder="Ej: BONO-12345"
              required
            />
          </div>
          <button 
            type="submit" 
            disabled={loading}
            className="bg-blue-600 hover:bg-blue-500 text-white font-bold py-2 px-6 rounded transition-colors disabled:opacity-50"
          >
            {loading ? 'Cargando...' : 'Analizar'}
          </button>
        </form>
      )}

      {searchMode === 'orden' && (
        <form onSubmit={fetchOEEByOrden} className="mb-8 bg-gray-800 p-4 rounded-lg shadow-md flex gap-4 items-end max-w-xl">
          <div className="flex-grow">
            <label className="block text-gray-400 text-xs font-semibold mb-2 uppercase">ID Orden de Producción</label>
            <input 
              type="number" 
              value={ordenId}
              onChange={(e) => setOrdenId(e.target.value)}
              className="w-full bg-gray-900 border border-gray-700 rounded px-4 py-2 text-white focus:outline-none focus:border-blue-500"
              placeholder="Ej: 2001"
              required
            />
          </div>
          <button 
            type="submit" 
            disabled={loading}
            className="bg-blue-600 hover:bg-blue-500 text-white font-bold py-2 px-6 rounded transition-colors disabled:opacity-50"
          >
            {loading ? 'Cargando...' : 'Buscar'}
          </button>
        </form>
      )}

      {searchMode === 'maquina' && (
        <form onSubmit={fetchOEEByMaquina} className="mb-8 bg-gray-800 p-4 rounded-lg shadow-md flex gap-4 items-end max-w-xl">
          <div className="flex-grow">
            <label className="block text-gray-400 text-xs font-semibold mb-2 uppercase">ID Máquina (Matrícula)</label>
            <input 
              type="text" 
              value={maquinaId}
              onChange={(e) => setMaquinaId(e.target.value)}
              className="w-full bg-gray-900 border border-gray-700 rounded px-4 py-2 text-white focus:outline-none focus:border-blue-500"
              placeholder="Ej: MAQ-01"
              required
            />
          </div>
          <button 
            type="submit" 
            disabled={loading}
            className="bg-blue-600 hover:bg-blue-500 text-white font-bold py-2 px-6 rounded transition-colors disabled:opacity-50"
          >
            {loading ? 'Cargando...' : 'Buscar'}
          </button>
        </form>
      )}

      {error && <p className="text-red-500 mb-4 text-lg font-semibold">{error}</p>}

      {/* Resultado Búsqueda por Bono */}
      {searchMode === 'bono' && oeeData && (
        <div className="space-y-8 animate-in fade-in duration-500">
          <div className="bg-gray-800 p-4 rounded-lg shadow-lg border-l-4 border-blue-500 flex justify-between items-center">
            <div>
              <p className="text-xs text-gray-400 uppercase tracking-widest">Máquina</p>
              <p className="text-xl font-bold">{oeeData.idMaquina || 'N/A'}</p>
            </div>
            <div>
              <p className="text-xs text-gray-400 uppercase tracking-widest">Lote</p>
              <p className="text-xl font-bold text-blue-300">{oeeData.numLote || 'N/A'}</p>
            </div>
            <div>
              <p className="text-xs text-gray-400 uppercase tracking-widest">Artículo</p>
              <p className="text-xl font-bold text-gray-300">{oeeData.idArticulo || 'N/A'}</p>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
            <div className="bg-gray-800 p-6 rounded-lg shadow-lg flex flex-col justify-center items-center h-32 border-b-4 border-indigo-500">
              <h2 className="text-gray-400 text-sm font-semibold uppercase tracking-widest mb-2">OEE Total</h2>
              <p className={`text-4xl font-extrabold ${getKPIColor(oeeData.oeeTotal)}`}>
                {oeeData.oeeTotal.toFixed(2)}%
              </p>
            </div>
            <div className="bg-gray-800 p-6 rounded-lg shadow-lg flex flex-col justify-center items-center h-32">
              <h2 className="text-gray-400 text-sm font-semibold uppercase tracking-widest mb-2">Disponibilidad</h2>
              <p className={`text-3xl font-bold ${getKPIColor(oeeData.disponibilidad)}`}>
                {oeeData.disponibilidad.toFixed(2)}%
              </p>
            </div>
            <div className="bg-gray-800 p-6 rounded-lg shadow-lg flex flex-col justify-center items-center h-32">
              <h2 className="text-gray-400 text-sm font-semibold uppercase tracking-widest mb-2">Rendimiento</h2>
              <p className={`text-3xl font-bold ${getKPIColor(oeeData.rendimiento)}`}>
                {oeeData.rendimiento.toFixed(2)}%
              </p>
            </div>
            <div className="bg-gray-800 p-6 rounded-lg shadow-lg flex flex-col justify-center items-center h-32">
              <h2 className="text-gray-400 text-sm font-semibold uppercase tracking-widest mb-2">Calidad</h2>
              <p className={`text-3xl font-bold ${getKPIColor(oeeData.calidad)}`}>
                {oeeData.calidad.toFixed(2)}%
              </p>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            <div className="bg-gray-800 p-6 rounded-lg shadow-lg">
              <h3 className="text-gray-300 font-semibold mb-4 uppercase tracking-wide">Desglose de OEE</h3>
              <div className="h-64">
                <Bar 
                  options={chartOptions} 
                  data={{
                    labels: ['Disponibilidad', 'Rendimiento', 'Calidad', 'OEE Final'],
                    datasets: [{
                      label: '% Porcentaje',
                      data: [oeeData.disponibilidad, oeeData.rendimiento, oeeData.calidad, oeeData.oeeTotal],
                      backgroundColor: [
                        'rgba(59, 130, 246, 0.8)',
                        'rgba(16, 185, 129, 0.8)',
                        'rgba(245, 158, 11, 0.8)',
                        'rgba(99, 102, 241, 0.9)'
                      ],
                      borderColor: [
                        'rgb(59, 130, 246)',
                        'rgb(16, 185, 129)',
                        'rgb(245, 158, 11)',
                        'rgb(99, 102, 241)'
                      ],
                      borderWidth: 1
                    }]
                  }} 
                />
              </div>
            </div>

            <div className="bg-gray-800 p-6 rounded-lg shadow-lg flex flex-col sm:flex-row gap-4">
              <div className="w-full sm:w-1/2 flex flex-col items-center">
                <h3 className="text-gray-300 font-semibold mb-4 text-center uppercase tracking-wide">Inyectadas: Teoría vs Real</h3>
                <div className="h-48 w-full max-w-xs relative">
                  <Doughnut 
                    options={doughnutOptions}
                    data={{
                      labels: ['Iny. Reales', 'Brecha Perdida'],
                      datasets: [{
                        data: [
                          oeeData.inyectadasReales, 
                          Math.max(0, oeeData.inyectadasTeoricas - oeeData.inyectadasReales)
                        ],
                        backgroundColor: ['rgba(16, 185, 129, 0.8)', 'rgba(239, 68, 68, 0.8)'],
                        borderWidth: 0,
                      }]
                    }}
                  />
                </div>
                <div className="mt-4 text-center w-full">
                  <p className="text-sm text-gray-400">Teóricas: <span className="text-white font-mono">{oeeData.inyectadasTeoricas}</span></p>
                  <p className="text-sm text-gray-400">Reales: <span className="text-green-400 font-mono">{oeeData.inyectadasReales}</span></p>
                </div>
              </div>

              <div className="w-full sm:w-1/2 flex flex-col items-center">
                <h3 className="text-gray-300 font-semibold mb-4 text-center uppercase tracking-wide">Tiempo (Minutos)</h3>
                <div className="h-48 w-full max-w-xs relative">
                  <Doughnut 
                    options={doughnutOptions}
                    data={{
                      labels: ['Min Automático', 'Min Paro/Manual'],
                      datasets: [{
                        data: [
                          oeeData.minutosEnAutomatico, 
                          Math.max(0, oeeData.minutosTotalesAutomata - oeeData.minutosEnAutomatico)
                        ],
                        backgroundColor: ['rgba(59, 130, 246, 0.8)', 'rgba(245, 158, 11, 0.8)'],
                        borderWidth: 0,
                      }]
                    }}
                  />
                </div>
                <div className="mt-4 text-center w-full">
                  <p className="text-sm text-gray-400">Total SCADA: <span className="text-white font-mono">{oeeData.minutosTotalesAutomata.toFixed(1)}</span></p>
                  <p className="text-sm text-gray-400">Auto (≈ Dispo): <span className="text-blue-400 font-mono">{oeeData.minutosEnAutomatico.toFixed(1)}</span></p>
                </div>
              </div>
            </div>
          </div>

          {/* Tabla de Fichajes de Operarios */}
          {fichajes.length > 0 && (
            <div className="bg-gray-800 p-6 rounded-lg shadow-lg border-t-4 border-yellow-500">
              <h3 className="text-xl font-bold text-white mb-4 uppercase tracking-wide">Histórico de Fichajes (Operarios / Pausas)</h3>
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-700">
                  <thead className="bg-gray-900">
                    <tr>
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Fecha / Hora</th>
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Empleado ID</th>
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Tipo Fichaje</th>
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Observaciones</th>
                    </tr>
                  </thead>
                  <tbody className="bg-gray-800 divide-y divide-gray-700">
                    {fichajes.map((fichaje) => (
                      <tr key={fichaje.id} className="hover:bg-gray-700 transition-colors">
                        <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-300">
                          {new Date(fichaje.fechaFichaje).toLocaleString()}
                        </td>
                        <td className="px-4 py-3 whitespace-nowrap text-sm text-white font-mono">
                          EMP-{String(fichaje.idEmpleado).padStart(2, '0')}
                        </td>
                        <td className="px-4 py-3 whitespace-nowrap">
                          <span className={`px-3 py-1 inline-flex text-xs leading-5 font-semibold rounded-full uppercase ${getFichajeBadgeColor(fichaje.tipoFichaje)}`}>
                            {fichaje.tipoFichaje}
                          </span>
                        </td>
                        <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-400">
                          {fichaje.observaciones || '-'}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      )}

      {/* Resultados Búsqueda por Orden */}
      {searchMode === 'orden' && oeeListByOrden && (
        <div className="space-y-6 animate-in fade-in duration-500">
          <div className="bg-indigo-900 p-4 rounded-lg shadow-lg border-l-4 border-indigo-400">
            <p className="text-gray-300 text-sm uppercase tracking-widest">Orden Nº</p>
            <p className="text-2xl font-bold text-indigo-200">{ordenId}</p>
            <p className="text-sm text-gray-400 mt-2">{oeeListByOrden.length} bono(s) encontrado(s)</p>
          </div>
          {oeeListByOrden.length > 0 ? (
            oeeListByOrden.map((bono, idx) => renderBonoCard(bono, idx))
          ) : (
            <p className="text-gray-400 text-center py-8">No se encontraron bonos para esta orden.</p>
          )}
        </div>
      )}

      {/* Resultados Búsqueda por Máquina */}
      {searchMode === 'maquina' && oeeListByMaquina && (
        <div className="space-y-6 animate-in fade-in duration-500">
          <div className="bg-purple-900 p-4 rounded-lg shadow-lg border-l-4 border-purple-400">
            <p className="text-gray-300 text-sm uppercase tracking-widest">Máquina (Matrícula)</p>
            <p className="text-2xl font-bold text-purple-200">{maquinaId}</p>
            <p className="text-sm text-gray-400 mt-2">{oeeListByMaquina.length} bono(s) encontrado(s)</p>
          </div>
          {oeeListByMaquina.length > 0 ? (
            oeeListByMaquina.map((bono, idx) => renderBonoCard(bono, idx))
          ) : (
            <p className="text-gray-400 text-center py-8">No se encontraron bonos para esta máquina.</p>
          )}
        </div>
      )}
    </div>
  );
}
