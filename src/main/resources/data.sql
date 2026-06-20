-- data.sql
-- Datos iniciales ficticios

-- Escandallo
INSERT INTO EscandalloArticulo (idArticulo, tiempoTeorico, descripcion) VALUES ('ART-001', 12.5, 'Pieza A');
INSERT INTO EscandalloArticulo (idArticulo, tiempoTeorico, descripcion) VALUES ('ART-002', 15.0, 'Pieza B');
INSERT INTO EscandalloArticulo (idArticulo, tiempoTeorico, descripcion) VALUES ('ART-003', 20.0, 'Pieza C');

-- MoldeMaquina
INSERT INTO MoldeMaquina (idMolde, idMaquina, idArticulo, eficienciaHistorica) VALUES ('MOLDE-A', 'MAQ-01', 'ART-001', 0.95);
INSERT INTO MoldeMaquina (idMolde, idMaquina, idArticulo, eficienciaHistorica) VALUES ('MOLDE-B', 'MAQ-02', 'ART-002', 0.92);

-- Automata_OEE_Bono_Resumen_ANFRA (simulacion)
INSERT INTO Automata_OEE_Bono_Resumen_ANFRA (id, idDocBono, numMaquina, fechaInicio, fechaFin, piezasProducidas, piezasBuenas, piezasRevision, piezasScrap, tiempoCicloTeoricoSeg, tiempoCicloMedioSeg, tiempoCicloStdDevSeg, minutosAutomatico, minutosManual, minutosParo, disponibilidad, rendimiento, calidad, oeeTotal, fechaCalculo)
VALUES ('OEE-1001', 'BONO-1001', 'MAQ-01', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 1025, 1000, 5, 20, 12.0, 12.5, 0.5, 200.0, 10.0, 5.0, 96.0, 95.0, 98.0, 89.0, CURRENT_TIMESTAMP());


-- EstadoActual inicial
INSERT INTO Automata_EstadoActual_ANFRA (numMaquina, idEstado, tiempoCiclo, inyectadas, IdDocBono, fechaInicioEstado, fechaUltimaActualizacion)
VALUES ('MAQ-01', 0, 12.4, 500, 'BONO-1001', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
