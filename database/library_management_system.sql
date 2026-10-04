-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: library_management_system
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `books`
--

DROP TABLE IF EXISTS `books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `books` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `book_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `author` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `publisher` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `publication_year` int DEFAULT NULL,
  `category` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quantity` int NOT NULL,
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `price` decimal(15,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `book_id` (`book_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `books`
--

LOCK TABLES `books` WRITE;
/*!40000 ALTER TABLE `books` DISABLE KEYS */;
INSERT INTO `books` VALUES (1,'B001','Java Programming','Nguyen Van A','NXB Tre',2024,'Programming',5,'IN_STOCK',0,120000.00),(2,'B002','Spring Boot Basics','Tran Van B','NXB Lao Dong',2025,'Programming',4,'IN_STOCK',0,150000.00),(3,'B003','Python','Vu Thanh Chuc','NXB Mo Lao',2026,'Programming',7,'IN_STOCK',0,200000.00);
/*!40000 ALTER TABLE `books` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `borrow_details`
--

DROP TABLE IF EXISTS `borrow_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `borrow_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `borrow_id` bigint NOT NULL,
  `book_id` bigint NOT NULL,
  `quantity` int NOT NULL,
  `returned_quantity` int NOT NULL DEFAULT '0',
  `damaged_quantity` int NOT NULL DEFAULT '0',
  `lost_quantity` int NOT NULL DEFAULT '0',
  `fee` decimal(15,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `fk_borrow_detail_borrow` (`borrow_id`),
  KEY `fk_borrow_detail_book` (`book_id`),
  CONSTRAINT `fk_borrow_detail_book` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
  CONSTRAINT `fk_borrow_detail_borrow` FOREIGN KEY (`borrow_id`) REFERENCES `borrows` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `borrow_details`
--

LOCK TABLES `borrow_details` WRITE;
/*!40000 ALTER TABLE `borrow_details` DISABLE KEYS */;
INSERT INTO `borrow_details` VALUES (1,1,1,2,0,0,0,0.00),(2,1,2,1,0,0,0,0.00),(3,2,1,4,0,0,0,0.00),(4,2,2,1,0,0,0,0.00),(5,3,2,1,0,0,0,0.00),(6,3,3,2,0,0,0,0.00),(7,3,1,1,0,0,0,0.00),(8,4,2,1,0,0,0,0.00),(9,5,1,9,0,0,0,0.00),(10,6,2,1,0,0,0,0.00),(11,6,1,9,0,0,0,0.00),(12,7,2,3,0,0,0,0.00),(13,8,2,4,0,0,0,0.00),(14,9,2,3,0,0,0,0.00),(15,10,1,1,1,0,0,0.00),(16,11,2,1,0,1,0,50000.00),(17,12,2,1,0,0,1,100000.00),(18,13,2,1,0,1,0,50000.00),(19,14,2,1,0,1,0,50000.00),(20,15,2,1,0,0,1,100000.00),(21,16,2,1,1,0,0,0.00),(22,17,2,1,1,0,0,0.00),(23,17,1,1,1,0,0,0.00),(24,18,2,1,0,0,1,100000.00),(25,19,1,4,3,1,0,50000.00),(26,20,1,4,2,2,0,100000.00),(27,21,1,3,1,1,1,150000.00),(28,22,2,3,1,1,1,250000.00),(29,23,3,5,3,2,0,266000.00),(30,24,2,1,1,0,0,0.00),(31,25,2,3,2,1,0,100000.00),(32,25,3,2,1,0,1,200000.00),(33,26,3,3,3,0,0,0.00),(34,27,2,1,1,0,0,0.00),(35,27,1,2,2,0,0,0.00),(36,28,1,4,2,2,0,160000.00),(37,28,2,1,1,0,0,0.00),(38,29,2,1,1,0,0,0.00),(39,29,1,1,1,0,0,0.00),(40,30,2,2,0,0,0,0.00),(41,30,1,2,0,0,0,0.00),(42,31,2,1,1,0,0,0.00),(43,31,1,2,2,0,0,0.00),(44,32,2,1,1,0,0,0.00),(45,32,1,1,1,0,0,0.00),(46,33,2,1,0,0,0,0.00),(47,33,1,1,0,0,0,0.00);
/*!40000 ALTER TABLE `borrow_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `borrows`
--

DROP TABLE IF EXISTS `borrows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `borrows` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `borrow_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `reader_id` bigint NOT NULL,
  `borrow_date` date NOT NULL,
  `due_date` date NOT NULL,
  `return_date` date DEFAULT NULL,
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `total_fee` decimal(15,2) NOT NULL DEFAULT '0.00',
  `payment_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'UNPAID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `borrow_id` (`borrow_id`),
  KEY `fk_borrow_reader` (`reader_id`),
  CONSTRAINT `fk_borrow_reader` FOREIGN KEY (`reader_id`) REFERENCES `readers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `borrows`
--

LOCK TABLES `borrows` WRITE;
/*!40000 ALTER TABLE `borrows` DISABLE KEYS */;
INSERT INTO `borrows` VALUES (1,'BR001',1,'2026-09-05','2027-03-05','2026-09-05','RETURNED',0.00,'UNPAID'),(2,'BR004',1,'2026-09-05','2027-03-05','2026-09-10','RETURNED',0.00,'UNPAID'),(3,'BR005',2,'2026-09-08','2027-03-08','2026-09-09','RETURNED',0.00,'UNPAID'),(4,'BR002',2,'2026-09-08','2027-03-08','2026-09-10','RETURNED',0.00,'UNPAID'),(5,'BR003',1,'2026-09-10','2027-03-10','2026-09-10','RETURNED',0.00,'UNPAID'),(6,'BR006',2,'2026-09-10','2027-03-10','2026-09-19','RETURNED',0.00,'UNPAID'),(7,'BR007',1,'2026-09-19','2027-03-19','2026-09-19','RETURNED',0.00,'UNPAID'),(8,'BR008',1,'2026-09-19','2027-03-19','2026-09-19','RETURNED',0.00,'UNPAID'),(9,'BR009',2,'2026-09-19','2027-03-19','2026-09-19','RETURNED',0.00,'UNPAID'),(10,'BR010',1,'2026-09-19','2027-03-19','2026-09-19','RETURNED',0.00,'PAID'),(11,'BR011',2,'2026-09-19','2027-03-19','2026-09-19','RETURNED',50000.00,'PAID'),(12,'BR012',2,'2026-09-19','2027-03-19','2026-09-19','RETURNED',100000.00,'PAID'),(13,'BR013',2,'2026-09-19','2027-03-19','2026-09-19','RETURNED',50000.00,'PAID'),(14,'BR014',2,'2026-09-19','2027-03-19','2026-09-19','RETURNED',50000.00,'UNPAID'),(15,'BR015',2,'2026-09-19','2027-03-19','2026-09-19','RETURNED',100000.00,'PAID'),(16,'BR016',2,'2026-09-19','2027-03-19','2026-09-19','RETURNED',0.00,'PAID'),(17,'BR017',2,'2026-09-19','2027-03-19','2026-09-19','RETURNED',0.00,'PAID'),(18,'BR018',2,'2026-09-19','2027-03-19','2026-09-19','RETURNED',100000.00,'PAID'),(19,'BR019',2,'2026-09-19','2027-03-19','2026-09-19','RETURNED',50000.00,'UNPAID'),(20,'BR020',2,'2026-09-19','2027-03-19','2026-09-19','RETURNED',100000.00,'UNPAID'),(21,'BR021',1,'2026-09-19','2027-03-19','2026-09-19','RETURNED',150000.00,'PAID'),(22,'BR022',2,'2026-09-19','2027-03-19','2026-09-19','RETURNED',250000.00,'UNPAID'),(23,'BR023',1,'2026-09-19','2027-03-19','2026-09-19','RETURNED',266000.00,'PAID'),(24,'BR024',1,'2026-09-19','2027-03-19','2026-09-19','RETURNED',0.00,'UNPAID'),(25,'BR025',1,'2026-09-19','2027-03-19','2026-09-19','RETURNED',300000.00,'PAID'),(26,'BR026',1,'2026-09-19','2027-03-19','2026-09-19','RETURNED',0.00,'UNPAID'),(27,'BR027',1,'2026-09-19','2027-03-19','2026-09-19','RETURNED',0.00,'UNPAID'),(28,'BR028',1,'2026-09-20','2027-03-20','2026-09-20','RETURNED',160000.00,'PAID'),(29,'BR029',1,'2026-09-20','2027-03-20','2026-10-08','RETURNED',0.00,'UNPAID'),(30,'BR030',1,'2026-09-21','2027-03-21',NULL,'BORROWING',0.00,'UNPAID'),(31,'BR031',1,'2026-10-04','2026-10-05','2026-10-06','RETURNED',0.00,'UNPAID'),(32,'BR032',1,'2026-10-06','2026-10-07','2026-10-08','RETURNED',0.00,'UNPAID'),(33,'BR033',1,'2026-10-08','2026-10-09',NULL,'OVERDUE',0.00,'UNPAID');
/*!40000 ALTER TABLE `borrows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `readers`
--

DROP TABLE IF EXISTS `readers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `readers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_of_birth` date DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `full_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `gender` enum('FEMALE','MALE','OTHER') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone_number` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reader_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKcpvgwbjqfp57awmldmkwd531a` (`reader_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `readers`
--

LOCK TABLES `readers` WRITE;
/*!40000 ALTER TABLE `readers` DISABLE KEYS */;
INSERT INTO `readers` VALUES (1,'2004-05-15',_binary '\0','Nguyen Van Nam','MALE','0901234567','R001'),(2,'2005-08-20',_binary '\0','Tran Thi Lan','FEMALE','0912345678','R002');
/*!40000 ALTER TABLE `readers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('ADMIN','LIBRARIAN') COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-04 20:00:24
