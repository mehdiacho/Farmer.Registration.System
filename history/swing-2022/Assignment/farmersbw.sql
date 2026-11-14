-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 31, 2022 at 12:31 PM
-- Server version: 10.4.25-MariaDB
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `farmersbw`
--

-- --------------------------------------------------------

--
-- Table structure for table `farmers`
--

CREATE TABLE `farmers` (
  `farmer_id` int(9) NOT NULL,
  `first_name` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_name` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact` int(8) NOT NULL,
  `date_of_birth` varchar(11) COLLATE utf8mb4_unicode_ci NOT NULL,
  `gender` varchar(6) COLLATE utf8mb4_unicode_ci NOT NULL,
  `postal_address` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `district` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sub_district` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email_address` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `farm_size` int(11) NOT NULL,
  `farming_area` int(11) NOT NULL,
  `registration_status` varchar(12) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `farmers`
--

INSERT INTO `farmers` (`farmer_id`, `first_name`, `last_name`, `contact`, `date_of_birth`, `gender`, `postal_address`, `district`, `sub_district`, `email_address`, `farm_size`, `farming_area`, `registration_status`) VALUES
(100000001, 'Kagiso', 'Molefe', 71000001, '14-Mar-1990', 'Male', 'PO Box 101', 'South-East', 'Gaborone', 'kagiso@example.com', 450, 400, 'Pending'),
(100000002, 'Naledi', 'Sebina', 72000002, '02-Jul-1985', 'Female', 'Private Bag 202', 'South-East', 'Gaborone', 'naledi@example.com', 100, 50, 'Approved'),
(100000003, 'Thato', 'Kgosi', 73000003, '21-Jan-1979', 'Male', 'PO Box 303', 'Central', 'Tutume', 'thato@example.com', 60, 55, 'Approved');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `username` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email_address` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_admin` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`username`, `email_address`, `password`, `is_admin`) VALUES
('Kagiso Molefe', 'kagiso@example.com', '[d, e, m, o]', 0),
('Naledi Sebina', 'naledi@example.com', '[d, e, m, o]', 1),
('Thato Kgosi', 'thato@example.com', '[d, e, m, o]', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `farmers`
--
ALTER TABLE `farmers`
  ADD PRIMARY KEY (`farmer_id`),
  ADD UNIQUE KEY `email_address` (`email_address`),
  ADD UNIQUE KEY `contact` (`contact`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`email_address`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
