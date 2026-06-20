import React, { useState, useEffect } from 'react';

const DashboardResumenOEE = ({ onBonoSelect }) => {
  const [resumenOee, setResumenOee] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchResumen = async () => {
      try {
        const response = await fetch('/api/oee/resumen');
        if (!response.ok) {
          throw new Error('Error al cargar el resumen de OEE');
        }
        const data = await response.json();
        setResumenOee(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchResumen();
  }, []);

  if (loading) return <div className="text-white p-4">Cargando resumen de OEE...</div>;
  if (error) return <div className="text-red-500 p-4">Error: {error}</div>;

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold text-white mb-4">Resumen OEE de Bonos (Doble clic para analizar)</h2>
      <div className="overflow-x-auto bg-gray-800 rounded-lg shadow">
        <table className="min-w-full divide-y divide-gray-700">
          <thead className="bg-gray-900">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Bono</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Máquina</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">P. Buenas</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">P. Scrap</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Disponibilidad</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Rendimiento</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">Calidad</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-300 uppercase tracking-wider">OEE Total</th>
            </tr>
          </thead>
          <tbody className="bg-gray-800 divide-y divide-gray-700">
            {resumenOee.map((item) => (
              <tr 
                key={item.id} 
                className="hover:bg-gray-700 transition-colors cursor-pointer"
                onDoubleClick={() => onBonoSelect && onBonoSelect(item.idDocBono)}
              >
                <td className="px-6 py-4 whitespace-nowrap text-sm text-white font-medium">{item.idDocBono}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{item.numMaquina}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{item.piezasBuenas}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{item.piezasScrap}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{item.disponibilidad.toFixed(2)}%</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{item.rendimiento.toFixed(2)}%</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{item.calidad.toFixed(2)}%</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-bold text-indigo-400">{item.oeeTotal.toFixed(2)}%</td>
              </tr>
            ))}
            {resumenOee.length === 0 && (
              <tr>
                <td colSpan="8" className="px-6 py-4 whitespace-nowrap text-sm text-gray-400 text-center">
                  No hay datos de OEE disponibles.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default DashboardResumenOEE;