-- phpMyAdmin SQL Dump
-- version 5.0.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 26, 2023 at 03:34 PM
-- Server version: 10.4.17-MariaDB
-- PHP Version: 7.4.15

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_supashopa`
--

-- --------------------------------------------------------

--
-- Table structure for table `order_payment_status`
--

CREATE TABLE `order_payment_status` (
  `id` int(11) NOT NULL,
  `payment status` varchar(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `order_payment_status`
--

INSERT INTO `order_payment_status` (`id`, `payment status`) VALUES
(0, 'not paid'),
(1, 'paid');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_orders_status`
--

CREATE TABLE `tbl_orders_status` (
  `status` varchar(25) NOT NULL,
  `code` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tbl_orders_status`
--

INSERT INTO `tbl_orders_status` (`status`, `code`) VALUES
('pending', 0),
('being processed', 1),
('processed', 2);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_shops`
--

CREATE TABLE `tbl_shops` (
  `shop code` int(11) NOT NULL,
  `shop name` varchar(50) NOT NULL,
  `shop description` varchar(300) NOT NULL,
  `shop rating` float NOT NULL,
  `shop image URL` varchar(250) NOT NULL,
  `database name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tbl_shops`
--

INSERT INTO `tbl_shops` (`shop code`, `shop name`, `shop description`, `shop rating`, `shop image URL`, `database name`) VALUES
(1, 'Naivas Supermarket', 'NEXT TO MAGUNAS SUPERMARKET', 3.99, 'naivas supermarket.jpg', 'db_naivas_supermarket'),
(3, 'Ndungu spices', 'Dealers in food stuff annd best quality spices', 4.3, 'ndungu spices.jpg', 'db_ndungu_spices'),
(4, 'magunas supermarket pangani', 'Location: PRPQ+36M, Northview Rd, Pangani, Nairobi\r\nContacts: 0797884345\r\nWorking hours: 7.30 am – 9 pm (Monday to Saturday) 8 am – 9 pm (Sunday)\r\n\r\nDo all your household shopping, from fresh fruits and vegetables to manufactured foods, drinks, home appliances and kitchenware.', 3.9, 'magunas supermarket pangani.jpg', 'db_magunas_supermarket_pangani'),
(5, 'DESTINY BEST SUPERMARKET GATAKA', 'located along gataka road at gataka market. \r\nAffordable prices friendly to your pocket and mind blowing offers.\r\nwe sell food stuff, kitchen ware, bakery products, liquor and everything in between. very convenient access and ample parking.\r\nopen from 6.00 am- 9.00 pm Monday to Sunday', 3.9, 'DESTINY BEST SUPERMARKET GATAKA.jpg', 'db_destiny_gataka_supermarket');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_users`
--

CREATE TABLE `tbl_users` (
  `user id` int(11) NOT NULL,
  `first name` varchar(25) NOT NULL,
  `last name` varchar(25) NOT NULL,
  `phone number` varchar(15) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `login status` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tbl_users`
--

INSERT INTO `tbl_users` (`user id`, `first name`, `last name`, `phone number`, `email`, `password`, `login status`) VALUES
(1, 'chao', 'njei', '0787654321', 'chaonjei@gmail.com', '1234', 0),
(2, 'musa', 'ngari', '0712435677', 'musangari@gmail.com', '1234', 0),
(3, 'Peter', 'Nyaga', '0700000000', 'pterngari@gmail.com', '81dc9bdb52d04dc20036dbd8313ed055', 0),
(4, 'Peter', 'Nyaga', '0716689703', 'nyashngari@gmail.com', '81dc9bdb52d04dc20036dbd8313ed055', 0),
(5, 'jomo', 'kenyatta', '0719201974', 'jomokenyatta@gmail.com', '81dc9bdb52d04dc20036dbd8313ed055', 0),
(12, 'Francis', 'Njihia', '0719103438', 'njihiahfrankh@gmail.com', '525ff5a7e04e68579151760fd3587af0', 0),
(13, 'Peter', 'Muriithi', '0717485634', 'petersonnmuriithi0@gmail.com', 'cc03e747a6afbbcbf8be7668acfebee5', 0),
(14, 'Davey', 'Nguch', '0723639971', 'daveynguch@gmail.com', 'd8578edf8458ce06fbc5bb76a58c5ca4', 0),
(15, 'Chao', 'Jei', '0791050430', 'chaojeik@gmail.com', 'c7882c34ee29c8a4740220406d0344d6', 0),
(16, 'cha', 'nje', '0712345678', 'chanje@gmail.com', '81dc9bdb52d04dc20036dbd8313ed055', 0),
(23, 'fgg', 'hcvv', '898', 'nyahngari@gmail.com', 'c4ca4238a0b923820dcc509a6f75849b', 0),
(24, 'Regan', 'Mugendi', '0743307222', 'mugendiregan087@gmail.com', 'fcea920f7412b5da7be0cf42b8c93759', 0),
(27, 'Peter', 'Nyaga', '0716000000', 'pngari@gmail.com', '81dc9bdb52d04dc20036dbd8313ed055', 0),
(28, 'Francis', 'Gikonyo', '0734981692', 'wanderifranje@gmail.com', 'b8fa911966e1dea48e2fd744615c910c', 0),
(29, 'Daniel', 'Maina', '0790145233', 'mwangidanielmaina17@gmail.com', 'e807f1fcf82d132f9bb018ca6738a19f', 0),
(30, 'peter', 'nyaga', '0716689707', 'nyashngary@gmail.com', '81dc9bdb52d04dc20036dbd8313ed055', 0),
(31, 'peter', 'nyaga', '0716689701', 'nyashngaryn@gmail.com', '81dc9bdb52d04dc20036dbd8313ed055', 0),
(32, 'Dennis', 'mugambi', '0716689702', 'nyashngeri@gmail.com', '4a7d1ed414474e4033ac29ccb8653d9b', 0),
(34, 'david', 'gitonga', '0727447182', 'dgitonga13@gmail.com', '085f97b800961a974875bd64cbea787f', 0),
(35, 'eugene', 'gene', '0793991007', 'mainaeugene1999@gmail.com', '16c5e4ebb90de9e7b93b0c17e5356f46', 0),
(36, 'Grace', 'Wambui', '0791732130', 'gracewambui719@gmail.com', '38f5e3e1bc6c54ae14092f3285fe58f1', 0);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_user_log_status`
--

CREATE TABLE `tbl_user_log_status` (
  `code` int(11) NOT NULL,
  `status` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tbl_user_log_status`
--

INSERT INTO `tbl_user_log_status` (`code`, `status`) VALUES
(1, 'logged in'),
(0, 'logged out');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `order_payment_status`
--
ALTER TABLE `order_payment_status`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tbl_orders_status`
--
ALTER TABLE `tbl_orders_status`
  ADD PRIMARY KEY (`code`);

--
-- Indexes for table `tbl_shops`
--
ALTER TABLE `tbl_shops`
  ADD PRIMARY KEY (`shop code`);

--
-- Indexes for table `tbl_users`
--
ALTER TABLE `tbl_users`
  ADD PRIMARY KEY (`user id`) USING BTREE,
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `USER ID` (`user id`),
  ADD UNIQUE KEY `phone number` (`phone number`),
  ADD KEY `fk_log_status` (`login status`);

--
-- Indexes for table `tbl_user_log_status`
--
ALTER TABLE `tbl_user_log_status`
  ADD PRIMARY KEY (`code`),
  ADD UNIQUE KEY `unique_log_status` (`status`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbl_shops`
--
ALTER TABLE `tbl_shops`
  MODIFY `shop code` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `tbl_users`
--
ALTER TABLE `tbl_users`
  MODIFY `user id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tbl_users`
--
ALTER TABLE `tbl_users`
  ADD CONSTRAINT `fk_log_status` FOREIGN KEY (`login status`) REFERENCES `tbl_user_log_status` (`code`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
