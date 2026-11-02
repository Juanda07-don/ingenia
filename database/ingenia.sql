-- MySQL dump 10.13  Distrib 8.4.9, for Win64 (x86_64)
--
-- Host: localhost    Database: ingenia
-- ------------------------------------------------------
-- Server version	8.4.9

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `asesoria`
--

DROP TABLE IF EXISTS `asesoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asesoria` (
  `id_asesoria` bigint NOT NULL AUTO_INCREMENT,
  `estudiante_id` bigint NOT NULL,
  `profesor_id` bigint NOT NULL,
  `materia_id` bigint NOT NULL,
  `disponibilidad_id` bigint NOT NULL,
  `fecha` date NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fin` time NOT NULL,
  `hora_ingreso` time DEFAULT NULL,
  `hora_salida` time DEFAULT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'AGENDADA',
  `disponibilidad_propuesta_id` bigint DEFAULT NULL,
  `motivo_reprogramacion` varchar(255) DEFAULT NULL,
  `hora_llegada` datetime DEFAULT NULL,
  PRIMARY KEY (`id_asesoria`),
  KEY `estudiante_id` (`estudiante_id`),
  KEY `profesor_id` (`profesor_id`),
  KEY `materia_id` (`materia_id`),
  KEY `disponibilidad_id` (`disponibilidad_id`),
  KEY `fk_asesoria_propuesta` (`disponibilidad_propuesta_id`),
  CONSTRAINT `asesoria_ibfk_1` FOREIGN KEY (`estudiante_id`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `asesoria_ibfk_2` FOREIGN KEY (`profesor_id`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `asesoria_ibfk_3` FOREIGN KEY (`materia_id`) REFERENCES `materia` (`id_materia`),
  CONSTRAINT `asesoria_ibfk_4` FOREIGN KEY (`disponibilidad_id`) REFERENCES `disponibilidad` (`id_disponibilidad`),
  CONSTRAINT `fk_asesoria_propuesta` FOREIGN KEY (`disponibilidad_propuesta_id`) REFERENCES `disponibilidad` (`id_disponibilidad`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asesoria`
--

LOCK TABLES `asesoria` WRITE;
/*!40000 ALTER TABLE `asesoria` DISABLE KEYS */;
INSERT INTO `asesoria` VALUES (1,1,2,1,2,'2026-10-27','09:15:00','09:30:00','09:15:00','09:30:00','FINALIZADA',NULL,NULL,NULL),(2,1,2,1,3,'2026-10-27','09:30:00','09:45:00','09:30:00','09:45:00','FINALIZADA',NULL,NULL,'2026-10-26 17:13:40'),(3,1,2,1,7,'2026-10-30','09:00:00','09:30:00',NULL,NULL,'AGENDADA',NULL,NULL,NULL);
/*!40000 ALTER TABLE `asesoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `disponibilidad`
--

DROP TABLE IF EXISTS `disponibilidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `disponibilidad` (
  `id_disponibilidad` bigint NOT NULL AUTO_INCREMENT,
  `profesor_id` bigint NOT NULL,
  `materia_id` bigint NOT NULL,
  `fecha` date NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fin` time NOT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'LIBRE',
  PRIMARY KEY (`id_disponibilidad`),
  KEY `profesor_id` (`profesor_id`),
  KEY `materia_id` (`materia_id`),
  CONSTRAINT `disponibilidad_ibfk_1` FOREIGN KEY (`profesor_id`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `disponibilidad_ibfk_2` FOREIGN KEY (`materia_id`) REFERENCES `materia` (`id_materia`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `disponibilidad`
--

LOCK TABLES `disponibilidad` WRITE;
/*!40000 ALTER TABLE `disponibilidad` DISABLE KEYS */;
INSERT INTO `disponibilidad` VALUES (1,2,1,'2026-10-27','09:00:00','09:15:00','LIBRE'),(2,2,1,'2026-10-27','09:15:00','09:30:00','OCUPADA'),(3,2,1,'2026-10-27','09:30:00','09:45:00','OCUPADA'),(5,2,1,'2026-10-29','14:00:00','14:30:00','LIBRE'),(6,2,1,'2026-10-29','15:30:00','16:00:00','LIBRE'),(7,2,1,'2026-10-30','09:00:00','09:30:00','OCUPADA'),(8,2,1,'2026-10-30','10:00:00','10:30:00','LIBRE'),(9,2,1,'2026-10-30','11:00:00','11:30:00','LIBRE'),(10,2,1,'2026-10-29','16:00:00','16:15:00','LIBRE'),(11,2,1,'2026-10-29','16:15:00','16:30:00','LIBRE');
/*!40000 ALTER TABLE `disponibilidad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `materia`
--

DROP TABLE IF EXISTS `materia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `materia` (
  `id_materia` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id_materia`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `materia`
--

LOCK TABLES `materia` WRITE;
/*!40000 ALTER TABLE `materia` DISABLE KEYS */;
INSERT INTO `materia` VALUES (1,'C├ílculo integral'),(2,'F├¡sica mec├ínica');
/*!40000 ALTER TABLE `materia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profesor_materia`
--

DROP TABLE IF EXISTS `profesor_materia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profesor_materia` (
  `profesor_id` bigint NOT NULL,
  `materia_id` bigint NOT NULL,
  PRIMARY KEY (`profesor_id`,`materia_id`),
  KEY `materia_id` (`materia_id`),
  CONSTRAINT `profesor_materia_ibfk_1` FOREIGN KEY (`profesor_id`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `profesor_materia_ibfk_2` FOREIGN KEY (`materia_id`) REFERENCES `materia` (`id_materia`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profesor_materia`
--

LOCK TABLES `profesor_materia` WRITE;
/*!40000 ALTER TABLE `profesor_materia` DISABLE KEYS */;
INSERT INTO `profesor_materia` VALUES (2,1);
/*!40000 ALTER TABLE `profesor_materia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reporte`
--

DROP TABLE IF EXISTS `reporte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reporte` (
  `id_reporte` bigint NOT NULL AUTO_INCREMENT,
  `asesoria_id` bigint NOT NULL,
  `fecha_generacion` datetime NOT NULL,
  `tema_tratado` varchar(255) DEFAULT NULL,
  `observaciones` varchar(500) DEFAULT NULL,
  `compromisos` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id_reporte`),
  UNIQUE KEY `asesoria_id` (`asesoria_id`),
  CONSTRAINT `reporte_ibfk_1` FOREIGN KEY (`asesoria_id`) REFERENCES `asesoria` (`id_asesoria`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reporte`
--

LOCK TABLES `reporte` WRITE;
/*!40000 ALTER TABLE `reporte` DISABLE KEYS */;
INSERT INTO `reporte` VALUES (1,1,'2026-10-26 17:23:14','L├¡mites',NULL,'Ejercicios 1 al 5'),(2,2,'2026-10-30 14:54:07','Integrales por partes','','Ejercicios 1 al 5 del libro');
/*!40000 ALTER TABLE `reporte` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `correo_institucional` varchar(120) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `rol` varchar(20) NOT NULL,
  `cubiculo` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `correo_institucional` (`correo_institucional`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'Valentina Garc├¡a','valentina@uao.edu.co','$2a$10$XkeejTUH1DZDuvz0j/qr/O5MshsYwKlc6hqywBODvHmxdmJ0y6a0e','ESTUDIANTE',NULL),(2,'Laura M├®ndez','laura@uao.edu.co','$2a$10$XkeejTUH1DZDuvz0j/qr/O5MshsYwKlc6hqywBODvHmxdmJ0y6a0e','PROFESOR','Edificio A ┬À Cub├¡culo 204');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-02 11:11:49
