-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Sep 12, 2026 at 01:49 AM
-- Server version: 11.4.13-MariaDB-cll-lve-log
-- PHP Version: 8.4.24

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `hiresmcq_lens_calculator`
--

-- --------------------------------------------------------

--
-- Table structure for table `access_logs`
--

CREATE TABLE `access_logs` (
  `id` int(11) NOT NULL,
  `ip_address` varchar(45) NOT NULL,
  `user_agent` text DEFAULT NULL,
  `request_uri` varchar(255) DEFAULT NULL,
  `accessed_at` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `access_logs`
--

INSERT INTO `access_logs` (`id`, `ip_address`, `user_agent`, `request_uri`, `accessed_at`) VALUES
(1, '112.134.188.213', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:151.0) Gecko/20100101 Firefox/151.0', '/api/log_access.php', '2026-05-17 16:43:31'),
(2, '112.134.188.213', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:151.0) Gecko/20100101 Firefox/151.0', '/api/log_access.php', '2026-05-17 17:44:40'),
(3, '66.249.74.66', 'Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.7778.96 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', '/api/log_access.php', '2026-05-18 08:59:26'),
(4, '66.249.74.67', 'Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.7778.96 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', '/api/log_access.php', '2026-05-20 01:49:25'),
(5, '173.252.82.3', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36', '/api/log_access.php', '2026-05-24 15:09:50'),
(6, '173.252.127.21', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36', '/api/log_access.php', '2026-05-24 15:13:17'),
(7, '112.134.181.100', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36', '/api/log_access.php', '2026-05-24 16:14:54'),
(8, '112.134.181.100', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36', '/api/log_access.php', '2026-05-24 16:15:52'),
(9, '112.134.181.100', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36', '/api/log_access.php', '2026-05-24 16:16:40'),
(10, '112.135.75.107', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '/api/log_access.php', '2026-05-24 16:44:34'),
(11, '112.134.181.100', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36', '/api/log_access.php', '2026-05-24 16:44:55'),
(12, '112.134.181.100', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36', '/api/log_access.php', '2026-05-24 16:49:18'),
(13, '66.249.74.66', 'Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.7727.116 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', '/api/log_access.php', '2026-05-26 09:29:29'),
(14, '66.249.74.66', 'Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1; +http://www.google.com/bot.html) Chrome/147.0.7727.116 Safari/537.36', '/api/log_access.php', '2026-05-26 09:29:32'),
(15, '112.134.186.132', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:152.0) Gecko/20100101 Firefox/152.0', '/api/log_access.php', '2026-05-26 17:58:44'),
(16, '112.134.186.132', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:152.0) Gecko/20100101 Firefox/152.0', '/api/log_access.php', '2026-05-26 18:28:51'),
(17, '70.49.169.80', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36', '/api/log_access.php', '2026-05-26 22:22:49'),
(18, '70.49.169.80', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36', '/api/log_access.php', '2026-05-26 22:57:48'),
(19, '66.249.83.67', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Mobile Safari/537.36 (compatible; Google-Read-Aloud; +https://support.google.com/webmasters/answer/1061943)', '/api/log_access.php', '2026-05-26 22:57:50'),
(20, '112.134.191.104', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:152.0) Gecko/20100101 Firefox/152.0', '/api/log_access.php', '2026-05-27 05:56:25'),
(21, '112.134.189.91', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:152.0) Gecko/20100101 Firefox/152.0', '/api/log_access.php', '2026-05-27 16:22:58'),
(22, '112.134.213.77', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36', '/api/log_access.php', '2026-05-28 09:23:33'),
(23, '212.104.229.186', 'Mozilla/5.0 (iPhone; CPU iPhone OS 18_7_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/146.0.7680.151 Mobile/15E148 Safari/604.1', '/api/log_access.php', '2026-06-08 12:02:01'),
(24, '66.249.74.67', 'Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.7778.96 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', '/api/log_access.php', '2026-06-11 04:29:32'),
(25, '66.249.74.67', 'Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1; +http://www.google.com/bot.html) Chrome/147.0.7727.116 Safari/537.36', '/api/log_access.php', '2026-06-11 04:29:33'),
(26, '124.43.240.151', 'Mozilla/5.0 (iPhone; CPU iPhone OS 18_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.0 Mobile/15E148 Safari/604.1', '/api/log_access.php', '2026-06-17 06:12:18'),
(27, '103.247.48.164', 'Mozilla/5.0 (iPhone; CPU iPhone OS 18_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.0 Mobile/15E148 Safari/604.1', '/api/log_access.php', '2026-06-17 09:33:10'),
(28, '66.249.74.67', 'Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1; +http://www.google.com/bot.html) Chrome/148.0.7778.96 Safari/537.36', '/api/log_access.php', '2026-06-22 09:59:23'),
(29, '66.249.74.66', 'Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.7778.96 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', '/api/log_access.php', '2026-06-22 09:59:25'),
(30, '112.134.191.236', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:153.0) Gecko/20100101 Firefox/153.0', '/api/log_access.php', '2026-06-23 16:10:47'),
(31, '112.134.191.236', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36', '/api/log_access.php', '2026-06-23 17:30:44'),
(32, '112.134.191.236', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36', '/api/log_access.php', '2026-06-23 17:38:34'),
(33, '66.220.149.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36 Edg/149.0.0.0', '/api/log_access.php', '2026-06-23 17:53:07'),
(34, '173.252.87.41', 'Mozilla/5.0 (iPhone; CPU iPhone OS 18_7 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.5 Mobile/15E148 Safari/604.1', '/api/log_access.php', '2026-06-23 17:53:10'),
(35, '212.104.229.72', 'Mozilla/5.0 (iPhone; CPU iPhone OS 26_3_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) GSA/425.6.927981711 Mobile/15E148 Safari/604.1', '/api/log_access.php', '2026-06-28 03:41:28'),
(36, '66.102.6.162', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Mobile Safari/537.36 (compatible; Google-Read-Aloud; +https://support.google.com/webmasters/answer/1061943)', '/api/log_access.php', '2026-06-28 03:41:29'),
(37, '66.102.6.162', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Mobile Safari/537.36 (compatible; Google-Read-Aloud; +https://support.google.com/webmasters/answer/1061943)', '/api/log_access.php', '2026-06-28 03:41:29'),
(38, '43.250.241.123', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36', '/api/log_access.php', '2026-07-02 08:44:02'),
(39, '192.248.2.10', 'Mozilla/5.0 (iPhone; CPU iPhone OS 26_5_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/149.0.7827.137 Mobile/15E148 Safari/604.1', '/api/log_access.php', '2026-07-02 09:38:04'),
(40, '66.249.74.66', 'Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.7827.200 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', '/api/log_access.php', '2026-07-03 14:58:35'),
(41, '175.157.229.117', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Mobile Safari/537.36', '/api/log_access.php', '2026-07-03 17:00:38'),
(42, '192.248.2.10', 'Mozilla/5.0 (iPhone; CPU iPhone OS 26_5_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/149.0.7827.137 Mobile/15E148 Safari/604.1', '/api/log_access.php', '2026-07-04 03:18:08'),
(43, '175.157.247.31', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Mobile Safari/537.36', '/api/log_access.php', '2026-07-05 11:52:09'),
(44, '66.102.6.163', 'Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Mobile Safari/537.36 (compatible; Google-Read-Aloud; +https://support.google.com/webmasters/answer/1061943)', '/api/log_access.php', '2026-07-05 11:52:13'),
(45, '40.77.179.158', 'Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm) Chrome/136.0.0.0 Safari/537.36', '/api/log_access.php', '2026-07-05 14:13:29'),
(46, '112.134.187.160', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:153.0) Gecko/20100101 Firefox/153.0', '/api/log_access.php', '2026-07-13 13:11:50'),
(47, '124.43.18.160', 'Mozilla/5.0 (iPhone; CPU iPhone OS 18_7 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.5 Mobile/15E148 Safari/604.1', '/api/log_access.php', '2026-07-13 13:13:38'),
(48, '66.249.74.67', 'Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1; +http://www.google.com/bot.html) Chrome/150.0.7871.46 Safari/537.36', '/api/log_access.php', '2026-07-16 03:19:49'),
(49, '66.249.74.66', 'Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.7871.46 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', '/api/log_access.php', '2026-07-16 03:19:52'),
(50, '66.249.74.67', 'Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.7871.128 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', '/api/log_access.php', '2026-07-24 03:31:30'),
(51, '66.249.74.66', 'Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1; +http://www.google.com/bot.html) Chrome/150.0.7871.124 Safari/537.36', '/api/log_access.php', '2026-07-26 16:34:06'),
(52, '66.249.74.67', 'Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.7871.124 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', '/api/log_access.php', '2026-07-26 16:34:07'),
(53, '66.249.74.67', 'Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.7871.186 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', '/api/log_access.php', '2026-08-06 13:46:37'),
(54, '66.249.74.66', 'Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1; +http://www.google.com/bot.html) Chrome/151.0.7922.71 Safari/537.36', '/api/log_access.php', '2026-08-06 13:46:41'),
(55, '112.134.191.54', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:154.0) Gecko/20100101 Firefox/154.0', '/api/log_access.php', '2026-08-09 19:57:53'),
(56, '66.249.74.66', 'Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1; +http://www.google.com/bot.html) Chrome/151.0.7922.108 Safari/537.36', '/api/log_access.php', '2026-08-17 03:42:23'),
(57, '66.249.74.67', 'Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/151.0.7922.108 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', '/api/log_access.php', '2026-08-17 03:42:25'),
(58, '66.249.74.42', 'Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/151.0.7922.137 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', '/api/log_access.php', '2026-08-28 11:02:06'),
(59, '66.249.74.41', 'Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1; +http://www.google.com/bot.html) Chrome/151.0.7922.137 Safari/537.36', '/api/log_access.php', '2026-08-28 11:02:08'),
(60, '66.249.74.42', 'Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/151.0.7922.173 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', '/api/log_access.php', '2026-09-07 15:20:41'),
(61, '66.249.74.40', 'Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1; +http://www.google.com/bot.html) Chrome/152.0.7977.64 Safari/537.36', '/api/log_access.php', '2026-09-07 15:20:44'),
(62, '112.134.188.230', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:156.0) Gecko/20100101 Firefox/156.0', '/api/log_access.php', '2026-09-12 05:28:09'),
(63, '112.134.188.230', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:156.0) Gecko/20100101 Firefox/156.0', '/api/log_access.php', '2026-09-12 05:29:44');

-- --------------------------------------------------------

--
-- Table structure for table `category_types`
--

CREATE TABLE `category_types` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `display_order` int(11) DEFAULT 0,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `category_types`
--

INSERT INTO `category_types` (`id`, `name`, `description`, `display_order`, `is_active`, `created_at`, `updated_at`) VALUES
(1, 'Camera Body', 'Digital camera bodies and housings', 1, 1, '2025-11-10 07:25:56', '2025-11-10 07:25:56'),
(2, 'Lenses', 'Camera lenses and optical equipment', 2, 1, '2025-11-10 07:25:56', '2025-11-10 07:25:56'),
(3, 'Lighting', 'Studio and portable lighting equipment', 3, 1, '2025-11-10 07:25:56', '2025-11-10 07:25:56'),
(4, 'Audio', 'Microphones and audio recording equipment', 4, 1, '2025-11-10 07:25:56', '2025-11-10 07:25:56'),
(5, 'Stabilization', 'Tripods, gimbals, and stabilization gear', 5, 1, '2025-11-10 07:25:56', '2025-11-10 07:25:56'),
(6, 'Accessories', 'Memory cards, batteries, and other accessories', 6, 1, '2025-11-10 07:25:56', '2025-11-10 07:25:56'),
(7, 'Backdrops', 'Backgrounds and backdrop systems', 7, 1, '2025-11-10 07:25:56', '2025-11-10 07:25:56'),
(8, 'Props', 'Photography props and styling items', 8, 1, '2025-11-10 07:25:56', '2025-11-10 07:25:56'),
(9, 'Other', 'Other Items', 9, 1, '2026-05-17 08:46:54', '2026-05-17 08:47:24');

-- --------------------------------------------------------

--
-- Table structure for table `equipment_details`
--

CREATE TABLE `equipment_details` (
  `id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `model` varchar(150) NOT NULL,
  `name` varchar(200) NOT NULL,
  `value` decimal(10,2) NOT NULL DEFAULT 0.00,
  `description` text DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `equipment_details`
--

INSERT INTO `equipment_details` (`id`, `category_id`, `type`, `model`, `name`, `value`, `description`, `is_active`, `created_at`, `updated_at`) VALUES
(1, 1, 12, 'Sony', 'Sony A7 IV', 6000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(2, 1, 12, 'Sony', 'Sony A7S III', 10000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(3, 1, 12, 'Sony', 'Sony A7R V', 12500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(4, 1, 12, 'Sony', 'Sony A6400', 3850.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(5, 1, 12, 'Sony', 'Sony A7 C', 5000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(6, 1, 12, 'Sony', 'Sony A7 III', 5000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(7, 1, 12, 'Sony', 'Sony FX 3', 12500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(8, 2, 4, 'Sony', 'Sony FE 24mm F/1.4 GM', 4750.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(9, 2, 4, 'Sony', 'Sony FE 85mm F/1.8', 2000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(10, 2, 4, 'Sony', 'Sony FE 85mm F/1.4 GM', 4000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(11, 2, 4, 'Sony', 'Sony FE 90mm F/2.8 Macro G OSS', 4750.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(12, 2, 4, 'Sony', 'Sony FE 35mm F/1.8', 2650.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(13, 2, 4, 'Sony', 'Sony FE 35mm F/1.4', 4000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(14, 2, 4, 'Sony', 'Sony FE 50mm F/1.8', 1500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(15, 2, 4, 'Sony', 'Sony FE 50mm F/1.4', 4000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(16, 2, 4, 'Sony', 'Sony FE 50mm F/1.2', 4950.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(17, 2, 4, 'Tamron', 'Tamron 20mm F/2.8 Di III OSD (Sony)', 1500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(18, 2, 4, 'Tamron', 'Tamron 24mm F/2.8 Di III OSD (Sony)', 1500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(19, 2, 4, 'Tamron', 'Tamron 35mm F/2.8 Di III OSD (Sony)', 1500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(20, 2, 4, 'Sigma', 'Sigma 35mm F/1.4 DG DN Art (Sony)', 3000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(21, 2, 4, 'Sigma', 'Sigma 50mm F/1.4 DG DN Art (Sony)', 3750.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(22, 2, 3, 'Sigma', 'Sigma 24-70mm F/2.8 DG DN Art (Sony)', 4000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(23, 2, 3, 'Sony', 'Sony FE 16-35mm F/4 ZA OSS', 3000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(24, 2, 3, 'Sony', 'Sony FE 16-35mm F/2.8 GM II', 4500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(25, 2, 3, 'Sony', 'Sony FE 24-70mm F/2.8 GM II', 5900.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(26, 2, 3, 'Sony', 'Sony FE 24-105mm F/4 G OSS', 3000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(27, 2, 3, 'Sony', 'Sony FE 70-200mm F/2.8 GM OSS II', 7500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(28, 2, 3, 'Sony', 'Sony FE 100-400mm F/4.5-5.6 GM OSS', 5900.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(29, 2, 3, 'Sony', 'Sony FE 200-600mm F/5.6-6.3 G OSS', 7500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(30, 2, 3, 'Sony', 'Sony FE 200-600mm F/5.6-6.3 G OSS', 7500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(31, 6, 17, 'Sony', 'Sony NP-7W50', 450.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(32, 6, 17, 'Sony', 'Sony NP-FZ100', 450.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(33, 6, 17, 'K&F', 'Sony FW-50 USB Battery Charger', 500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(34, 6, 17, 'Sony', 'Sony BC-QZ1 Battery Charger', 500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(35, 6, 14, 'Tough', 'Sony Tough 64GB Memory Card', 500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(36, 6, 14, 'Tough', 'Sony Tough 128GB Memory Card', 1000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(37, 6, 14, 'Tough', 'Sony Tough 80GB CF Express Type A Memory Card', 2000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(38, 6, 14, 'Tough', 'Sony Tough 160GB CF Express Type A Memory Card', 2500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(39, 9, 18, 'Sony', 'Sony MRW-G2 Memory Card Reader', 500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(40, 3, 7, 'GODOX', 'V860 II Flash', 1000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 10:47:32'),
(41, 3, 7, 'GODOX', 'V860 III Flash', 1750.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 10:47:36'),
(42, 9, 9, 'GODOX', 'X2 Trigger', 500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 10:47:39'),
(43, 9, 9, 'GODOX', 'X3 Trigger', 1000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 10:47:42'),
(44, 1, 12, 'Nikon', 'D3300 + Lens', 2250.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(45, 1, 12, 'Nikon', 'D3400', 2500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(46, 1, 12, 'Nikon', 'D5300', 3000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(47, 1, 12, 'Nikon', 'D3500', 3000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(48, 1, 12, 'Nikon', 'D5500', 3250.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(49, 1, 12, 'Nikon', 'D5500 + lens', 3750.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(50, 1, 12, 'Nikon', 'D3500 + lens', 3500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(51, 1, 12, 'Nikon', 'D5600 + lens', 4000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(52, 1, 12, 'Nikon', 'D7500', 6000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(53, 1, 12, 'Nikon', 'Z5 + Lens', 7000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(54, 1, 12, 'Canon', 'EOS R', 5000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(55, 1, 12, 'Canon', 'EOS R6 Mark II', 6000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(56, 2, 3, 'Canon', 'Canon RF 24-70mm f/2.8 L IS USM', 5900.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(57, 2, 3, 'Canon', 'Canon RF 24-105mm f/4 L IS USM', 3000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(58, 2, 4, 'Canon', 'Canon RF 50mm f/1.2 L USM', 5900.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(59, 2, 4, 'Canon', 'Canon RF 16mm f/2.8 USM', 1750.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(60, 2, 4, 'Canon', 'Canon RF 85mm f/2 STM', 2000.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(61, 2, 4, 'Canon', 'Canon RF 50mm f/1.8 STM', 1500.00, NULL, 1, '2026-05-17 09:29:53', '2026-05-17 09:29:53'),
(62, 2, 3, 'Canon', 'Canon 100-300mm', 1000.00, '', 1, '2026-05-17 12:21:04', '2026-05-17 12:21:04'),
(63, 1, 12, 'Canon', 'Canon M50', 2000.00, '', 1, '2026-05-17 17:44:08', '2026-05-17 17:44:08'),
(64, 1, 13, 'Canon', '5D mark iv', 4000.00, '', 1, '2026-05-24 16:46:01', '2026-05-24 16:46:01'),
(65, 1, 12, 'Canon', 'Canon EOS R', 5000.00, '', 1, '2026-05-24 16:46:59', '2026-05-24 16:46:59'),
(66, 1, 12, 'Nikon', 'Nikon Z6 II', 5000.00, '', 1, '2026-05-24 16:48:38', '2026-05-24 16:48:38'),
(67, 1, 12, 'Nikon', 'Nikon Z6III', 6500.00, '', 1, '2026-05-24 16:49:49', '2026-05-24 16:49:49'),
(68, 5, 15, 'DJI', 'DJI RS3', 3000.00, '', 1, '2026-05-24 16:51:37', '2026-05-24 16:51:37'),
(69, 5, 15, 'DJI', 'DJI RS4', 4000.00, '', 1, '2026-05-24 16:52:11', '2026-05-24 16:52:11'),
(70, 5, 15, 'DJI', 'DJI RS4 Pro', 5000.00, '', 1, '2026-05-24 16:52:48', '2026-05-24 16:52:48'),
(71, 5, 15, 'DJI', 'DJI RS3 Pro', 4000.00, '', 1, '2026-05-24 16:53:39', '2026-05-24 16:53:39'),
(72, 5, 15, 'DJI', 'DJI RSC', 2000.00, '', 1, '2026-05-24 16:54:51', '2026-05-24 16:54:51'),
(73, 5, 15, 'DJI', 'DJI RSC2', 2500.00, '', 1, '2026-05-24 16:55:19', '2026-05-24 16:55:19'),
(74, 4, 5, 'Zoom', 'Zoom H6 audio recorder', 1500.00, '', 1, '2026-05-24 16:58:19', '2026-05-24 16:58:19'),
(75, 1, 1, 'Sony', 'Sony PXW Z90', 5000.00, '', 1, '2026-05-24 16:59:22', '2026-05-24 16:59:22'),
(76, 1, 1, 'Sony', 'Sony HXR NX200', 5000.00, '', 1, '2026-05-24 17:00:38', '2026-05-24 17:00:38'),
(77, 1, 13, 'Canon', 'Canon 6D Mark II', 3500.00, '', 1, '2026-05-24 17:01:59', '2026-05-24 17:01:59'),
(78, 1, 13, 'Canon', 'Canon 5D Mark III', 3000.00, '', 1, '2026-05-24 17:02:42', '2026-05-24 17:02:42'),
(79, 1, 13, 'Canon', 'Canon 6D', 2500.00, '', 1, '2026-05-24 17:03:10', '2026-05-24 17:03:10'),
(80, 6, 14, 'Sandisk', 'SanDisk Extreme Pro 64GB V30 SDXC', 500.00, '', 1, '2026-05-24 17:06:22', '2026-05-24 17:06:22'),
(81, 6, 14, 'Sandisk', 'Sandisk Extreme Pro 128GB V30 SDXC', 750.00, '', 1, '2026-05-24 17:07:08', '2026-05-24 17:07:08'),
(82, 5, 2, 'Tripod', 'Video Tripod', 1000.00, '', 1, '2026-05-24 17:08:21', '2026-05-24 17:08:21'),
(83, 5, 2, 'Tripod', 'Photography Tripod', 500.00, '', 1, '2026-05-24 17:08:47', '2026-05-24 17:08:47'),
(84, 5, 2, 'Monopod', 'Monopod', 500.00, '', 1, '2026-05-24 17:09:41', '2026-05-24 17:09:41'),
(85, 6, 17, 'Fz100', 'Sony battry', 500.00, '', 1, '2026-07-05 11:56:42', '2026-07-05 11:56:42'),
(86, 6, 18, 'Envilop', 'Flash battry', 500.00, '', 1, '2026-07-05 11:57:47', '2026-07-05 11:57:47');

-- --------------------------------------------------------

--
-- Table structure for table `equipment_types`
--

CREATE TABLE `equipment_types` (
  `id` int(11) NOT NULL,
  `type` varchar(100) NOT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `equipment_types`
--

INSERT INTO `equipment_types` (`id`, `type`, `is_active`, `created_at`) VALUES
(1, 'Video Camera', 1, '2025-11-10 17:26:22'),
(2, 'Tripod', 1, '2025-11-10 17:26:22'),
(3, 'Telephoto Lens', 1, '2025-11-10 17:26:22'),
(4, 'Prime Lens', 1, '2025-11-10 17:26:22'),
(5, 'Mics', 1, '2025-11-10 17:26:22'),
(6, 'Strobe', 1, '2025-11-10 17:26:22'),
(7, 'Flashs', 1, '2025-11-10 17:26:22'),
(8, 'Slider', 1, '2025-11-10 17:26:22'),
(9, 'Remote/Trigger', 1, '2025-11-10 17:26:22'),
(10, 'Light Modifier', 1, '2025-11-10 17:26:22'),
(11, 'Monopod', 1, '2025-11-10 17:26:22'),
(12, 'Mirrorless', 1, '2025-11-10 17:26:22'),
(13, 'DSLR', 1, '2025-11-10 17:26:23'),
(14, 'Storage', 1, '2025-11-10 17:26:23'),
(15, 'Gimbal', 1, '2025-11-10 17:26:23'),
(16, 'Continuous Light', 1, '2025-11-10 17:26:23'),
(17, 'Battery', 1, '2025-11-10 17:26:23'),
(18, 'Other', 1, '2026-05-17 08:46:12');

-- --------------------------------------------------------

--
-- Table structure for table `packages`
--

CREATE TABLE `packages` (
  `id` int(11) NOT NULL,
  `package_name` varchar(200) NOT NULL,
  `client_name` varchar(200) DEFAULT NULL,
  `template_id` int(11) DEFAULT NULL,
  `labor_hours` decimal(5,2) DEFAULT 0.00,
  `hourly_rate` decimal(10,2) DEFAULT 0.00,
  `margin_percentage` decimal(5,2) DEFAULT 0.00,
  `equipment_total` decimal(10,2) DEFAULT 0.00,
  `labor_total` decimal(10,2) DEFAULT 0.00,
  `subtotal` decimal(10,2) DEFAULT 0.00,
  `margin_amount` decimal(10,2) DEFAULT 0.00,
  `final_total` decimal(10,2) DEFAULT 0.00,
  `notes` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `package_equipment`
--

CREATE TABLE `package_equipment` (
  `id` int(11) NOT NULL,
  `package_id` int(11) NOT NULL,
  `equipment_id` int(11) NOT NULL,
  `quantity` int(11) DEFAULT 1,
  `unit_value` decimal(10,2) NOT NULL,
  `total_value` decimal(10,2) NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `templates`
--

CREATE TABLE `templates` (
  `id` int(11) NOT NULL,
  `name` varchar(200) NOT NULL,
  `description` text DEFAULT NULL,
  `labor_hours` decimal(5,2) DEFAULT 0.00,
  `hourly_rate` decimal(10,2) DEFAULT 0.00,
  `margin_percentage` decimal(5,2) DEFAULT 0.00,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `templates`
--

INSERT INTO `templates` (`id`, `name`, `description`, `labor_hours`, `hourly_rate`, `margin_percentage`, `is_active`, `created_at`, `updated_at`) VALUES
(1, 'Birthday Shoot - Basic', 'Basic birthday photography package with essential equipment', 3.00, 1000.00, 10.00, 1, '2025-11-10 07:25:56', '2025-11-10 12:07:23'),
(2, 'Wedding Shoot - Professional', 'Comprehensive wedding photography package with premium equipment', 8.00, 2000.00, 30.00, 1, '2025-11-10 07:25:57', '2025-11-10 12:06:48'),
(4, 'Product Photography - Basic', 'Basic product photography setup for e-commerce', 3.00, 1000.00, 10.00, 1, '2025-11-10 07:25:57', '2025-11-10 12:07:17');

-- --------------------------------------------------------

--
-- Table structure for table `template_equipment`
--

CREATE TABLE `template_equipment` (
  `id` int(11) NOT NULL,
  `template_id` int(11) NOT NULL,
  `equipment_id` int(11) NOT NULL,
  `quantity` int(11) DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `template_equipment`
--

INSERT INTO `template_equipment` (`id`, `template_id`, `equipment_id`, `quantity`, `created_at`) VALUES
(1, 1, 63, 1, '2025-11-10 12:25:57'),
(2, 1, 61, 1, '2025-11-10 12:25:57'),
(3, 1, 40, 1, '2025-11-10 12:25:57'),
(8, 2, 41, 2, '2025-11-10 12:25:57'),
(9, 2, 26, 1, '2025-11-10 12:25:57'),
(10, 2, 6, 1, '2025-11-10 12:25:57'),
(11, 2, 42, 1, '2025-11-10 12:25:57'),
(12, 2, 35, 1, '2025-11-10 12:25:57'),
(18, 4, 1, 1, '2025-11-10 12:25:57'),
(19, 4, 11, 1, '2025-11-10 12:25:57'),
(20, 4, 41, 1, '2025-11-10 12:25:57');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `access_logs`
--
ALTER TABLE `access_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_ip` (`ip_address`),
  ADD KEY `idx_accessed` (`accessed_at`);

--
-- Indexes for table `category_types`
--
ALTER TABLE `category_types`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `equipment_details`
--
ALTER TABLE `equipment_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_category` (`category_id`),
  ADD KEY `idx_type` (`type`),
  ADD KEY `idx_active` (`is_active`);

--
-- Indexes for table `equipment_types`
--
ALTER TABLE `equipment_types`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `type` (`type`);

--
-- Indexes for table `packages`
--
ALTER TABLE `packages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `template_id` (`template_id`),
  ADD KEY `idx_client` (`client_name`),
  ADD KEY `idx_created` (`created_at`);

--
-- Indexes for table `package_equipment`
--
ALTER TABLE `package_equipment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `equipment_id` (`equipment_id`),
  ADD KEY `idx_package` (`package_id`);

--
-- Indexes for table `templates`
--
ALTER TABLE `templates`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `template_equipment`
--
ALTER TABLE `template_equipment`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_template_equipment` (`template_id`,`equipment_id`),
  ADD KEY `idx_template` (`template_id`),
  ADD KEY `idx_equipment` (`equipment_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `access_logs`
--
ALTER TABLE `access_logs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=64;

--
-- AUTO_INCREMENT for table `category_types`
--
ALTER TABLE `category_types`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `equipment_details`
--
ALTER TABLE `equipment_details`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=87;

--
-- AUTO_INCREMENT for table `equipment_types`
--
ALTER TABLE `equipment_types`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `packages`
--
ALTER TABLE `packages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `package_equipment`
--
ALTER TABLE `package_equipment`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `templates`
--
ALTER TABLE `templates`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `template_equipment`
--
ALTER TABLE `template_equipment`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `equipment_details`
--
ALTER TABLE `equipment_details`
  ADD CONSTRAINT `equipment_details_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category_types` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `equipment_details_ibfk_2` FOREIGN KEY (`type`) REFERENCES `equipment_types` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `packages`
--
ALTER TABLE `packages`
  ADD CONSTRAINT `packages_ibfk_1` FOREIGN KEY (`template_id`) REFERENCES `templates` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `package_equipment`
--
ALTER TABLE `package_equipment`
  ADD CONSTRAINT `package_equipment_ibfk_1` FOREIGN KEY (`package_id`) REFERENCES `packages` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `package_equipment_ibfk_2` FOREIGN KEY (`equipment_id`) REFERENCES `equipment_details` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `template_equipment`
--
ALTER TABLE `template_equipment`
  ADD CONSTRAINT `template_equipment_ibfk_1` FOREIGN KEY (`template_id`) REFERENCES `templates` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `template_equipment_ibfk_2` FOREIGN KEY (`equipment_id`) REFERENCES `equipment_details` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
