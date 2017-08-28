-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb1
-- http://www.phpmyadmin.net
--
-- Хост: localhost
-- Время создания: Май 06 2013 г., 23:10
-- Версия сервера: 5.5.31
-- Версия PHP: 5.4.6-1ubuntu1.2

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- База данных: `categories_db`
--

-- --------------------------------------------------------

--
-- Структура таблицы `document`
--

CREATE TABLE IF NOT EXISTS `document` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'document id',
  `description` text NOT NULL,
  `file_path` text NOT NULL,
  `hash` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- Структура таблицы `ref_doc_term`
--

CREATE TABLE IF NOT EXISTS `ref_doc_term` (
  `id` int(255) unsigned NOT NULL AUTO_INCREMENT,
  `document_ref` int(255) unsigned NOT NULL,
  `term_ref` int(255) unsigned NOT NULL,
  `frequency` int(15) unsigned DEFAULT NULL,
  `weight` double unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2487 ;

-- --------------------------------------------------------

--
-- Структура таблицы `term`
--

CREATE TABLE IF NOT EXISTS `term` (
  `id` int(15) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `count` int(255) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2485 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
