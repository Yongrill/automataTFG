-- schema.sql
-- Drop tables if they exist for clean initialization
DROP TABLE IF EXISTS Automata_HistoricoProduccion_ANFRA;
DROP TABLE IF EXISTS Automata_EstadoActual_ANFRA;
DROP TABLE IF EXISTS Automata_OEE_Bono_Resumen_ANFRA;
DROP TABLE IF EXISTS EscandalloArticulo;
DROP TABLE IF EXISTS MoldeMaquina;
DROP TABLE IF EXISTS FichajeBono;

CREATE TABLE FichajeBono (
    id VARCHAR(80) PRIMARY KEY,
    idDocBono VARCHAR(30) NOT NULL,
    idEmpleado INT NOT NULL,
    tipoFichaje VARCHAR(20) NOT NULL,
    observaciones VARCHAR(255),
    fechaFichaje DATETIME NOT NULL
);

-- Tabla de OEE / Historico
CREATE TABLE Automata_HistoricoProduccion_ANFRA (
    id VARCHAR(255) PRIMARY KEY,
    numMaquina VARCHAR(50),
    idEstado INT,
    tiempoCicloMedio FLOAT,
    tiempoCicloMax FLOAT,
    tiempoCicloMin FLOAT,
    tiempoCicloStdDev FLOAT,
    inyectadas INT,
    IdDocBono VARCHAR(50),
    fechaEvento DATETIME
);

CREATE TABLE Automata_EstadoActual_ANFRA (
    numMaquina VARCHAR(50) PRIMARY KEY,
    idEstado INT,
    tiempoCiclo FLOAT,
    inyectadas INT,
    IdDocBono VARCHAR(50),
    fechaInicioEstado DATETIME,
    fechaUltimaActualizacion DATETIME
);

-- Tabla ficticia de OEE resumido por bono
CREATE TABLE Automata_OEE_Bono_Resumen_ANFRA (
    id VARCHAR(80) PRIMARY KEY,
    idDocBono VARCHAR(30) NOT NULL,
    numMaquina VARCHAR(20) NOT NULL,
    fechaInicio DATETIME NOT NULL,
    fechaFin DATETIME NOT NULL,
    piezasProducidas INT NOT NULL,
    piezasBuenas INT NOT NULL,
    piezasRevision INT NOT NULL,
    piezasScrap INT NOT NULL,
    tiempoCicloTeoricoSeg FLOAT NOT NULL,
    tiempoCicloMedioSeg FLOAT NOT NULL,
    tiempoCicloStdDevSeg FLOAT NOT NULL,
    minutosAutomatico FLOAT NOT NULL,
    minutosManual FLOAT NOT NULL,
    minutosParo FLOAT NOT NULL,
    disponibilidad FLOAT NOT NULL,
    rendimiento FLOAT NOT NULL,
    calidad FLOAT NOT NULL,
    oeeTotal FLOAT NOT NULL,
    fechaCalculo DATETIME NOT NULL
);

-- Escandallo (tiempo teórico por pieza)
CREATE TABLE EscandalloArticulo (
    idArticulo VARCHAR(50) PRIMARY KEY,
    tiempoTeorico FLOAT,
    descripcion VARCHAR(255)
);

-- Moldes y eficiencia por máquina
CREATE TABLE MoldeMaquina (
    idMolde VARCHAR(50) PRIMARY KEY,
    idMaquina VARCHAR(50),
    idArticulo VARCHAR(50),
    eficienciaHistorica FLOAT
);
