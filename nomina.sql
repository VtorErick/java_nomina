-- phpMyAdmin SQL Dump
-- version 4.7.7
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost:3306
-- Tiempo de generación: 14-10-2018 a las 08:09:24
-- Versión del servidor: 5.6.38
-- Versión de PHP: 7.1.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `nomina`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nomina_banks`
--

CREATE TABLE `nomina_banks` (
  `bank_id` int(11) NOT NULL,
  `bank_name` varchar(250) NOT NULL,
  `is_active` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `nomina_banks`
--

INSERT INTO `nomina_banks` (`bank_id`, `bank_name`, `is_active`) VALUES
(1, 'Bancomer', 1),
(2, 'Banamex', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nomina_countries`
--

CREATE TABLE `nomina_countries` (
  `country_id` int(11) NOT NULL,
  `country_name` varchar(250) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `nomina_countries`
--

INSERT INTO `nomina_countries` (`country_id`, `country_name`) VALUES
(1, 'México');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nomina_deductions`
--

CREATE TABLE `nomina_deductions` (
  `deduction_id` int(11) NOT NULL,
  `deduction_name` varchar(250) NOT NULL,
  `deduction_price` float NOT NULL,
  `deduction_type` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `nomina_deductions`
--

INSERT INTO `nomina_deductions` (`deduction_id`, `deduction_name`, `deduction_price`, `deduction_type`) VALUES
(1, 'IVA', 0.16, 1),
(2, 'ISR', 0.03, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nomina_employe_deductions`
--

CREATE TABLE `nomina_employe_deductions` (
  `register_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `deduction_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nomina_permissions`
--

CREATE TABLE `nomina_permissions` (
  `permission_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nomina_profiles`
--

CREATE TABLE `nomina_profiles` (
  `profile_id` int(11) NOT NULL,
  `profile_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `nomina_profiles`
--

INSERT INTO `nomina_profiles` (`profile_id`, `profile_name`) VALUES
(1, 'Administrador'),
(2, 'Usuario del sistema'),
(3, 'Usuario de nómina');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nomina_profile_permission`
--

CREATE TABLE `nomina_profile_permission` (
  `id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL DEFAULT '0',
  `profile_id` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nomina_users`
--

CREATE TABLE `nomina_users` (
  `user_id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `lastname` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `profile_id` int(11) NOT NULL DEFAULT '0',
  `username` varchar(50) NOT NULL,
  `email` varchar(250) NOT NULL,
  `is_active` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `nomina_users`
--

INSERT INTO `nomina_users` (`user_id`, `name`, `lastname`, `password`, `profile_id`, `username`, `email`, `is_active`) VALUES
(1, 'Dante Iván', 'Cervantes Gómez', '8cb2237d0679ca88db6464eac60da96345513964', 1, 'dcergo', 'contacto@dantecervantes.com', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nomina_user_details`
--

CREATE TABLE `nomina_user_details` (
  `record_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `payment_type` int(11) NOT NULL,
  `payment_amount` float NOT NULL,
  `curp` varchar(75) NOT NULL,
  `birthdate` date NOT NULL,
  `rfc` varchar(50) DEFAULT NULL,
  `country` int(11) NOT NULL,
  `state` varchar(250) NOT NULL,
  `address` varchar(400) NOT NULL,
  `cp` int(11) NOT NULL,
  `job_position` text NOT NULL,
  `bank_id` int(11) NOT NULL,
  `bank_clabe` varchar(250) DEFAULT NULL,
  `user_image` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `nomina_banks`
--
ALTER TABLE `nomina_banks`
  ADD PRIMARY KEY (`bank_id`);

--
-- Indices de la tabla `nomina_countries`
--
ALTER TABLE `nomina_countries`
  ADD PRIMARY KEY (`country_id`);

--
-- Indices de la tabla `nomina_deductions`
--
ALTER TABLE `nomina_deductions`
  ADD PRIMARY KEY (`deduction_id`);

--
-- Indices de la tabla `nomina_employe_deductions`
--
ALTER TABLE `nomina_employe_deductions`
  ADD PRIMARY KEY (`register_id`);

--
-- Indices de la tabla `nomina_permissions`
--
ALTER TABLE `nomina_permissions`
  ADD PRIMARY KEY (`permission_id`);

--
-- Indices de la tabla `nomina_profiles`
--
ALTER TABLE `nomina_profiles`
  ADD PRIMARY KEY (`profile_id`);

--
-- Indices de la tabla `nomina_profile_permission`
--
ALTER TABLE `nomina_profile_permission`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `nomina_users`
--
ALTER TABLE `nomina_users`
  ADD PRIMARY KEY (`user_id`);

--
-- Indices de la tabla `nomina_user_details`
--
ALTER TABLE `nomina_user_details`
  ADD PRIMARY KEY (`record_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `nomina_banks`
--
ALTER TABLE `nomina_banks`
  MODIFY `bank_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `nomina_countries`
--
ALTER TABLE `nomina_countries`
  MODIFY `country_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `nomina_deductions`
--
ALTER TABLE `nomina_deductions`
  MODIFY `deduction_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `nomina_employe_deductions`
--
ALTER TABLE `nomina_employe_deductions`
  MODIFY `register_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `nomina_permissions`
--
ALTER TABLE `nomina_permissions`
  MODIFY `permission_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `nomina_profiles`
--
ALTER TABLE `nomina_profiles`
  MODIFY `profile_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `nomina_profile_permission`
--
ALTER TABLE `nomina_profile_permission`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `nomina_users`
--
ALTER TABLE `nomina_users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT de la tabla `nomina_user_details`
--
ALTER TABLE `nomina_user_details`
  MODIFY `record_id` int(11) NOT NULL AUTO_INCREMENT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
