# Enlace al exe
 https://drive.google.com/file/d/1HnudRa4l5T2UgN6nNtamtUOakzQsnnan/view?usp=drive_link


Script SQL de la base de datos
-- Borrar y crear base de datos
DROP DATABASE IF EXISTS StardamValley;
CREATE DATABASE StardamValley;
USE StardamValley;

-- Tabla Alimentos
CREATE TABLE Alimentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL
);

-- Tabla Animales
CREATE TABLE animales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    fecha_adquisicion DATETIME NOT NULL,
    haComido TINYINT(1) DEFAULT 0,
    dias_vida INT DEFAULT 0,
    dias_sin_comer INT DEFAULT 0,
    id_alimento INT NOT NULL,
    peso DECIMAL(5,2),  -- Solo aplicable a vacas (y futuros animales con peso)
    FOREIGN KEY (id_alimento) REFERENCES alimentos(id)
);

-- Tabla Transacciones
CREATE TABLE Transacciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_transaccion ENUM('COMPRA', 'VENTA') NOT NULL,
    tipo_elemento ENUM('ANIMAL', 'ALIMENTO', 'SEMILLA') NOT NULL,
    nombre_elemento VARCHAR(50) NOT NULL,
    precio DOUBLE NOT NULL,
    fecha_transaccion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Alimentos iniciales
INSERT INTO Alimentos (nombre, precio) VALUES
('Maiz', 0.50),
('Avena', 0.70),
('Heno', 1.00);
