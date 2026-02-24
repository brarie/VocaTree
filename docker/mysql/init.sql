-- MySQL dump 10.13  Distrib 8.0.45, for Linux (x86_64)
--
-- Host: localhost    Database: vocatree
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `contribution_logs`
--

DROP TABLE IF EXISTS `contribution_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contribution_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(20) NOT NULL,
  `resource` varchar(20) DEFAULT NULL,
  `target_word_id` bigint NOT NULL,
  `target_sense_id` bigint DEFAULT NULL,
  `target_example_id` bigint DEFAULT NULL,
  `suggested_data` text NOT NULL,
  `contributor_id` bigint NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'PENDING',
  `reviewer_note` text,
  `created_at` datetime(6) NOT NULL,
  `reviewed_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contribution_opinions`
--

DROP TABLE IF EXISTS `contribution_opinions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contribution_opinions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contribution_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `content` text NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contribution_opinions_contribution_id__id` (`contribution_id`),
  CONSTRAINT `fk_contribution_opinions_contribution_id__id` FOREIGN KEY (`contribution_id`) REFERENCES `contribution_logs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `public_examples`
--

DROP TABLE IF EXISTS `public_examples`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `public_examples` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sense_id` bigint NOT NULL,
  `sentence` text NOT NULL,
  `translation` text,
  `is_official` tinyint(1) NOT NULL DEFAULT '0',
  `source_info` varchar(200) DEFAULT NULL,
  `display_order` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_public_examples_sense_id__id` (`sense_id`),
  CONSTRAINT `fk_public_examples_sense_id__id` FOREIGN KEY (`sense_id`) REFERENCES `public_senses` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `public_senses`
--

DROP TABLE IF EXISTS `public_senses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `public_senses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `word_id` bigint NOT NULL,
  `synonyms` json DEFAULT NULL,
  `antonyms` json DEFAULT NULL,
  `definition` text NOT NULL,
  `eng_definition` text,
  `part_of_speech` varchar(20) DEFAULT NULL,
  `british_phonetic` varchar(100) DEFAULT NULL,
  `american_phonetic` varchar(100) DEFAULT NULL,
  `display_order` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_public_senses_word_id__id` (`word_id`),
  CONSTRAINT `fk_public_senses_word_id__id` FOREIGN KEY (`word_id`) REFERENCES `public_words` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `public_words`
--

DROP TABLE IF EXISTS `public_words`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `public_words` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `spelling` varchar(100) NOT NULL,
  `slug` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `public_words_spelling_unique` (`spelling`),
  UNIQUE KEY `public_words_slug_unique` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `support_categories`
--

DROP TABLE IF EXISTS `support_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `support_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_name` varchar(255) NOT NULL,
  `parent_category_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_support_categories_parent_category_id__id` (`parent_category_id`),
  CONSTRAINT `fk_support_categories_parent_category_id__id` FOREIGN KEY (`parent_category_id`) REFERENCES `support_categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `support_posts`
--

DROP TABLE IF EXISTS `support_posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `support_posts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `author_id` binary(16) NOT NULL,
  `support_category_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_support_posts_author_id__id` (`author_id`),
  KEY `fk_support_posts_support_category_id__id` (`support_category_id`),
  CONSTRAINT `fk_support_posts_author_id__id` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_support_posts_support_category_id__id` FOREIGN KEY (`support_category_id`) REFERENCES `support_categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` binary(16) NOT NULL,
  `username` varchar(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `nickname` varchar(50) NOT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'USER',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_username_unique` (`username`),
  UNIQUE KEY `user_email_unique` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_search_history`
--

DROP TABLE IF EXISTS `user_search_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_search_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(50) NOT NULL,
  `spelling` varchar(100) NOT NULL,
  `searched_at` datetime(6) NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `word_examples`
--

DROP TABLE IF EXISTS `word_examples`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `word_examples` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sense_id` bigint NOT NULL,
  `sentence` text,
  `translation` text,
  `display_order` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_word_examples_sense_id__id` (`sense_id`),
  CONSTRAINT `fk_word_examples_sense_id__id` FOREIGN KEY (`sense_id`) REFERENCES `word_senses` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `word_senses`
--

DROP TABLE IF EXISTS `word_senses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `word_senses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `word_id` bigint NOT NULL,
  `definition` text,
  `eng_definition` text,
  `british_phonetic` varchar(100) DEFAULT NULL,
  `american_phonetic` varchar(100) DEFAULT NULL,
  `part_of_speech` varchar(20) DEFAULT NULL,
  `synonyms` json DEFAULT NULL,
  `antonyms` json DEFAULT NULL,
  `display_order` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_word_senses_word_id__id` (`word_id`),
  CONSTRAINT `fk_word_senses_word_id__id` FOREIGN KEY (`word_id`) REFERENCES `words` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wordbook_column_setting`
--

DROP TABLE IF EXISTS `wordbook_column_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wordbook_column_setting` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `wordbook_id` bigint NOT NULL,
  `column_type` varchar(50) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `width` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_wordbook_column_setting_wordbook_id__id` (`wordbook_id`),
  CONSTRAINT `fk_wordbook_column_setting_wordbook_id__id` FOREIGN KEY (`wordbook_id`) REFERENCES `wordbook_node` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wordbook_node`
--

DROP TABLE IF EXISTS `wordbook_node`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wordbook_node` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `owner_id` binary(16) NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  `node_type` varchar(20) NOT NULL,
  `is_starred` tinyint(1) NOT NULL DEFAULT '0',
  `title` varchar(200) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_wordbook_node_owner_id__id` (`owner_id`),
  KEY `fk_wordbook_node_parent_id__id` (`parent_id`),
  CONSTRAINT `fk_wordbook_node_owner_id__id` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_wordbook_node_parent_id__id` FOREIGN KEY (`parent_id`) REFERENCES `wordbook_node` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `words`
--

DROP TABLE IF EXISTS `words`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `words` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `wordbook_id` bigint NOT NULL,
  `spelling` varchar(100) DEFAULT NULL,
  `display_order` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_words_wordbook_id__id` (`wordbook_id`),
  CONSTRAINT `fk_words_wordbook_id__id` FOREIGN KEY (`wordbook_id`) REFERENCES `wordbook_node` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-24 14:10:03
